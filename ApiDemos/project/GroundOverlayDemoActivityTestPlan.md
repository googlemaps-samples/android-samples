### Test Plan: `GroundOverlayDemoActivity`

#### 1. Objective

To verify that all UI interactions and map-related logic within the `GroundOverlayDemoActivity` function as described and to ensure the activity serves as a robust, reliable code sample for developers. The primary goal is to confirm that user actions lead to the expected changes in the `GroundOverlay` properties.

#### 2. Scope

*   **In Scope:**
    *   Verification of the initial state of the map and overlays upon activity launch.
    *   Testing the functionality of all interactive UI elements: the transparency `SeekBar`, the "Switch Image" button, and the "Clickable" checkbox.
    *   Testing the direct interaction with the ground overlays on the map (i.e., click events).
    *   Confirming that changes to one overlay do not unintentionally affect the other.
*   **Out of Scope:**
    *   Testing the underlying Google Maps SDK rendering. We assume the map draws what it's told to draw.
    *   UI performance or frame rate.
    *   Testing framework-level behavior (e.g., `onCreate` lifecycle).

#### 3. Test Strategy

*   **Frameworks:** We will use **Espresso** for UI interactions and **Google Truth** for assertions.
*   **Asynchronicity:** The `MapIdlingResource` will be used to ensure all tests wait for map camera movements and other asynchronous operations to complete before making assertions, preventing flaky tests.
*   **Assertions:** Standard Truth assertions (`assertThat`) will be used for checking properties like transparency and clickability.
*   **Structure:** Each test case will be focused on a single piece of functionality (e.g., testing the `SeekBar` in isolation).

---

### Test Implementation TODO List

- [ ] Create test class `GroundOverlayDemoActivityTest` for the `java-app` module.
- [ ] Create test class `GroundOverlayDemoActivityTest` for the `kotlin-app` module.
- [ ] **`testInitialState`**: Verify that both `groundOverlay` and `groundOverlayRotated` are not null after the map is ready.
- [ ] **`testInitialClickability`**: Verify that `groundOverlayRotated` is clickable by default and that its transparency changes upon the first click.
- [ ] **`testTransparencySeekBar`**:
    - [ ] Move the `SeekBar` to a non-zero position.
    - [ ] Assert that the transparency of the main `groundOverlay` has changed.
    - [ ] Assert that the transparency of `groundOverlayRotated` has *not* changed.
- [ ] **`testSwitchImageButton`**:
    - [ ] Get the initial `BitmapDescriptor` of the main `groundOverlay`.
    - [ ] Click the "Switch Image" button.
    - [ ] Assert that the `BitmapDescriptor` of the `groundOverlay` has changed.
- [ ] **`testToggleClickability_DisablesClicks`**:
    - [ ] Verify the overlay is initially clickable (by clicking it and asserting transparency changes).
    - [ ] Click the "Clickable" checkbox to uncheck it.
    - [ ] Get the current transparency of `groundOverlayRotated`.
    - [ ] Click the map where `groundOverlayRotated` is located.
    - [ ] Assert that the transparency of `groundOverlayRotated` has *not* changed.
- [ ] **`testToggleClickability_ReEnablesClicks`**:
    - [ ] Click the "Clickable" checkbox twice (off, then on again).
    - [ ] Get the current transparency of `groundOverlayRotated`.
    - [ ] Click the map where `groundOverlayRotated` is located.
    - [ ] Assert that the transparency of `groundOverlayRotated` *has* changed.
- [ ] **`testMainOverlay_IsNotClickable`**:
    - [ ] Get the initial transparency of the main `groundOverlay`.
    - [ ] Click the map in the center of the main `groundOverlay`.
    - [ ] Assert that its transparency has *not* changed.
