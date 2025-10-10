### Plan for Refactoring `onClick` Attributes (Iterative Approach)

1.  **Enable View Binding:**
    *   Ensure the `build.gradle.kts` files for `java-app`, `kotlin-app`, and `common-ui` modules have View Binding enabled.
    *   **Verification:** Run `./gradlew build` to confirm the project builds successfully.

2.  **Track `onClick` Attributes:**
    *   Use the `ON_CLICKS.md` file as a checklist for all `onClick` attributes to be refactored.

3.  **Iterative Refactoring and Committing:**
    *   For each `onClick` function listed in `ON_CLICKS.md` (skipping StreetView-related ones for now), perform the following steps:
        a.  **Select Target:** Choose a single `onClick` function to refactor.
        b.  **Refactor Java:** In the `java-app` module, locate the relevant Activity/Fragment, and replace the `onClick` attribute with a programmatic click listener using View Binding.
        c.  **Refactor Kotlin:** Repeat the process for the corresponding Activity/Fragment in the `kotlin-app` module.
        d.  **Remove from XML:** Remove the `android:onClick` attribute from the relevant XML layout file(s) in the `common-ui` module.
        e.  **Verification:** Run `./gradlew build` to ensure the project compiles and the changes are stable.
        f.  **Commit Changes:** Commit the successful refactoring with a clear, descriptive message (e.g., "Refactor: Replace onClick for onGoToBondi with View Binding").
        g.  **Update Tracking File:** Mark the corresponding checkboxes in `ON_CLICKS.md` as complete.

4.  **StreetView Activities:**
    *   Defer refactoring of `onClick` attributes in StreetView-related activities (`StreetViewPanoramaNavigationDemoActivity`, etc.) until a reliable testing strategy can be established for them.

5.  **Logging:**
    *   Maintain a high-level summary of progress and lessons learned in `PROGRESS.md`.