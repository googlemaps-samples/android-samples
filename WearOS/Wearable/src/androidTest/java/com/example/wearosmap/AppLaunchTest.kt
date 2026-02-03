package com.example.wearosmap

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.example.wearosmap.kt.MainActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AppLaunchTest {

    @Test
    fun appLaunchesSuccessfully() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertThat(activity).isNotNull()
            }
        }
    }

    @Test
    fun mapContainerIsDisplayed() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            
            // Wait for the map container to appear
            val mapContainer = device.findObject(UiSelector().resourceId("com.example.wearos:id/map_container"))
            assertThat(mapContainer.waitForExists(5000)).isTrue()
        }
    }

    @Test
    fun markerTitleIsDisplayed() {
        // Launch the activity
        ActivityScenario.launch(MainActivity::class.java).use {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

            // Wait for the marker title "Sydney Opera House" to appear
            // Note: This relies on the Google Map rendering the marker and it being visible/accessible.
            // In a real device/emulator, this might need more robust waiting logic or accessibility settings.
            val markerTitle = device.findObject(UiSelector().descriptionContains("Sydney Opera House"))
            
            // Allow some time for map loading and rendering
            if (!markerTitle.exists()) {
                markerTitle.waitForExists(10000)
            }
            
            // Verify if found (Bonus check, might flake on some emulators if map doesn't render)
            // We use a softer check here or assertion if we are confident.
            // For now, let's assert it exists if we want to be strict, or just log if we want to be lenient.
            // Given the requirement "Verify Map and Marker presence (Bonus)", we'll try to assert.
            assertThat(markerTitle.exists()).isTrue()
        }
    }
}
