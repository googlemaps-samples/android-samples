Google Maps Android API Demos
===================================

These are demos for the [Maps SDK for Android](https://developers.google.com/maps/documentation/android-api/)
and [Maps SDK for Android V3 BETA](https://developers.google.com/maps/documentation/android-sdk/v3-client-migration) libraries
in Kotlin.

They demonstrate most of the features available in the API.

The Maps SDK for Android samples can be found under the `main` gradle product
flavor, while the Maps SDK V3 BETA samples can be found under the `v3` gradle
product flavor. The active product flavor can be modified through
Android Studio’s “Build Variants” toolbar options.

Pre-requisites
--------------

- Android API level 21+
- Latest Android Build Tools
- Google Repository
- Google Play Services

Getting Started
---------------

This sample uses the Gradle build system.

First download the samples by cloning this repository or downloading an archived
snapshot. (See the options at the top of the page.)

In Android Studio, use "Open an existing Android Studio project". Next select
the `ApiDemos/kotlin/` directory that you downloaded from this repository.
If prompted for a gradle configuration accept the default settings. 

Alternatively use the `gradlew build` command to build the project directly.

This demo app requires that you add your own Google Maps API key. See [Get an API key](https://developers.google.com/maps/documentation/android-sdk/get-api-key) for more instructions.

# Using Dataset Styles in Android

This guide explains how to create and apply dataset styles in Android by creating a map style, associating it with a map ID, uploading a dataset, and linking the dataset to the map style.

## 1. Create a Map ID
To create a new map ID, follow the steps in [Create a map ID](https://developers.google.com/maps/documentation/get-map-id#create-a-map-id). Make sure to set the **Map type** to **Android**.

## 2. Create a New Map Style
Follow the instructions in [Manage map styles](https://developers.google.com/maps/documentation/android-sdk/cloud-customization/map-styles) to create a new style and [associate it with the map ID you just created](https://developers.google.com/maps/documentation/android-sdk/cloud-customization/map-styles#associate-style-with-map-id).

## 3. Upload a Dataset
To include data-driven styling in your map:

1. Upload the dataset on the [Google Maps Platform Datasets](https://console.cloud.google.com/google/maps-apis/datasets) page.
2. Confirm that the dataset upload is successful (sometimes there can be issues due to an invalid structure).

## 4. Link the Dataset to the Map Style
To enable data-driven styling:

1. Open your dataset in the [Google Maps Platform Datasets](https://console.cloud.google.com/google/maps-apis/datasets) page.
2. Click on the **Preview** of the dataset.
3. Associate the dataset with one of the previously created styles


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

Please refer to the [LICENSE](https://github.com/googlemaps/android-samples/blob/main/LICENSE) at the root of this repo.
