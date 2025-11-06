// Copyright 2025 Google LLC
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
