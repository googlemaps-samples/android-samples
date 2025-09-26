This directory contains sample datasets for demonstrating data-driven styling in Google Maps.
Data-driven styling allows you to customize the appearance of map features (like points, lines, and
polygons) based on the data associated with those features.

* **`new_york_points.csv`:** This CSV file contains data about squirrel sightings in New York City.
  Each row represents a sighting, with the location (latitude
  and longitude), date, and other details about the squirrel (e.g., color, size). This data can be
  used to create a map where each squirrel sighting is represented by a point, and the appearance of
  the point (color, icon) can be customized based on the squirrel's attributes.

* **`boulder_polylines.geojson`:** This GeoJSON file contains data about trails in Boulder,
  Colorado. Each feature in the file represents a trail, and the geometry is likely a polyline (a
  series of connected points). The file also includes attributes like trail name, difficulty,
  and length. This data can be used to create a map where trails are displayed as lines, and the
  appearance of the lines (color, thickness, pattern) can be customized based on the trail's
  attributes.

* **`kyoto_polygons.geojson`:** This GeoJSON file contains data about temples in Kyoto, Japan. Each
  feature in the file represents a temple, and the geometry is a polygon (an enclosed area).

**Uploading Data to Google Cloud Console and Using it with Google Maps:**

To use this data with data-driven styling in Google Maps, you'll need to upload it to the Google
Cloud Console and create a dataset. Here's a general outline of the process:

## 1. Create a Map ID
To create a new map ID, follow the steps in [Create a map ID](https://developers.google.com/maps/documentation/android-sdk/map-ids/mapid-over).
Make sure to set the **Map type** to **Android**.

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

## 5. Set the Dataset ID in the secrets.properties file
```
BOULDER_DATASET_ID=<BOULDER_DATASET_ID>
NEW_YORK_DATASET_ID=<NEW_YORK_DATASET_ID>
KYOTO_DATASET_ID=<KYOTO_DATASET_ID>
```
** Important: ** the map ID set in the strings.xml file must match the maps ID associated with the style.