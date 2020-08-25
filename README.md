![GitHub contributors](https://img.shields.io/github/contributors/googlemaps/android-samples)
![Apache-2.0](https://img.shields.io/badge/license-Apache-blue) [![Build demos](https://github.com/googlemaps/android-samples/workflows/Build%20demos/badge.svg)](https://github.com/googlemaps/android-samples/actions?query=workflow%3A%22Build+demos%22)

Google Maps SDK for Android Samples
===================================

Samples demonstrating how to use
[Maps SDK for Android](https://developers.google.com/maps/documentation/android/).

This repo contains the following samples:

1. [ApiDemos](ApiDemos): A collection of small demos showing most features of the Maps SDK for Android.
1. [WearOS](WearOS):
Displays a map on a Wear OS device. This sample demonstrates the basic
setup required for a gradle-based Android Studio project.
1. [Tutorials](https://github.com/googlemaps/android-samples/tree/master/tutorials): Samples
associated with tutorials in the developer's guide. See each sample for a link to the associated guide.
1. [Snippets](snippets): Snippets for code found in https://developers.google.com/maps/documentation/android-sdk


Pre-requisites
--------------

See each sample for pre-requisites.
All require up-to-date versions of the Android build tools and the Android support repository.

Getting Started
---------------

These samples use the Gradle build system.

First download the samples by cloning this repository or downloading an archived
snapshot. (See the options at the top of the page.)

In Android Studio, use the "Import non-Android Studio project" or
"Import Project" option. Next select one of the sample directories that you downloaded from this
repository.
If prompted for a gradle configuration accept the default settings.

Alternatively use the `gradlew build` command to build the project directly.

The demo apps require that you add your own Google Maps API key:

1. [Get a Maps API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
1. Create a file in the root directory called `secure.properties` (this file should *NOT* be under version control to protect your API key)
1. Add a single line to `secure.properties` that looks like `MAPS_API_KEY=YOUR_API_KEY`, where `YOUR_API_KEY` is the API key you obtained in the first step
1. Build and run

Support
-------

- Stack Overflow: https://stackoverflow.com/questions/tagged/android+google-maps

If you have discovered an issue with the Google Maps Platform SDK for Android v2, please see
the resources here: https://developers.google.com/maps/support/

If you've found an error in these samples, please file an issue:
https://github.com/googlemaps/android-samples/issues

Patches are encouraged, and may be submitted according to the instructions in
CONTRIBUTING.md.

![Analytics](https://maps-ga-beacon.appspot.com/UA-12846745-20/android-samples/readme?pixel)

License
-------
(See each sample directory for details.)

Copyright 2015 The Android Open Source Project

Copyright 2015 Google, Inc.

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
