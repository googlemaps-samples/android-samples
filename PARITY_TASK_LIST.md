# ✅ Complete Capability Parity Task List (100% Core 2D SDK Completed & Verified)

This task list enumerates all code snippet authoring, catalog registration, and verification tasks required to achieve **100% capability parity** between Google Maps Platform's official index (`capabilities.json`) and the 2D Maps SDK samples repository (`comprehensive-catalog`).

---

## 🎯 Phase 1: Data-Driven Styling (DDS) Parity (Completed)

Data-Driven Styling enables developers to style administrative boundaries (states, counties, postal codes) and upload custom geospatial datasets via Google Cloud. **Final Coverage: 7 / 7 Capabilities.**

### Boundary Styling
- [x] **Author Administrative Boundary Styling Snippet**  
  - *Capability ID:* `dedc17af-b978-4790-858c-b83bb99a8bee` — Change the style of boundaries on a map.  
  - *Target File:* `snippets/kotlin-app/.../snippets/DataDrivenBoundarySnippets.kt` & `.java`  
  - *Verification:* Verified via fail-then-pass test `verifyDataDrivenBoundarySnippetsRegistered`.
- [x] **Author Boundary Click Interaction Snippet**  
  - *Capability ID:* `fa7cc2f9-225c-436a-b001-e4d71f277604` — Respond to user interactions with boundaries on a map.  
  - *Target File:* `DataDrivenBoundarySnippets.kt` & `.java` (`addBoundaryClickListener`).
- [x] **Author Boundary Choropleth Map Snippet**  
  - *Capability ID:* `0a767a66-08a3-412d-9d8f-92027e3ed9be` — Add choropleth styling to a map.  
  - *Target File:* `DataDrivenBoundarySnippets.kt` & `.java` (`createChoroplethMap`).

### Custom Geospatial Datasets
- [x] **Author Custom Dataset Feature Layer Snippet**  
  - *Capability ID:* `3ebaeaa1-1f4a-4d98-9969-289fb76d001e` — Add a custom geospatial dataset to a map.  
  - *Target File:* `DatasetLayerSnippets.kt` & `.java` (`loadDatasetLayer`).  
  - *Verification:* Verified via fail-then-pass test `verifyDatasetLayerSnippetsRegistered`.
- [x] **Author Custom Dataset Feature Styling Snippet**  
  - *Capability ID:* `eb3ed819-c782-4712-a2d2-03f8e5431b29` — Change the style of custom dataset features on a map.  
  - *Target File:* `DatasetLayerSnippets.kt` & `.java` (`styleDatasetFeatures`).
- [x] **Author Custom Dataset Interaction Snippet**  
  - *Capability ID:* `e72146cb-1e8f-4db9-9e44-b64296a19398` — Respond to user interactions with custom dataset features on a map.  
  - *Target File:* `DatasetLayerSnippets.kt` & `.java` (`addDatasetClickListener`).
- [x] **Author Dataset Creation Workflow Reference Guide**  
  - *Capability ID:* `5b54c6a7-fdd5-42ec-b7ce-63b37c9a1649` — Create a reusable, cross-platform geospatial dataset.  
  - *Target File:* `DatasetLayerSnippets.kt` & `.java` & `CATALOG.md`.

---

## 🧭 Phase 2: Street View Catalog Registration Parity (Completed)

Street View initialization, camera panning, zooming, tilting, and animation code registered in `StreetViewSnippets`. **Final Coverage: 4 / 4 Registered Capabilities.**

- [x] **Annotate Street View Snippet Class**  
  - *Capability ID:* `b8fadfc3-caae-464b-ac0e-70a1503a1e5e` — Add a configurable, interactive Google Street View to an app.  
  - *Target File:* `StreetViewSnippets.kt` & `StreetViewSnippets.java`  
  - *Verification:* Verified via fail-then-pass test `verifyStreetViewSnippetRegistered`.
- [x] **Register Street View Interaction & Event Callback Snippet**  
  - *Capability ID:* `6e3999d1-6c71-4a63-a52b-15cf2358ae10` — Respond to user interactions and events in a Google Street View.  
  - *Target File:* `StreetViewSnippets.kt` & `.java` (`launchStreetView`).
- [x] **Register Street View Gestures Customization Snippet**  
  - *Capability ID:* `75a7efe9-1797-404c-acba-a616be210f36` — Customize the gestures that are available for Google Street View.  
  - *Target File:* `StreetViewSnippets.kt` & `.java` (`zoomPanorama`).
- [x] **Register Street View Camera Animation Snippet**  
  - *Capability ID:* `7b144b66-b24c-49d9-a08c-c1cf69178c87` — Animate the camera movements for a Google Street View.  
  - *Target File:* `StreetViewSnippets.kt` & `.java` (`animatePanorama`).

---

## ☁️ Phase 3: Cloud Console Customization Parity (Completed)

Formal code registration mapping Cloud Console styling workflows to Map ID client code. **Final Coverage: 8 / 8 Capabilities.**

- [x] **Author Cloud Customization Snippets Class**  
  - *Target File:* `CloudCustomizationSnippets.kt` & `.java`  
  - *Verification:* Verified via fail-then-pass test `verifyCloudCustomizationSnippetsRegistered`.
- [x] **Map Reusable Map Styles (`4d87a0ea`)** (`loadReusableMapStyle`)
- [x] **Map Road & Polygon Styling (`5d26e9fb`)** (`loadRoadAndPolygonStyling`)
- [x] **Map Feature Visibility Toggling (`1f5dea73`)** (`loadFeatureVisibilityStyling`)
- [x] **Map Icons & Text Labels Styling (`3fc0911b`)** (`loadIconAndLabelStyling`)
- [x] **Map Zoom-Level Styling (`589c7e69`)** (`loadZoomLevelStyling`)
- [x] **Map POI Density Filtering (`468c2301`)** (`loadPoiDensityFiltering`)
- [x] **Map Building Styling (`89814817`)** (`loadBuildingStyling`)
- [x] **Map Landmark Styling (`4255f56a`)** (`loadLandmarkStyling`)

---

## ⚙️ Phase 4: Standalone Map Configurations & Wear OS Parity

- [x] **Author Map Color Scheme (Dark Mode) Snippet** (`25bf9dfd`)  
  - *Target File:* `MapInitSnippets.kt` & `.java` (`setMapColorScheme`).
- [x] **Author Traffic Layer Toggling Snippet** (`20793ebb`)  
  - *Target File:* `MapInitSnippets.kt` & `.java` (`enableTrafficLayer`).
- [ ] **TODO: Index Wear OS Map Sample in Catalog Discovery (`2b6457c4`)**  
  - *Status:* Deferred per user feedback. Sample project maintained under `WearOS/Wearable`.

---

## 🧪 Phase 5: Full Automation & Execution Verification (Completed)

- [x] **Regenerated `CATALOG.md` & `COVERAGE.md`** via `python3 test/verify_catalog.py`.
- [x] **Executed `verifyAllSnippetsLaunchWithoutCrash`** confirming 100% clean launch across all 90+ catalog items on `medium_phone`.
