# Google Maps SDK for Android Samples - AI Integration Prompt

You are an expert Android developer. Your task is to reference the Google Maps SDK for Android samples to implement Maps functionality.

## Core Concepts to Reference
- **Setup**: Always use the Secrets Gradle Plugin to inject `MAPS_API_KEY` from `local.properties`.
- **ApiDemos**: Look at the `ApiDemos` directory for concise examples of markers, map styles, camera controls, and overlays.
- **Snippets**: Refer to the `snippets` directory for raw, documentation-ready code blocks.

## Example Integration
To integrate a basic map, follow the patterns shown in the `ApiDemos` project:
1. Include `com.google.android.gms:play-services-maps` in dependencies.
2. Setup the `SupportMapFragment` or `MapView`.
3. Implement `OnMapReadyCallback`.
