Wear OS Sample
===================================

This sample uses the [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/wear)
to display a map on Wear OS. It shows the basic setup required for a
gradle-based Android Studio project that [supports ambient mode](https://developer.android.com/training/wearables/apps/always-on.html).

Pre-requisites
--------------

- Android SDK v31
- Latest Android Build Tools
- Wear OS emulator or device

Getting Started
---------------

This sample use the Gradle build system.

First download the samples by cloning this repository or downloading an archived
snapshot. (See the options at the top of the page.)

In Android Studio, use the "Import non-Android Studio project" or
"Import Project" option. Next select the `WearOS/` directory that you downloaded
from this repository.
If prompted for a gradle configuration accept the default settings.

Alternatively use the "gradlew build" command to build the project directly.

See the [Get an API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key) guide to get an API key.

Open the `secrets.properties` file in your top-level directory, and then add the following code. Replace YOUR_API_KEY with your API key. Store your key in this file because secrets.properties is excluded from being checked into a version control system.
If the `secrets.properties` file does not exist, create it in the same folder as the `local.properties` file.

```
MAPS_API_KEY=YOUR_API_KEY
```


Support
-------

- Stack Overflow: https://stackoverflow.com/questions/tagged/android+google-maps

If you have discovered an issue with the Google Maps Android API v2, please see
the resources here: https://developers.google.com/maps/support/

If you've found an error in these samples, please file an issue:
https://github.com/googlemaps/android-samples/issues

Patches are encouraged, and may be submitted according to the instructions in
CONTRIBUTING.md.

![Analytics](https://ga-beacon.appspot.com/UA-12846745-20/android-samples-wearmap/readme?pixel)

License
-------

Please refer to the [LICENSE](https://github.com/googlemaps/android-samples/blob/main/LICENSE) at the root of this repo.
