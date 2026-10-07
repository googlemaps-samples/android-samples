# API Key Security & Restrictions

This guide explains how to secure Google Maps Platform API keys, prevent secret leaks in version control, and configure application restriction whitelists in the Google Cloud Console.

---

## 1. Secrets Gradle Plugin Configuration

The **Secrets Gradle Plugin for Android** reads credentials from an untracked properties file and injects them as `BuildConfig` fields or Manifest placeholders at compile time.

### Step 1: Root Project Setup
In the root `build.gradle.kts`:
```kotlin
buildscript {
    dependencies {
        classpath(libs.plugins.secrets.gradle.plugin)
    }
}
```

### Step 2: App Module Setup
In the app-level `build.gradle.kts`:
```kotlin
// [START maps_android_secrets_gradle_plugin_config]
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.secrets.gradle.plugin)
}

secrets {
    // 1. Point to untracked local file
    propertiesFileName = "secrets.properties"

    // 2. Point to tracked defaults file
    defaultPropertiesFileName = "local.defaults.properties"

    // 3. Configure key keys to ignore or include
    ignoreList.add("keyToIgnore")
}
// [END maps_android_secrets_gradle_plugin_config]
```

### Step 3: Git Ignore Configuration
In `.gitignore`:
```gitignore
# Exclude credentials
local.properties
secrets.properties
```

---

## 2. Manifest Placeholders

The plugin automatically provides `${MAPS_API_KEY}` to the manifest merger:

```xml
<application ...>
    <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="${MAPS_API_KEY}" />
</application>
```

---

## 3. Google Cloud Console API Key Restrictions

Unrestricted API keys pose a security vulnerability. For Android applications, restrict every key by **Application Restrictions** and **API Restrictions**.

### Retrieving SHA-1 Certificate Fingerprint
Run the Gradle `signingReport` task to get the SHA-1 fingerprint for your debug or release keystore:

```bash
./gradlew signingReport
```

Or using `keytool` directly:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

### Applying Whitelist via Google Cloud Console
1. Navigate to **Google Cloud Console** &rarr; **APIs & Services** &rarr; **Credentials**.
2. Select your API key.
3. Under **Application restrictions**, choose **Android apps**.
4. Click **Add an item**:
   - **Package name**: Your `applicationId` (e.g., `com.example.kotlindemos`).
   - **SHA-1 certificate fingerprint**: The 20-byte hex fingerprint (e.g., `DA:39:A3:EE:5E:6B:4B:0D:32:55:BF:EF:95:60:18:90:AF:D8:07:09`).
5. Under **API restrictions**, select **Restrict key** and enable only:
   - **Maps SDK for Android**
   - **Places API** (if using Places)

> [!CAUTION]
> If you encounter `ERR_DIFFERENT_APP_OR_KEY` or `230 authorization errors`, verify that both the active build variant's `applicationId` and the signing key's SHA-1 fingerprint match the entry in Cloud Console. Debug and Release builds use different keystores and must both be registered.

---

## 4. Pre-Flight Device Verification & Whitelist Alignment

### The Silent Authorization Failure Mode
When an application with an unauthorized `applicationId` or mismatched SHA-1 fingerprint runs on a device:
- The app **does NOT crash**.
- The `SupportMapFragment` or `MapView` initializes normally.
- However, the Google Maps SDK receives a `403 / 230 Authorization Error` in the background and renders an **empty, solid beige or grey canvas with only the Google logo watermark**.

### Pre-Flight Checklist Before On-Device Deployment
Before deploying a new app or module to physical hardware or emulators:
1. **Check Allowed Applications**: Verify whether the API key in `secrets.properties` is restricted in Google Cloud Console.
2. **Handle New Application IDs**: If creating a brand-new app (e.g. `com.example.boulderfountains`), either:
   - Add the new package name and debug SHA-1 (`B6:54:7C:6C:FA:69:4D:59:22:65:E4:50:5B:9F:D1:16:9A:93:B7:26`) to the API key's whitelist using the safe helper script or Cloud Console.
   - Or during development, set `defaultConfig.applicationId = "com.example.kotlindemos"` (or another already-whitelisted package name) to authorize immediately.
3. **Secrets Plugin Manifest Placeholders**:
   Avoid defining static dummy placeholders like `manifestPlaceholders["MAPS_API_KEY"] = "DEFAULT_KEY"` in `defaultConfig` if it prevents the Secrets Gradle Plugin from injecting the actual key from `secrets.properties`. Use `local.defaults.properties` for fallback values instead.

