package com.example.mapdemo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.maps.model.CameraPosition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CameraDemoActivityTest {

    private MapIdlingResource idlingResource;
    private ActivityScenario<CameraDemoActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(CameraDemoActivity.class);
        scenario.onActivity(activity -> {
            idlingResource = new MapIdlingResource(activity.getMap());
            IdlingRegistry.getInstance().register(idlingResource);
        });
    }

    @Test
    public void testGoToBondi() {
        Espresso.onView(ViewMatchers.withText("Go to Bondi")).perform(ViewActions.click());

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        scenario.onActivity(activity -> {
            CameraPosition cameraPosition = activity.getMap().getCameraPosition();
            assertEquals(CameraDemoActivity.BONDI.target.latitude, cameraPosition.target.latitude, 1e-5);
            assertEquals(CameraDemoActivity.BONDI.target.longitude, cameraPosition.target.longitude, 1e-5);
        });
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
        scenario.close();
    }
}
