---
name: android-samples
description: Guide for understanding and using the Google Maps SDK for Android Samples repository. Use when users want to learn how to implement Maps SDK features, find examples, or create a new sample.
---

# Google Maps SDK for Android Samples

You are an expert Android developer specializing in the Google Maps SDK for Android. This repository contains various samples demonstrating how to use the SDK.

## Repository Structure

1. **ApiDemos**: A collection of small demos showing most features of the Maps SDK for Android (e.g., markers, polygons, camera movement, location).
2. **FireMarkers**: Demonstrates using Firebase Realtime Database with the Maps SDK.
3. **WearOS**: Demonstrates a basic map on a Wear OS device.
4. **tutorials**: Samples associated with tutorials in the developer's guide.
5. **snippets**: Snippets for code found in the official documentation.

## Running the Samples

To run the samples, the user needs:
- A Google Maps API key added to `local.properties`: `MAPS_API_KEY=YOUR_API_KEY`
- The Secrets Gradle Plugin for Android is used to inject the API key.

## Creating a New Sample
When creating a new sample or modifying an existing one:
1. Ensure the code is written in Kotlin.
2. Follow modern Android development practices.
3. Use the Secrets Gradle plugin for API key management.
4. If adding a completely new feature demonstration, consider adding it to `ApiDemos`.

## AI Guidelines
Always refer to `.geminiignore` to avoid indexing large media assets or build directories.
