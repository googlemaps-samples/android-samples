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
package com.example.kotlindemos.smoke

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.viewpager.widget.ViewPager
import com.example.common_ui.R
import com.example.kotlindemos.BuildConfig
import com.example.kotlindemos.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.StreetViewPanoramaView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * End-to-end smoke test covering every demo in the app.
 *
 * The parameter list is every activity the manifest declares in the app's package except
 * [MainActivity], so a demo added to the app is covered here with no change to this file.
 *
 * Each demo is checked for these things:
 * - it reaches [Lifecycle.State.RESUMED] without throwing,
 * - a map or Street View panorama is attached, laid out and ready,
 * - its map survives a zoom out and back in,
 * - it survives a configuration change,
 * - nothing crashes on a background thread while it is open.
 *
 * This deliberately does not assert on map content. Checking a particular marker or overlay
 * belongs in the focused tests next to this one; the value here is breadth.
 */
@RunWith(Parameterized::class)
class DemoSmokeTest(
    private val demoName: String,
    private val demoActivity: Class<out Activity>,
) {

    companion object {
        /**
         * How long to wait for a demo's map to be laid out and ready. Generous because the first
         * demo to run on a cold emulator pays for Maps SDK initialization.
         */
        private const val MAP_READY_TIMEOUT_MS = 20_000L

        private const val POLL_INTERVAL_MS = 250L

        /** How long to wait for tiles to render when `requireMapLoaded` is set. */
        private const val MAP_LOADED_TIMEOUT_MS = 30_000L

        /** Time given to a demo to react to a camera change before the next check. */
        private const val SETTLE_MS = 1_500L

        /**
         * Demos that legitimately show no map of their own. Keep this empty unless a demo really
         * is map-free; an entry here is a hole in the coverage, not a fix.
         */
        private val DEMOS_WITHOUT_MAP = emptySet<String>()

        /** Demos that close themselves when no map ID is configured, see the README. */
        private val DEMOS_REQUIRING_MAP_ID = setOf(
            "AdvancedMarkersDemoActivity",
            "DataDrivenBoundariesActivity",
            "DataDrivenDatasetStylingActivity",
        )

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun demos(): List<Array<Any>> {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_ACTIVITIES,
            )
            return packageInfo.activities.orEmpty()
                .map { it.name }
                .filter { it.startsWith("${context.packageName}.") }
                .filter { it != MainActivity::class.java.name }
                .sorted()
                .map { name ->
                    arrayOf(
                        name.substringAfterLast('.'),
                        Class.forName(name).asSubclass(Activity::class.java),
                    )
                }
        }
    }

    /** The location demos ask for this on start; a permission dialog would cover the map. */
    @get:Rule
    val permissions: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    /**
     * Tiles only render with a real key, so waiting for them is opt-in: pass
     * `-Pandroid.testInstrumentationRunnerArguments.requireMapLoaded=true`.
     */
    private val requireMapLoaded =
        InstrumentationRegistry.getArguments().getString("requireMapLoaded").toBoolean()

    private val uncaughtExceptions = CopyOnWriteArrayList<Throwable>()
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null

    @Before
    fun setUp() {
        if (demoName in DEMOS_REQUIRING_MAP_ID) {
            assumeTrue("Map ID not specified", hasMapId())
        }

        val handler = Thread.getDefaultUncaughtExceptionHandler()
        defaultHandler = handler
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            uncaughtExceptions.add(throwable)
            // The main looper cannot recover from a crash, so let it take the process down: the
            // orchestrator reports it against this test and carries on with the next one.
            if (thread == Looper.getMainLooper().thread) handler?.uncaughtException(thread, throwable)
        }
    }

    /** Also catches crashes while the demo is torn down after the test body. */
    @After
    fun tearDown() {
        try {
            assertNoUncaughtExceptions()
        } finally {
            // Only restore what setUp replaced: if the map ID check skipped the test, restoring
            // null would disable crash reporting for later tests.
            defaultHandler?.let { Thread.setDefaultUncaughtExceptionHandler(it) }
        }
    }

    /** Mirrors ApiDemoApplication.mapId, which cannot be read here because it shows a toast. */
    private fun hasMapId(): Boolean {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return BuildConfig.MAP_ID != "MAP_ID" || context.getString(R.string.map_id) != "DEMO_MAP_ID"
    }

    @Test
    fun demoLaunchesAndShowsMap() {
        ActivityScenario.launch(demoActivity).use { scenario ->
            scenario.assertResumed()
            if (demoName !in DEMOS_WITHOUT_MAP) {
                val map = scenario.awaitMapSurfaceReady()
                if (map != null) scenario.zoomOutAndBackIn(map)
            }
            scenario.assertResumed()
            assertNoUncaughtExceptions()
        }
    }

    /**
     * Rotation and other configuration changes are where saved state and retained fragments tend
     * to regress, and they are easy to miss when clicking through the app by hand.
     */
    @Test
    fun demoSurvivesConfigurationChange() {
        ActivityScenario.launch(demoActivity).use { scenario ->
            scenario.assertResumed()
            if (demoName !in DEMOS_WITHOUT_MAP) scenario.awaitMapSurfaceReady()

            scenario.recreate()

            scenario.assertResumed()
            if (demoName !in DEMOS_WITHOUT_MAP) scenario.awaitMapSurfaceReady()
            assertNoUncaughtExceptions()
        }
    }

    private fun ActivityScenario<out Activity>.assertResumed() {
        assertThat(state).isEqualTo(Lifecycle.State.RESUMED)
    }

    /**
     * Waits until the demo shows a map or panorama that is laid out on screen, then until the
     * Maps SDK reports it ready. Returns the map to zoom, or null when the demo only shows Street
     * View. A map fully on screen is preferred: in a scrolling list of maps, one partly off
     * screen may never report that it finished loading.
     */
    private fun ActivityScenario<out Activity>.awaitMapSurfaceReady(): GoogleMap? {
        val deadline = System.currentTimeMillis() + MAP_READY_TIMEOUT_MS
        var surfaces = emptyList<MapSurface>()
        var shown = emptyList<MapSurface>()

        while (shown.isEmpty() && System.currentTimeMillis() < deadline) {
            onActivity { activity ->
                surfaces = activity.mapSurfaces()
                shown = surfaces.filter { it.view?.isLaidOutOnScreen() == true }
                // Demos such as MapInPager open on a page without a map: swipe on to find it.
                if (shown.isEmpty()) activity.window.decorView.turnPagerPage()
            }
            if (shown.isEmpty()) Thread.sleep(POLL_INTERVAL_MS)
        }
        if (shown.isEmpty()) {
            val detail = if (surfaces.isEmpty()) {
                "no map or Street View panorama was found"
            } else {
                surfaces.joinToString(prefix = "found but not laid out: ") { it.name }
            }
            throw AssertionError(
                "$demoName did not show a map within ${MAP_READY_TIMEOUT_MS}ms: $detail",
            )
        }

        val surface = shown.firstOrNull { it.isMap && it.view!!.isFullyOnScreen() }
            ?: shown.firstOrNull { it.isMap }
            ?: shown.first()
        var map: GoogleMap? = null
        val ready = CountDownLatch(1)
        onActivity {
            surface.awaitReady { readyMap ->
                map = readyMap
                ready.countDown()
            }
        }
        assertWithMessage("$demoName ${surface.name} was not ready within ${MAP_READY_TIMEOUT_MS}ms")
            .that(ready.await(MAP_READY_TIMEOUT_MS, TimeUnit.MILLISECONDS))
            .isTrue()
        return map
    }

    /**
     * Zooms the map out and back in. When [requireMapLoaded] is set, also waits for the tiles to
     * render each time. This replaces the demo's own map loaded callback, if it had one, for the
     * rest of the test.
     */
    private fun ActivityScenario<out Activity>.zoomOutAndBackIn(map: GoogleMap) {
        for (delta in floatArrayOf(-1f, 1f)) {
            val loaded = CountDownLatch(1)
            onActivity {
                if (requireMapLoaded) map.setOnMapLoadedCallback { loaded.countDown() }
                map.moveCamera(CameraUpdateFactory.zoomBy(delta))
            }
            if (requireMapLoaded) {
                assertWithMessage("$demoName map did not finish loading after zooming")
                    .that(loaded.await(MAP_LOADED_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                    .isTrue()
            }
            Thread.sleep(SETTLE_MS)
        }
    }

    private fun assertNoUncaughtExceptions() {
        val failure = uncaughtExceptions.firstOrNull() ?: return
        throw AssertionError("$demoName crashed on a background thread", failure)
    }

    /**
     * A map or panorama shown by a demo, either as a view or as a fragment. [awaitReady] calls
     * back with the [GoogleMap] for a map, or with null for a panorama once it is ready.
     */
    private class MapSurface(
        val name: String,
        val view: View?,
        val isMap: Boolean,
        val awaitReady: ((GoogleMap?) -> Unit) -> Unit,
    )

    private fun mapSurface(name: String, view: View?, getMapAsync: (OnMapReadyCallback) -> Unit) =
        MapSurface(name, view, isMap = true) { callback -> getMapAsync { callback(it) } }

    private fun panoramaSurface(
        name: String,
        view: View?,
        getPanoramaAsync: (() -> Unit) -> Unit,
    ) = MapSurface(name, view, isMap = false) { callback -> getPanoramaAsync { callback(null) } }

    /** Every map and panorama in the activity, from its views and its fragments. */
    private fun Activity.mapSurfaces(): List<MapSurface> = buildList {
        fun walk(view: View) {
            when (view) {
                is MapView -> add(mapSurface("MapView", view, view::getMapAsync))
                is StreetViewPanoramaView -> add(
                    panoramaSurface("StreetViewPanoramaView", view) { onReady ->
                        view.getStreetViewPanoramaAsync { onReady() }
                    },
                )
            }
            if (view is ViewGroup) {
                for (index in 0 until view.childCount) walk(view.getChildAt(index))
            }
        }

        fun walk(fragment: Fragment) {
            when (fragment) {
                is SupportMapFragment -> add(
                    mapSurface("SupportMapFragment", fragment.view, fragment::getMapAsync),
                )
                is SupportStreetViewPanoramaFragment -> add(
                    panoramaSurface("SupportStreetViewPanoramaFragment", fragment.view) { onReady ->
                        fragment.getStreetViewPanoramaAsync { onReady() }
                    },
                )
            }
            fragment.childFragmentManager.fragments.forEach(::walk)
        }

        walk(window.decorView)
        if (this@mapSurfaces is FragmentActivity) {
            supportFragmentManager.fragments.forEach(::walk)
        }
    }

    /** Moves the first pager below this view that has a page left to its next page. */
    private fun View.turnPagerPage(): Boolean {
        if (this is ViewPager && currentItem < (adapter?.count ?: 0) - 1) {
            currentItem += 1
            return true
        }
        return this is ViewGroup && (0 until childCount).any { getChildAt(it).turnPagerPage() }
    }

    private fun View.isLaidOutOnScreen(): Boolean = isShown && width > 0 && height > 0

    /** True when this view is shown and its whole area is visible on screen. */
    private fun View.isFullyOnScreen(): Boolean {
        if (!isLaidOutOnScreen()) return false
        val visible = Rect()
        return getGlobalVisibleRect(visible) &&
            visible.width() == width &&
            visible.height() == height
    }
}
