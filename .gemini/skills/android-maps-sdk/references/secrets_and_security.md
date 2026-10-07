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

## 4. Mandatory Pre-Flight API Key & Restriction Audit (via `gcloud`)

> [!IMPORTANT]
> **Hard Pre-Flight Gate**:  
> Agents must **ALWAYS** verify the status of the API key, package name whitelist, signing key fingerprint, and required enabled APIs **BEFORE** building or deploying a new app to an emulator or physical device.  
> Do **NOT** assume the key works. Skipping this check leads directly to the "empty watermark" failure mode.

### The Three Pre-Flight Verification Criteria
1. **Application Restriction Whitelist**:
   Does the target `applicationId` (package name) and active debug keystore SHA-1 fingerprint exist in the API key's allowed Android applications list?
2. **API Key Target Restrictions**:
   Is **Maps SDK for Android** (`maps-android-backend.googleapis.com`) explicitly allowed under API restrictions on the key?
3. **Project API Service Enablement**:
   Is the Maps SDK for Android service enabled in the Google Cloud Platform project?

---

### Automated Verification Workflow via `gcloud`

#### Step 1: Extract Keystore SHA-1 Fingerprint
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep "SHA1:"
```
Default debug fingerprint in this environment:
`B6:54:7C:6C:FA:69:4D:59:22:65:E4:50:5B:9F:D1:16:9A:93:B7:26`

#### Step 2: Query API Key Restrictions with `gcloud`
```bash
gcloud services api-keys describe <KEY_RESOURCE_NAME> --format=json
```
Inspect:
- `restrictions.androidKeyRestrictions.allowedApplications`: verify `(package_name, sha1_fingerprint)` is present.
- `restrictions.apiTargets`: verify `service: "maps-android-backend.googleapis.com"` is present.

#### Step 3: Verify Enabled Project Services
```bash
gcloud services list --enabled --filter="name:maps-android-backend.googleapis.com"
```

---

### What to Do If Verification Fails

#### Case A: If `gcloud` is Working and Authenticated
Offer to fix the key automatically using the safe update helper script:

```bash
python3 /usr/local/google/home/dkhawk/.gemini/config/skills/adding_api_key_restrictions/scripts/add_android_restriction.py \
  --key-name="<KEY_RESOURCE_NAME>" \
  --package-name="<TARGET_PACKAGE_NAME>" \
  --sha1="<SHA1_FINGERPRINT>"
```

> [!CAUTION]
> Never call `gcloud services api-keys update --allowed-application=...` with only the new package, as `gcloud` will **wipe out all existing whitelisted applications**. Always use the helper script or query and re-pass all existing applications.

If the service itself is not enabled in the project:
```bash
gcloud services enable maps-android-backend.googleapis.com
```

#### Case B: If `gcloud` Fails (Auth Expired, CAA Block, or Tool Missing)
If running `gcloud` produces an authentication error (such as `Access was blocked by Context Aware Access` or `gcloud auth login` required) or if `gcloud` is not installed:

1. **Offer Automated Fix Upon CLI Auth**:
   Inform the user:
   > *"I detected that your `gcloud` session needs authentication (or CAA access). If you run `gcloud auth login` (or follow `go/gcloud-caa-error`), I can automatically update your API key restrictions and enable the required APIs for you."*

2. **Provide Exact Manual Cloud Console Instructions**:
   If the user prefers to fix it manually in the web console, provide the exact parameters:
   - **Google Cloud Console URL**: `https://console.cloud.google.com/apis/credentials`
   - **Target API Key**: Select key `AIzaSy...` (from `secrets.properties`).
   - **Add Application Restriction**:
     - Under **Application restrictions**, choose **Android apps**.
     - Click **+ Add an item**.
     - **Package name**: `<exact_applicationId>` (e.g. `com.example.bouldertrailheads`).
     - **SHA-1 certificate fingerprint**: `<exact_sha1_fingerprint>` (e.g. `B6:54:7C:6C:FA:69:4D:59:22:65:E4:50:5B:9F:D1:16:9A:93:B7:26`).
   - **Verify API Restrictions**:
     - Under **API restrictions**, choose **Restrict key**.
     - Ensure **Maps SDK for Android** is checked (and Places API if used).
   - Click **Save**.
   - Note: Changes take approximately 1–2 minutes to propagate to Google's edge servers.


