package com.example.mapdemo.utils;

import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.action.Tapper;
import androidx.test.espresso.matcher.ViewMatchers;
import com.example.common_ui.R;
import com.example.mapdemo.MapIdlingResource;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import org.junit.After;
import org.junit.Before;

public abstract class MapDemoActivityTest<T extends AppCompatActivity & MapProvider> {
    private final Class<T> activityClass;

    protected View mapView;
    protected GoogleMap map;
    protected MapIdlingResource idlingResource;
    protected ActivityScenario<T> scenario;

    public MapDemoActivityTest(Class<T> activityClass) {
        this.activityClass = activityClass;
    }

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(activityClass);
        scenario.onActivity(activity -> {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 5000; // 5 second timeout

            while (!activity.isMapReady() && System.currentTimeMillis() < endTime) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!activity.isMapReady()) {
                throw new RuntimeException("Map is not ready after 5 seconds");
            }

            map = activity.getMap();
            mapView = activity.findViewById(R.id.map);

            idlingResource = new MapIdlingResource(activity.getMap());
            IdlingRegistry.getInstance().register(idlingResource);
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        scenario.close();
    }

    protected void clickOnMapAt(LatLng latLng) {
        clickOnMapAt(latLng, Tap.SINGLE);
    }

    protected void clickOnMapAt(LatLng latLng, Tapper tapType) {
        clickOnMapAt(latLng, map, mapView, tapType);
    }

    protected void clickOnMapAt(LatLng latLng, GoogleMap map, View mapView, Tapper tapType) {
        final float[] coordinates = new float[2];
        scenario.onActivity(activity -> {
            int[] mapLocation = new int[2];
            mapView.getLocationOnScreen(mapLocation);

            android.graphics.Point screenPosition = map.getProjection().toScreenLocation(latLng);

            coordinates[0] = screenPosition.x + mapLocation[0];
            coordinates[1] = screenPosition.y + mapLocation[1];
        });

        GeneralClickAction clickAction = new GeneralClickAction(
            tapType,
            view -> coordinates,
            Press.FINGER,
            InputDevice.SOURCE_UNKNOWN,
            MotionEvent.BUTTON_PRIMARY
        );
        Espresso.onView(ViewMatchers.withId(R.id.map)).perform(clickAction);
    }
}
