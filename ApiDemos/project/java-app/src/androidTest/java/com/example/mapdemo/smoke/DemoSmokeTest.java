// Copyright 2026 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.mapdemo.smoke;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assume.assumeTrue;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.viewpager.widget.ViewPager;
import com.example.common_ui.R;
import com.example.mapdemo.BuildConfig;
import com.example.mapdemo.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * End-to-end smoke test covering every demo in the app.
 *
 * <p>The parameter list is every activity the manifest declares in the app's package except
 * {@link MainActivity}, so a demo added to the app is covered here with no change to this file.
 *
 * <p>Each demo is checked for these things:
 * <ul>
 *   <li>it reaches {@link Lifecycle.State#RESUMED} without throwing,</li>
 *   <li>a map or Street View panorama is attached, laid out and ready,</li>
 *   <li>its map survives a zoom out and back in,</li>
 *   <li>it survives a configuration change,</li>
 *   <li>nothing crashes on a background thread while it is open.</li>
 * </ul>
 *
 * <p>This deliberately does not assert on map content. Checking a particular marker or overlay
 * belongs in the focused tests next to this one; the value here is breadth.
 */
@RunWith(Parameterized.class)
public class DemoSmokeTest {

    /**
     * How long to wait for a demo's map to be laid out and ready. Generous because the first demo
     * to run on a cold emulator pays for Maps SDK initialization.
     */
    private static final long MAP_READY_TIMEOUT_MS = 20_000L;

    private static final long POLL_INTERVAL_MS = 250L;

    /** How long to wait for tiles to render when {@code requireMapLoaded} is set. */
    private static final long MAP_LOADED_TIMEOUT_MS = 30_000L;

    /** Time given to a demo to react to a camera change before the next check. */
    private static final long SETTLE_MS = 1_500L;

    /**
     * Demos that legitimately show no map of their own. Keep this empty unless a demo really is
     * map-free; an entry here is a hole in the coverage, not a fix.
     */
    private static final Set<String> DEMOS_WITHOUT_MAP = Collections.emptySet();

    /** Demos that close themselves when no map ID is configured, see the README. */
    private static final Set<String> DEMOS_REQUIRING_MAP_ID = new HashSet<>(Arrays.asList(
        "AdvancedMarkersDemoActivity",
        "DataDrivenBoundariesActivity",
        "DataDrivenDatasetStylingActivity"));

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> demos() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PackageInfo packageInfo = context.getPackageManager()
            .getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        List<String> names = new ArrayList<>();
        if (packageInfo.activities != null) {
            for (ActivityInfo activity : packageInfo.activities) {
                if (activity.name.startsWith(context.getPackageName() + ".")
                    && !activity.name.equals(MainActivity.class.getName())) {
                    names.add(activity.name);
                }
            }
        }
        Collections.sort(names);

        List<Object[]> demos = new ArrayList<>();
        for (String name : names) {
            demos.add(new Object[] {
                name.substring(name.lastIndexOf('.') + 1),
                Class.forName(name).asSubclass(Activity.class),
            });
        }
        return demos;
    }

    /** The location demos ask for this on start; a permission dialog would cover the map. */
    @Rule
    public final GrantPermissionRule permissions = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION);

    private final String demoName;
    private final Class<? extends Activity> demoActivity;

    /**
     * Tiles only render with a real key, so waiting for them is opt-in: pass
     * {@code -Pandroid.testInstrumentationRunnerArguments.requireMapLoaded=true}.
     */
    private final boolean requireMapLoaded = Boolean.parseBoolean(
        InstrumentationRegistry.getArguments().getString("requireMapLoaded"));

    private final List<Throwable> uncaughtExceptions = new CopyOnWriteArrayList<>();
    private Thread.UncaughtExceptionHandler defaultHandler;

    public DemoSmokeTest(String demoName, Class<? extends Activity> demoActivity) {
        this.demoName = demoName;
        this.demoActivity = demoActivity;
    }

    @Before
    public void setUp() {
        if (DEMOS_REQUIRING_MAP_ID.contains(demoName)) {
            assumeTrue("Map ID not specified", hasMapId());
        }

        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        defaultHandler = handler;
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            uncaughtExceptions.add(throwable);
            // The main looper cannot recover from a crash, so let it take the process down: the
            // orchestrator reports it against this test and carries on with the next one.
            if (thread == Looper.getMainLooper().getThread() && handler != null) {
                handler.uncaughtException(thread, throwable);
            }
        });
    }

    /** Also catches crashes while the demo is torn down after the test body. */
    @After
    public void tearDown() {
        try {
            assertNoUncaughtExceptions();
        } finally {
            // Only restore what setUp replaced: if the map ID check skipped the test, restoring
            // null would disable crash reporting for later tests.
            if (defaultHandler != null) {
                Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
            }
        }
    }

    /** Mirrors ApiDemoApplication.getMapId(), which cannot be called here as it shows a toast. */
    private static boolean hasMapId() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        return !BuildConfig.MAP_ID.equals("MAP_ID")
            || !context.getString(R.string.map_id).equals("DEMO_MAP_ID");
    }

    @Test
    public void demoLaunchesAndShowsMap() throws InterruptedException {
        try (ActivityScenario<? extends Activity> scenario = ActivityScenario.launch(demoActivity)) {
            assertResumed(scenario);
            if (!DEMOS_WITHOUT_MAP.contains(demoName)) {
                GoogleMap map = awaitMapSurfaceReady(scenario);
                if (map != null) {
                    zoomOutAndBackIn(scenario, map);
                }
            }
            assertResumed(scenario);
            assertNoUncaughtExceptions();
        }
    }

    /**
     * Rotation and other configuration changes are where saved state and retained fragments tend
     * to regress, and they are easy to miss when clicking through the app by hand.
     */
    @Test
    public void demoSurvivesConfigurationChange() throws InterruptedException {
        try (ActivityScenario<? extends Activity> scenario = ActivityScenario.launch(demoActivity)) {
            assertResumed(scenario);
            if (!DEMOS_WITHOUT_MAP.contains(demoName)) {
                awaitMapSurfaceReady(scenario);
            }

            scenario.recreate();

            assertResumed(scenario);
            if (!DEMOS_WITHOUT_MAP.contains(demoName)) {
                awaitMapSurfaceReady(scenario);
            }
            assertNoUncaughtExceptions();
        }
    }

    private static void assertResumed(ActivityScenario<? extends Activity> scenario) {
        assertThat(scenario.getState()).isEqualTo(Lifecycle.State.RESUMED);
    }

    /**
     * Waits until the demo shows a map or panorama that is laid out on screen, then until the
     * Maps SDK reports it ready. Returns the map to zoom, or null when the demo only shows Street
     * View. A map fully on screen is preferred: in a scrolling list of maps, one partly off screen
     * may never report that it finished loading.
     */
    @Nullable
    private GoogleMap awaitMapSurfaceReady(ActivityScenario<? extends Activity> scenario)
        throws InterruptedException {
        long deadline = System.currentTimeMillis() + MAP_READY_TIMEOUT_MS;
        List<MapSurface> surfaces = new ArrayList<>();
        List<MapSurface> shown = new ArrayList<>();

        while (shown.isEmpty() && System.currentTimeMillis() < deadline) {
            scenario.onActivity(activity -> {
                surfaces.clear();
                surfaces.addAll(mapSurfaces(activity));
                shown.clear();
                for (MapSurface surface : surfaces) {
                    if (surface.view != null && isLaidOutOnScreen(surface.view)) {
                        shown.add(surface);
                    }
                }
                // Demos such as MapInPager open on a page without a map: swipe on to find it.
                if (shown.isEmpty()) {
                    turnPagerPage(activity.getWindow().getDecorView());
                }
            });
            if (shown.isEmpty()) {
                Thread.sleep(POLL_INTERVAL_MS);
            }
        }
        if (shown.isEmpty()) {
            StringBuilder detail = new StringBuilder();
            if (surfaces.isEmpty()) {
                detail.append("no map or Street View panorama was found");
            } else {
                detail.append("found but not laid out:");
                for (MapSurface surface : surfaces) {
                    detail.append(' ').append(surface.name);
                }
            }
            throw new AssertionError(demoName + " did not show a map within "
                + MAP_READY_TIMEOUT_MS + "ms: " + detail);
        }

        MapSurface surface = pickSurface(shown);
        AtomicReference<GoogleMap> map = new AtomicReference<>();
        CountDownLatch ready = new CountDownLatch(1);
        scenario.onActivity(activity -> surface.awaitReady.accept(readyMap -> {
            map.set(readyMap);
            ready.countDown();
        }));
        assertWithMessage(demoName + " " + surface.name + " was not ready within "
            + MAP_READY_TIMEOUT_MS + "ms")
            .that(ready.await(MAP_READY_TIMEOUT_MS, TimeUnit.MILLISECONDS))
            .isTrue();
        return map.get();
    }

    private static MapSurface pickSurface(List<MapSurface> shown) {
        for (MapSurface surface : shown) {
            if (surface.isMap && isFullyOnScreen(surface.view)) {
                return surface;
            }
        }
        for (MapSurface surface : shown) {
            if (surface.isMap) {
                return surface;
            }
        }
        return shown.get(0);
    }

    /**
     * Zooms the map out and back in. When {@link #requireMapLoaded} is set, also waits for the
     * tiles to render each time. This replaces the demo's own map loaded callback, if it had one,
     * for the rest of the test.
     */
    private void zoomOutAndBackIn(ActivityScenario<? extends Activity> scenario, GoogleMap map)
        throws InterruptedException {
        for (float delta : new float[] {-1f, 1f}) {
            CountDownLatch loaded = new CountDownLatch(1);
            scenario.onActivity(activity -> {
                if (requireMapLoaded) {
                    map.setOnMapLoadedCallback(loaded::countDown);
                }
                map.moveCamera(CameraUpdateFactory.zoomBy(delta));
            });
            if (requireMapLoaded) {
                assertWithMessage(demoName + " map did not finish loading after zooming")
                    .that(loaded.await(MAP_LOADED_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                    .isTrue();
            }
            Thread.sleep(SETTLE_MS);
        }
    }

    private void assertNoUncaughtExceptions() {
        if (!uncaughtExceptions.isEmpty()) {
            throw new AssertionError(demoName + " crashed on a background thread",
                uncaughtExceptions.get(0));
        }
    }

    /**
     * A map or panorama shown by a demo, either as a view or as a fragment. {@link #awaitReady}
     * calls back with the {@link GoogleMap} for a map, or with null for a panorama once it is
     * ready.
     */
    private static final class MapSurface {
        final String name;
        @Nullable final View view;
        final boolean isMap;
        final Consumer<Consumer<GoogleMap>> awaitReady;

        MapSurface(String name, @Nullable View view, boolean isMap,
                   Consumer<Consumer<GoogleMap>> awaitReady) {
            this.name = name;
            this.view = view;
            this.isMap = isMap;
            this.awaitReady = awaitReady;
        }
    }

    /** Every map and panorama in the activity, from its views and its fragments. */
    private static List<MapSurface> mapSurfaces(Activity activity) {
        List<MapSurface> surfaces = new ArrayList<>();
        walk(activity.getWindow().getDecorView(), surfaces);
        if (activity instanceof FragmentActivity) {
            for (Fragment fragment
                : ((FragmentActivity) activity).getSupportFragmentManager().getFragments()) {
                walk(fragment, surfaces);
            }
        }
        return surfaces;
    }

    private static void walk(View view, List<MapSurface> surfaces) {
        if (view instanceof MapView) {
            MapView mapView = (MapView) view;
            surfaces.add(new MapSurface("MapView", view, true,
                callback -> mapView.getMapAsync(callback::accept)));
        } else if (view instanceof StreetViewPanoramaView) {
            StreetViewPanoramaView panoramaView = (StreetViewPanoramaView) view;
            surfaces.add(new MapSurface("StreetViewPanoramaView", view, false,
                callback -> panoramaView.getStreetViewPanoramaAsync(
                    panorama -> callback.accept(null))));
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                walk(group.getChildAt(index), surfaces);
            }
        }
    }

    private static void walk(Fragment fragment, List<MapSurface> surfaces) {
        if (fragment instanceof SupportMapFragment) {
            SupportMapFragment mapFragment = (SupportMapFragment) fragment;
            surfaces.add(new MapSurface("SupportMapFragment", fragment.getView(), true,
                callback -> mapFragment.getMapAsync(callback::accept)));
        } else if (fragment instanceof SupportStreetViewPanoramaFragment) {
            SupportStreetViewPanoramaFragment panoramaFragment =
                (SupportStreetViewPanoramaFragment) fragment;
            surfaces.add(new MapSurface("SupportStreetViewPanoramaFragment", fragment.getView(),
                false, callback -> panoramaFragment.getStreetViewPanoramaAsync(
                    panorama -> callback.accept(null))));
        }
        for (Fragment child : fragment.getChildFragmentManager().getFragments()) {
            walk(child, surfaces);
        }
    }

    /** Moves the first pager below this view that has a page left to its next page. */
    private static boolean turnPagerPage(View view) {
        if (view instanceof ViewPager) {
            ViewPager pager = (ViewPager) view;
            int pageCount = pager.getAdapter() == null ? 0 : pager.getAdapter().getCount();
            if (pager.getCurrentItem() < pageCount - 1) {
                pager.setCurrentItem(pager.getCurrentItem() + 1);
                return true;
            }
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int index = 0; index < group.getChildCount(); index++) {
                if (turnPagerPage(group.getChildAt(index))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isLaidOutOnScreen(View view) {
        return view.isShown() && view.getWidth() > 0 && view.getHeight() > 0;
    }

    /** True when the view is shown and its whole area is visible on screen. */
    private static boolean isFullyOnScreen(View view) {
        if (!isLaidOutOnScreen(view)) {
            return false;
        }
        Rect visible = new Rect();
        return view.getGlobalVisibleRect(visible)
            && visible.width() == view.getWidth()
            && visible.height() == view.getHeight();
    }
}
