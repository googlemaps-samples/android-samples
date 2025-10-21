### Plan for Ensuring `onClick` Functionality with Tests

**Primary Goal:** Work across all activities to ensure a basic level of testing, with the priority of verifying that all `onClick` functions work correctly.

1.  **Project Stability Check:**
    *   Ensure View Binding is enabled in `java-app`, `kotlin-app`, and `common-ui` modules.
    *   **Verification:** Run `./gradlew build` to confirm the project builds successfully before starting.

2.  **`onClick` Attribute Inventory:**
    *   Use `ON_CLICKS.md` as the master checklist for all `onClick` attributes to be refactored and tested.

3.  **Iterative Refactoring, Testing, and Committing:**
    *   For each `onClick` function listed in `ON_CLICKS.md`:
        a.  **Target Selection:** Choose one `onClick` function to process.
        b.  **Write Verification Test:** Create a basic Espresso test that simulates a click on the button/view and verifies a visible outcome (e.g., a toast message appears, a map camera moves, a UI element changes). This is the primary validation for the `onClick` functionality.
        c.  **Verification Build:** Run `./gradlew build` and `./gradlew connectedAndroidTest` to ensure the project compiles and the new test passes.
        d.  **Refactor to View Binding (Java):** In the `java-app` module, replace the `android:onClick` XML attribute with a programmatic click listener using View Binding in the relevant Activity/Fragment.
        e.  **Refactor to View Binding (Kotlin):** Repeat the refactoring for the corresponding Activity/Fragment in the `kotlin-app` module.
        f.  **XML Cleanup:** Remove the `android:onClick` attribute from the layout file(s) in the `common-ui` module.
        g.  **Verification Build (Post-Refactoring):** Run `./gradlew build` and `./gradlew connectedAndroidTest` again to ensure the refactoring did not break anything.
        h.  **Commit Progress:** Commit the successful refactoring and testing with a clear message (e.g., "Refactor & Test: Replace onClick for onGoToBondi with View Binding and Espresso test.").
        i.  **Update Checklist:** Mark the corresponding checkboxes in `ON_CLICKS.md` as complete.

4.  **Progress Logging:**
    *   Maintain a summary of progress in `PROGRESS.md`.

### Transition to Google Truth for Assertions

**Goal:** Improve assertion readability and maintainability by migrating from standard JUnit asserts to Google Truth, and create custom subjects for geospatial types.

1.  **Add Google Truth Dependency:**
    *   Add `androidTestImplementation("com.google.truth:truth:1.1.3")` to the `build.gradle.kts` file for both the `java-app` and `kotlin-app` modules.

2.  **Create Custom Truth Subjects:**
    *   **`LatLngSubject`:** Create a custom subject for `LatLng` objects.
        *   This will provide an `isNear()` assertion to check if two points are within a given tolerance of each other.
        *   Implement for both Java and Kotlin test sources.
    *   **`LatLngBoundsSubject`:** Create a custom subject for `LatLngBounds`.
        *   This will provide a `containsWithTolerance()` assertion to check if a `LatLng` is within the bounds, accounting for floating-point inaccuracies.
        *   Implement for both Java and Kotlin test sources.

3.  **Refactor Existing Tests:**
    *   Update `CameraClampingDemoActivityTest` in both Java and Kotlin modules to use the new `LatLngBoundsSubject` and its `containsWithTolerance()` method.
    *   This will replace the manual, verbose, and ugly epsilon comparisons.

4.  **Verification:**
    *   Run `./gradlew :java-app:connectedCheck` and `./gradlew :kotlin-app:connectedCheck` to confirm that all tests still pass after the refactoring.
