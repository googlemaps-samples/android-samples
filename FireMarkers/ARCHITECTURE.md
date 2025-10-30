# FireMarkers Architecture

This document provides a visual overview of the FireMarkers application's architecture using a Mermaid diagram. This helps to understand the flow of data and user interactions within the app.

## Architecture Diagram

```mermaid
flowchart TD
    subgraph "Firebase Cloud"
        DB[(Firebase Realtime Database\n/markers)]
    end

    subgraph "Android App"
        subgraph "UI Layer (View)"
            A[MainActivity] --> B(MapScreen Composable);
            B -- User Clicks --> C["TopAppBar Actions<br>(Cycle, Seed, Clear)"];
            B -- Renders Markers --> D[GoogleMap Composable];
        end

        subgraph "State & Logic Layer (ViewModel)"
            VM[MarkersViewModel];
        end

        subgraph "Data Layer"
            FC[FirebaseConnection];
            M(MarkerData Model);
        end

        subgraph "Dependency Injection"
            Hilt(Hilt/Dagger);
        end
    end

    %% --- Interactions ---
    C -- Calls function --> VM;
    VM -- Uses --> FC;
    FC -- Reads/Writes to --> DB;
    DB -.->|Real-time updates| FC;
    FC -.-> VM;
    VM -- Updates StateFlow --> B;
    VM -- Uses Model --> M;

    %% --- DI Graph ---
    Hilt -- Injects --> FC;
    Hilt -- Injects --> VM;

    %% --- Styling ---
    style A fill:#cde,stroke:#333,stroke-width:2px
    style B fill:#cde,stroke:#333,stroke-width:2px
    style C fill:#cde,stroke:#333,stroke-width:2px
    style D fill:#cde,stroke:#333,stroke-width:2px
    style VM fill:#dce,stroke:#333,stroke-width:2px
    style FC fill:#edc,stroke:#333,stroke-width:2px
    style M fill:#edc,stroke:#333,stroke-width:2px
    style DB fill:#f9d,stroke:#333,stroke-width:2px
    style Hilt fill:#eee,stroke:#333,stroke-width:2px
```

### How it Works

1.  **UI Layer:** The `MainActivity` hosts the `MapScreen` composable. The user interacts with the `TopAppBar` buttons (`IconButton`s).
2.  **ViewModel:** These UI interactions call functions on the `MarkersViewModel` (e.g., `seedDatabase()`, `clearMarkers()`).
3.  **Data Layer:** The `MarkersViewModel` uses the `FirebaseConnection` service to send commands to the Firebase Realtime Database (e.g., write new markers, remove all markers).
4.  **Real-time Updates:** The `MarkersViewModel` also establishes a listener to the database. When data changes in the `/markers` path, Firebase pushes the update to the app.
5.  **State Flow:** The `MarkersViewModel` receives the updated data and emits it to a `StateFlow`.
6.  **UI Update:** The `MapScreen` composable collects the `StateFlow`. When a new list of markers is emitted, the `GoogleMap` recomposes and re-renders the markers on the screen.
7.  **Dependency Injection:** Hilt is used to provide the `FirebaseConnection` as a singleton to the `MarkersViewModel`, decoupling the layers.

```