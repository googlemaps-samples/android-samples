// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.app_utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.example.utils.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

class KML {
    private GoogleMap map;

    private void addKmlLayerFile(Context context) throws IOException, XmlPullParserException {
        // [START maps_android_utils_kml_add_file]
        KmlLayer layer = new KmlLayer(map, R.raw.geojson_file, context);
        // [END maps_android_utils_kml_add_file]
    }

    private void addKmlLayerFileInputStream(Context context) throws IOException, XmlPullParserException {
        // [START maps_android_utils_kml_add_input_stream]
        InputStream inputStream = // InputStream containing KML data
            // [START_EXCLUDE silent]
            null;
        // [END_EXCLUDE]
        KmlLayer layer = new KmlLayer(map, inputStream, context);
        // [END maps_android_utils_kml_add_input_stream]

        // [START maps_android_utils_kml_add_layer]
        layer.addLayerToMap();
        // [END maps_android_utils_kml_add_layer]

        // [START maps_android_utils_kml_remove_layer]
        layer.removeLayerFromMap();
        // [END maps_android_utils_kml_remove_layer]

        // [START maps_android_utils_kml_access_containers]
        for (KmlContainer containers : layer.getContainers()) {
            // Do something to container
        }
        // [END maps_android_utils_kml_access_containers]

        // [START maps_android_utils_kml_access_placemarks]
        for (KmlPlacemark placemark : layer.getPlacemarks()) {
            // Do something to Placemark
        }
        // [END maps_android_utils_kml_access_placemarks]

        // [START maps_android_utils_kml_access_properties]
        for (KmlContainer container : layer.getContainers()) {
            if (container.hasProperty("name")) {
                Log.i("KML", container.getProperty("name"));
            }
        }
        // [END maps_android_utils_kml_access_properties]

        // [START maps_android_utils_kml_click_listener]
        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Log.i("KML", "Feature clicked: " + feature.getId());
            }
        });
        // [END maps_android_utils_kml_click_listener]
    }

    // [START maps_android_utils_kml_access_containers_nested]
    public void accessContainers(Iterable<KmlContainer> containers) {
        for (KmlContainer container : containers) {
            if (container.hasContainers()) {
                accessContainers(container.getContainers());
            }
        }
    }
    // [END maps_android_utils_kml_access_containers_nested]
}
