/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.common_ui.R
import com.google.android.material.appbar.MaterialToolbar
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BasicMapReviewerUiTest {

    @Test
    fun testBasicMapHasReviewerToolbarActions() {
        val scenario = ActivityScenario.launch(BasicMapDemoActivity::class.java)

        scenario.onActivity { activity ->
            val toolbar = activity.findViewById<MaterialToolbar>(R.id.top_bar)
            assertNotNull("Top bar must be present in layout", toolbar)

            // Verify action bar is attached to the toolbar
            val actionBar = activity.supportActionBar
            assertNotNull("SupportActionBar must be attached to MaterialToolbar", actionBar)

            // Verify menu items are present in toolbar
            val menu = toolbar.menu
            assertNotNull("Toolbar menu must not be null", menu)
            
            val infoItem = menu.findItem(2001)
            assertNotNull("Criteria & Purpose (id 2001) action button must exist in toolbar menu", infoItem)

            val goodJobItem = menu.findItem(2003)
            assertNotNull("Good Job (id 2003) action button must exist in toolbar menu", goodJobItem)

            val wrongItem = menu.findItem(2004)
            assertNotNull("Something's Wrong (id 2004) action button must exist in toolbar menu", wrongItem)
        }

        // Verify action icons are displayed and clickable on screen
        onView(withContentDescription("Criteria & Purpose")).check(matches(isDisplayed()))
        onView(withContentDescription("Good Job (Pass)")).check(matches(isDisplayed()))
        onView(withContentDescription("Something's Wrong")).check(matches(isDisplayed()))

        scenario.close()
    }
}
