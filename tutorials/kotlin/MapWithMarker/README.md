Google Maps Android API Sample: Adding a Map with a Marker
==========================================================

This sample goes hand in hand with a tutorial for the Google Maps Android API:
[Adding a Map with a Marker](https://developers.google.com/maps/documentation/android-sdk/map-with-marker).

Prerequisites
--------------

- Android API level 24+
- Latest Android Build Tools
- Google Repository
- Google Play Services

Getting started
---------------

This sample uses the Gradle build system and is part of the root `android-samples` Gradle build.

1. Download the samples by cloning this repository or downloading an archived
  snapshot. (See the options at the top of the page.)
1. In Android Studio, select "Open" (or "Open an existing Android Studio project") and choose the
  root `android-samples` directory that you downloaded.
1. Select the `tutorials.kotlin.MapWithMarker` run configuration in Android Studio, or build the
  module from the root of this repository using Gradle.

This demo app requires that you add your own Google Maps API key. See [Get an API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key) for more instructions.

From the root of this repository, add your key to `secrets.properties` (see
`local.defaults.properties` for the format) and run:

```
./gradlew :tutorials:kotlin:MapWithMarker:assembleDebug
```

Support
-------

Stack Overflow: https://stackoverflow.com/questions/tagged/android+google-maps

If you have discovered an issue with the Google Maps Android API v2, please see
the resources here: https://developers.google.com/maps/documentation/android-sdk/support

If you've found an error in these samples, please file an issue:
https://github.com/googlemaps-samples/android-samples/issues

![Analytics](https://ga-beacon.appspot.com/UA-12846745-20/android-samples-apidemos/readme?pixel)

License
-------

Please refer to the [LICENSE](https://github.com/googlemaps-samples/android-samples/blob/main/LICENSE) at the root of this repo.
