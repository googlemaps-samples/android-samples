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