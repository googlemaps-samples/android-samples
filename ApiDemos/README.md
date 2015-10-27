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
snapshot. (See the options on the right hand side.)

In Android Studio, use the "Import non-Android Studio project" or 
"Import Project" option. Next select the ApiDemos/ directory that you downloaded
from this repository.
If prompted for a gradle configuration accept the default settings. 

Alternatively use the "gradlew build" command to build the project directly.

Don't forget to add your API key to the file ``debug/values/google_maps_api.xml`` for your debug keystore.
(See [https://developers.google.com/maps/documentation/android/start](https://developers.google.com/maps/documentation/android/start#get_an_android_certificate_and_the_google_maps_api_key))

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

Copyright 2015 The Android Open Source Project

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
