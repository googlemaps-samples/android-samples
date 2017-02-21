Google Maps Android API Demos
===================================

These are demos for the [Google Maps Android API v2](https://developers.google.com/maps/documentation/android-api/).
They demonstrate most of the features available in the API.

This app was written for a minSdk of 9 and the v4 support library, but it can be easily adapted to
use native functionality instead.
(For example replacing ``SupportMapFragment`` with ``MapFragment``.)

Pre-requisites
--------------

- Android SDK v23
- Latest Android Build Tools
- Android Support Repository

Getting Started
---------------

This sample use the Gradle build system.

First download the samples by cloning this repository or downloading an archived
snapshot. (See the options at the top of the page.)

In Android Studio, use the "Import non-Android Studio project" or 
"Import Project" option. Next select the ApiDemos/ directory that you downloaded
from this repository.
If prompted for a gradle configuration accept the default settings. 

Alternatively use the "gradlew build" command to build the project directly.

Add your API key to the file `debug/values/google_maps_api.xml`.
It's pulled from there into your app's `AndroidManifest.xml` file.
See the [quick guide to getting an API key](https://developers.google.com/maps/documentation/android-api/signup).

Support
-------

- Stack Overflow: https://stackoverflow.com/questions/tagged/android+google-maps

If you have discovered an issue with the Google Maps Android API v2, please see
the resources here: https://developers.google.com/maps/documentation/android-api/support

If you've found an error in these samples, please file an issue:
https://github.com/googlemaps/android-samples/issues

Patches are encouraged, and may be submitted according to the instructions in
CONTRIBUTING.md.

![Analytics](https://ga-beacon.appspot.com/UA-12846745-20/android-samples-apidemos/readme?pixel)

License
-------

Please refer to the [LICENSE](https://github.com/googlemaps/android-samples/blob/master/LICENSE) at the root of this repo.
