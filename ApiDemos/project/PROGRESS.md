# Refactoring Progress for `onClick` Attributes

This file summarizes the progress, successful strategies, and lessons learned during the refactoring of `android:onClick` attributes to programmatic click listeners using View Binding.

### Progress Summary

**Objective:** Refactor all `android:onClick` attributes in XML layouts to use programmatic click listeners via View Binding, with a test-first approach.

**Completed Tasks:**

1.  **Enabled View Binding:** Successfully enabled the `viewBinding` feature in the `build.gradle.kts` files for the `java-app`, `kotlin-app`, and `common-ui` modules.
2.  **Created Tracking File:** Generated `ON_CLICKS.md` which lists all unique `onClick` function names found in the project's layout files.
3.  **Updated Development Plan:** The `PLAN.md` has been updated to enforce a test-first workflow.
4.  **Added and Stabilized Tests:**
    *   **`CameraClampingDemoActivity`**: Added comprehensive Espresso tests for all `onClick` functionality in both the `java-app` and `kotlin-app` modules.
    *   **`CameraDemoActivity`**: Fixed a pre-existing failing test.

### Solutions and Strategies That Work

1.  **`MapIdlingResource` for Reliable Tests:** Using a custom Espresso `IdlingResource` that listens for `OnCameraIdleListener` and `OnCameraMoveStartedListener` events is the most reliable way to synchronize tests with the `GoogleMap` component. This approach is far superior to `Thread.sleep()`, which is prone to flakiness.
2.  **`testInstrumentationRunner` for JUnit4 Tests:** Explicitly setting the `testInstrumentationRunner` in the `build.gradle.kts` file is necessary to ensure that JUnit4 tests are correctly discovered and executed by the Android test runner.
3.  **Robust Test Assertions:** When testing camera movements and zoom levels, it's more reliable to verify that previously set constraints are removed rather than asserting a specific default state. This makes the tests less brittle and more resilient to changes in the map's default behavior.

### Frequent Mistakes and Lessons Learned

1.  **`Thread.sleep()` is Unreliable:** Using `Thread.sleep()` to wait for asynchronous operations on the map is a significant source of flaky tests. The correct approach is to use an `IdlingResource`.
2.  **Type Mismatches in Assertions:** Kotlin's strict type system can cause build failures if the expected and actual values in an `assertEquals` call do not match. For example, the map's zoom level is a `Float`, but the `assertEquals` function often expects a `Double`. Explicitly casting the values (e.g., `.toDouble()`) is necessary to resolve these issues.
3.  **Variable Scope in Tests:** In Java tests, it's important to ensure that variables like `ActivityScenario` are declared as class fields so that they can be accessed in both the `@Before` and `@After` methods.