Google Maps Android API Sample: Adding a Styled Map
===================================================

This sample goes hand in hand with a tutorial for the Google Maps Android API:
[Adding a Styled Map](https://developers.google.com/maps/documentation/android-api/styling).

Prerequisites
--------------

- Android SDK v24
- Latest Android Build Tools
- Android Support Repository

Getting started
---------------

This sample uses the Gradle build system.

1. Download the samples by cloning this repository or downloading an archived
  snapshot. (See the options at the top of the page.)
1. In Android Studio, create a new project and choose the "Import non-Android Studio project" or
  "Import Project" option.
1. Select the `StyledMap` directory that you downloaded with this repository.
1. If prompted for a gradle configuration, accept the default settings.
  Alternatively use the "gradlew build" command to build the project directly.

This demo app requires that you add your own Google Maps API key:

  1. [Get a Maps API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
  1. Create a file in the root directory called `secure.properties` (this file should *NOT* be under version control to protect your API key)
  1. Add a single line to `secure.properties` that looks like `MAPS_API_KEY=YOUR_API_KEY`, where `YOUR_API_KEY` is the API key you obtained in the first step
  1. Build and run

Support
-------

Stack Overflow: https://stackoverflow.com/questions/tagged/android+google-maps

If you have discovered an issue with the Google Maps Android API v2, please see
the resources here: https://developers.google.com/maps/documentation/android-api/support

If you've found an error in these samples, please file an issue:
https://github.com/googlemaps/android-samples/issues

![Analytics](https://ga-beacon.appspot.com/UA-12846745-20/android-samples-apidemos/readme?pixel)

License
-------

Please refer to the [LICENSE](https://github.com/googlemaps/android-samples/blob/master/LICENSE) at the root of this repo.
