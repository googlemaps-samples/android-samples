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
  series of connected points). The file might also include attributes like trail name, difficulty,
  and length. This data can be used to create a map where trails are displayed as lines, and the
  appearance of the lines (color, thickness, pattern) can be customized based on the trail's
  attributes.

* **`kyoto_polygons.geojson`:** This GeoJSON file contains data about temples in Kyoto, Japan. Each
  feature in the file represents a temple, and the geometry is likely a polygon (an enclosed area).
  The file might also include attributes like temple name, founding date, and architectural style.
  This data can be used to create a map where temples are displayed as polygons, and the appearance
  of the polygons (fill color, border color) can be customized based on the temple's attributes."

**Uploading Data to Google Cloud Console and Using it with Google Maps:**

To use this data with data-driven styling in Google Maps, you'll need to upload it to the Google
Cloud Console and create a dataset. Here's a general outline of the process:

1. **Go to the Google Cloud Console:** Open the Google Cloud Console in your web browser.

2. **Create a Project (If Needed):** If you don't already have a project, create one.

3. **Enable the Maps Platform:** Make sure the Maps Platform is enabled for your project.

4. **Go to the "Datasets" page:** In the Cloud Console, navigate to the "Datasets" page (you can
   search for it).

5. **Create a Dataset:** Click "Create Dataset" and choose the appropriate type based on your data (
   e.g., "Point dataset" for `new_york_points.csv`, "Polyline dataset" for
   `boulder_polylines.geojson`, "Polygon dataset" for `kyoto_polygons.geojson`).

6. **Upload Your Data:** Follow the instructions to upload your data file. You might need to specify
   the data format (CSV or GeoJSON) and how to interpret the columns (e.g., which column contains
   latitude and longitude).

7. **Create a Dataset ID:** Once the data is uploaded, you'll get a Dataset ID. This is a unique
   identifier for your dataset.

8. **Use the Dataset ID in Your Code:** In your Android code, when you create a
   `FeatureLayerOptions` object, use the `datasetId()` method to set the Dataset ID. This will link
   your map layer to the dataset you uploaded.

9. **Apply Data-Driven Styling:** Use the `StyleFactory` to define how to style the features in your
   layer based on their attributes. The `StyleFactory` provides access to the feature's properties,
   which you can use to determine the appropriate styling.

**Important Notes:**

* **Data Preparation:** Make sure your data is properly formatted (CSV or GeoJSON) and that it
  includes the necessary columns (latitude, longitude, and any attributes you want to use for
  styling).
* **Dataset Types:** Choose the correct dataset type when creating the dataset in the Cloud Console.
  This will ensure that the data is interpreted correctly.
* **Dataset IDs:** Keep track of your Dataset IDs, as you'll need them to link your map layers to
  the correct datasets.
* **Styling:** Data-driven styling is very powerful. You can create complex and dynamic map styles
  based on your data. Refer to the Google Maps documentation for more information and examples.

By following these steps, you can upload your data to the Google Cloud Console, create datasets, and
use those datasets to create beautiful and informative maps with data-driven styling in your Android
application.



## Using Dataset Styles in Android

This guide explains how to create and apply dataset styles in Android. You'll learn how to create a map style, associate it with a map ID, upload datasets, and link the datasets to the map style to enable data-driven styling.

### Sample Datasets

This directory contains sample datasets for demonstrating data-driven styling:

* **`new_york_points.csv`:** This CSV file contains data about squirrel sightings in New York City.  Each row represents a sighting, and the columns likely include information like location (latitude and longitude), date, and other details about the squirrel (e.g., color, size).  This data can be used to create a map where each squirrel sighting is represented by a point, and the appearance of the point (color, icon) can be customized based on the squirrel's attributes.

* **`boulder_polylines.geojson`:** This GeoJSON file contains data about trails in Boulder, Colorado. Each feature in the file represents a trail, and the geometry is likely a polyline (a series of connected points).  The file might also include attributes like trail name, difficulty, and length. This data can be used to create a map where trails are displayed as lines, and the appearance of the lines (color, thickness, pattern) can be customized based on the trail's attributes.

* **`kyoto_polygons.geojson`:** This GeoJSON file contains data about temples in Kyoto, Japan. Each feature in the file represents a temple, and the geometry is likely a polygon (an enclosed area). The file might also include attributes like temple name, founding date, and architectural style. This data can be used to create a map where temples are displayed as polygons, and the appearance of the polygons (fill color, border color) can be customized based on the temple's attributes.

### Steps to Enable Data-Driven Styling

1. **Create a Map ID:**

    * Follow the steps in [Create a map ID](https://developers.google.com/maps/documentation/get-map-id#create-a-map-id). Make sure to set the **Map type** to **Android**.

2. **Create a New Map Style:**

    * Follow the instructions in [Manage map styles](https://developers.google.com/maps/documentation/android-sdk/cloud-customization/map-styles) to create a new style.
    * [Associate the new style with the map ID you just created](https://developers.google.com/maps/documentation/android-sdk/cloud-customization/map-styles#associate-style-with-map-id).

3. **Upload a Dataset:**

    * Go to the [Google Maps Platform Datasets](https://console.cloud.google.com/google/maps-apis/datasets) page in the Google Cloud Console.
    * Click "Create Dataset" and choose the appropriate type based on your data (e.g., "Point dataset" for `new_york_points.csv`, "Polyline dataset" for `boulder_polylines.geojson`, "Polygon dataset" for `kyoto_polygons.geojson`).
    * Upload your data file, specifying the data format (CSV or GeoJSON) and how to interpret the columns.
    * Confirm that the dataset upload is successful.

4. **Link the Dataset to the Map Style:**

    * On the [Datasets](https://console.cloud.google.com/google/maps-apis/datasets) page, click on the **Preview** of your dataset.
    * Associate the dataset with one of the map styles you created.

**Important Notes:**

* **Data Preparation:** Ensure your data is properly formatted (CSV or GeoJSON) and includes the necessary columns (latitude, longitude, and any attributes you want to use for styling).
* **Dataset Types:** Choose the correct dataset type when creating the dataset.
* **Dataset IDs:** You'll need the Dataset ID to link your map layers to the correct datasets in your Android code.
* **Styling:** Use the `StyleFactory` in your Android code to define how to style features based on their attributes.

By following these steps, you can upload your data, create datasets, and use them to create informative maps with data-driven styling in your Android application.