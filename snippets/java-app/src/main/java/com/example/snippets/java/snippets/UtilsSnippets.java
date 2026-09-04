/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.java.snippets;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.collections.GroundOverlayManager;
import com.google.maps.android.collections.MarkerManager;
import com.google.maps.android.collections.PolygonManager;
import com.google.maps.android.collections.PolylineManager;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineString;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.google.maps.android.data.geojson.GeoJsonPoint;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.example.snippets.common.R;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

@SnippetGroup(
        title = "Utility Library",
        description = "Snippets demonstrating marker clustering, heatmaps, GeoJSON, KML, and Multilayer managers."
)
public class UtilsSnippets {

    private final Context context;
    private final TrackedMap map;
    private ClusterManager<MyItem> clusterManager;
    private GeoJsonLayer geoJsonLayer;
    private TileOverlay heatmapTileOverlay;

    public UtilsSnippets(Context context, TrackedMap map) {
        this.context = context;
        this.map = map;
    }

    // [START maps_android_utils_clustering_cluster_item]
    public static class MyItem implements ClusterItem {
        private final LatLng position;
        private final String title;
        private final String snippet;

        public MyItem(double lat, double lng, String title, String snippet) {
            position = new LatLng(lat, lng);
            this.title = title;
            this.snippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }

        @Nullable
        @Override
        public Float getZIndex() {
            return 0f;
        }
    }
    // [END maps_android_utils_clustering_cluster_item]

    @SnippetItem(
            title = "1. Marker Clustering Setup",
            description = "What it does: Initializes a ClusterManager with 10 markers in close geographic proximity in London.\nHow to see the effect: Zooming out aggregates markers into numbered cluster circles; zooming in breaks them into pins."
    )
    public void setUpClusterer() {
        // [START maps_android_utils_clustering_cluster_manager]
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MyItem>(context, map.getDelegate());

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
        clusterManager.cluster();
        // [END maps_android_utils_clustering_cluster_manager]
    }

    private void addItems() {
        // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;

        List<MyItem> items = new ArrayList<>();
        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng, "Title " + i, "Snippet " + i);
            items.add(offsetItem);
        }
        clusterManager.addItems(items);
    }

    @SnippetItem(
            title = "2. Disable Cluster Animation",
            description = "What it does: Disables smooth position transition animations on ClusterManager.\nHow to see the effect: When zooming or panning, cluster pins immediately snap to position without smooth sliding."
    )
    public void clusterAnimation() {
        if (clusterManager != null) {
            // [START maps_android_utils_clustering_animation_off]
            clusterManager.setAnimation(false);
            // [END maps_android_utils_clustering_animation_off]
        }
    }

    @SnippetItem(
            title = "3. Add Clustering Info Window Item",
            description = "What it does: Adds a single ClusterItem with a custom title and snippet to the ClusterManager.\nHow to see the effect: Tap the individual cluster item pin to view its custom title and snippet callout."
    )
    public void infoWindow() {
        if (clusterManager != null) {
            // [START maps_android_utils_clustering_info_window]
            // Set the lat/long coordinates for the marker.
            double lat = 51.5009;
            double lng = -0.122;

            // Set the title and snippet strings.
            String title = "This is the title";
            String snippet = "and this is the snippet.";

            // Create a cluster item for the marker and set the title and snippet using the constructor.
            MyItem infoWindowItem = new MyItem(lat, lng, title, snippet);

            // Add the cluster item (marker) to the cluster manager.
            clusterManager.addItem(infoWindowItem);
            // [END maps_android_utils_clustering_info_window]
        }
    }

    @SnippetItem(
            title = "3b. Clear Cluster Items",
            description = "What it does: Clears all items and clusters from the ClusterManager.\nHow to see the effect: All cluster pin circles and individual cluster markers disappear from the map view."
    )
    public void clearClusterItems() {
        // [START maps_android_utils_clustering_clear]
        if (clusterManager != null) {
            clusterManager.clearItems();
            clusterManager.cluster();
        }
        // [END maps_android_utils_clustering_clear]
    }

    @SnippetItem(
            title = "3c. Remove Single Cluster Item",
            description = "What it does: Removes a specified single item from the active ClusterManager collection.\nHow to see the effect: The target marker pin is removed and surrounding cluster count numbers decrement."
    )
    public void removeSingleClusterItem() {
        // [START maps_android_utils_clustering_remove]
        if (clusterManager != null) {
            MyItem item = new MyItem(51.5145160, -0.1270060, "Title to remove", "Snippet");
            clusterManager.removeItem(item);
            clusterManager.cluster();
        }
        // [END maps_android_utils_clustering_remove]
    }

    @SnippetItem(
            title = "3d. Cluster Listeners",
            description = "What it does: Registers click listeners for clusters, cluster items, and info window popups.\nHow to see the effect: Tapping a cluster circle or item displays a Toast notification with cluster details."
    )
    public void demonstrateClusterListeners() {
        // [START maps_android_utils_clustering_listeners]
        if (clusterManager == null) {
            return;
        }
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(com.google.maps.android.clustering.Cluster<MyItem> cluster) {
                Toast.makeText(context, "Cluster clicked: " + cluster.getSize() + " items", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                Toast.makeText(context, "Cluster item clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
            @Override
            public void onClusterItemInfoWindowClick(MyItem item) {
                Toast.makeText(context, "Cluster item info window clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        // [END maps_android_utils_clustering_listeners]
    }

    @SnippetItem(
            title = "4. GeoJSON Layer from JSONObject",
            description = "What it does: Constructs a GeoJsonLayer programmatically from an in-memory JSON object schema.\nHow to see the effect: Parsed GeoJSON points, lines, or polygons instantiate onto the map instance."
    )
    public void addGeoJsonLayerJsonObject() {
        // [START maps_android_util_geojson_add_jsonobject]
        JSONObject geoJsonData = // JSONObject containing the GeoJSON data
        // [START_EXCLUDE silent]
            null;
        // [END_EXCLUDE]
        GeoJsonLayer layer = new GeoJsonLayer(map.getDelegate(), geoJsonData);
        // [END maps_android_util_geojson_add_jsonobject]
    }

    @SnippetItem(
            title = "5. Add GeoJSON Layer from File",
            description = "What it does: Imports and renders a GeoJSON dataset file from raw resources onto the map view.\nHow to see the effect: US geographic boundary lines and features render over the map in red vector lines."
    )
    public void addGeoJsonLayerFile() throws IOException, JSONException {
        // [START maps_android_util_geojson_add_file]
        GeoJsonLayer layer = new GeoJsonLayer(map.getDelegate(), R.raw.geojson_file, context);
        // [END maps_android_util_geojson_add_file]
        geoJsonLayer = layer;

        // [START maps_android_util_geojson_add_layer_to_map]
        layer.addLayerToMap();
        // [END maps_android_util_geojson_add_layer_to_map]
        map.getDelegate().animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(new com.google.android.gms.maps.model.LatLng(38.0, -97.0), 3f));
    }

    @SnippetItem(
            title = "5b. Remove GeoJSON Layer",
            description = "What it does: Removes the imported GeoJSON layer from the active GoogleMap instance.\nHow to see the effect: All vector polylines and points associated with the GeoJSON dataset disappear."
    )
    public void removeGeoJsonLayerFile() {
        if (geoJsonLayer != null) {
            // [START maps_android_util_geojson_remove_layer]
            geoJsonLayer.removeLayerFromMap();
            // [END maps_android_util_geojson_remove_layer]
        }
    }

    @SnippetItem(
            title = "6. GeoJSON Features and Styling",
            description = "What it does: Programmatically iterates, styles, and adds custom point and linestring GeoJsonFeatures.\nHow to see the effect: Draggable markers and styled lines render according to default GeoJson feature styles."
    )
    public void geoJsonFeature() {
        GeoJsonLayer layer = new GeoJsonLayer(map.getDelegate(), null);

        // [START maps_android_util_geojson_point_feature]
        GeoJsonPoint point = new GeoJsonPoint(new LatLng(0, 0));
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Ocean", "South Atlantic");
        GeoJsonFeature pointFeature = new GeoJsonFeature(point, "Origin", properties, null);
        // [END maps_android_util_geojson_point_feature]

        // [START maps_android_util_geojson_point_feature_add]
        layer.addFeature(pointFeature);
        // [END maps_android_util_geojson_point_feature_add]

        // [START maps_android_util_geojson_point_feature_remove]
        layer.removeFeature(pointFeature);
        // [END maps_android_util_geojson_point_feature_remove]

        // [START maps_android_util_geojson_point_feature_access]
        for (GeoJsonFeature feature : layer.getFeatures()) {
            // Do something to the feature
            // [START_EXCLUDE silent]
            // [START maps_android_util_geojson_point_feature_has_property]
            if (feature.hasProperty("Ocean")) {
                String oceanProperty = feature.getProperty("Ocean");
            }
            // [END maps_android_util_geojson_point_feature_has_property]
            // [END_EXCLUDE]
        }
        // [END maps_android_util_geojson_point_feature_access]

        // [START maps_android_util_geojson_geometry_click_events]
        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Log.i("GeoJsonClick", "Feature clicked: " + feature.getProperty("title"));
            }
        });
        // [END maps_android_util_geojson_geometry_click_events]

        // [START maps_android_util_geojson_style]
        GeoJsonPointStyle pointStyle = layer.getDefaultPointStyle();
        pointStyle.setDraggable(true);
        pointStyle.setTitle("Hello, World!");
        pointStyle.setSnippet("I am a draggable marker");
        GeoJsonLineStringStyle lineStyle = layer.getDefaultLineStringStyle();
        com.google.maps.android.data.geojson.GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
        // [END maps_android_util_geojson_style]

        // [START maps_android_util_geojson_style_specific]
        // Create a new feature containing a linestring
        List<LatLng> lineStringArray = new ArrayList<LatLng>();
        lineStringArray.add(new LatLng(0, 0));
        lineStringArray.add(new LatLng(50, 50));
        GeoJsonLineString lineString = new GeoJsonLineString(lineStringArray);
        GeoJsonFeature lineStringFeature = new GeoJsonFeature(lineString, null, null, null);

        // Set the color of the linestring to red
        GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
        lineStringStyle.setColor(Color.RED);

        // Set the style of the feature
        lineStringFeature.setLineStringStyle(lineStringStyle);
        // [END maps_android_util_geojson_style_specific]
    }

    @SnippetItem(
            title = "7. KML Layer from File Resource",
            description = "What it does: Imports KML 3D building polygons from raw resource files and renders them over Mountain View.\nHow to see the effect: KML campus polygon outlines and placemarks render over the Google Campus."
    )
    public void addKmlLayerFile() throws IOException, XmlPullParserException {
        // [START maps_android_utils_kml_add_file]
        KmlLayer layer = new KmlLayer(map.getDelegate(), R.raw.kml_file, context);
        // [END maps_android_utils_kml_add_file]
        layer.addLayerToMap();
        map.getDelegate().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.422, -122.084), 16f));
    }

    @SnippetItem(
            title = "8. KML Layer from Input Stream",
            description = "What it does: Parses a KML dataset stream, iterates nested placemark containers, and binds click listeners.\nHow to see the effect: Tap any KML feature line or placemark to trigger a log and view container hierarchy."
    )
    public void addKmlLayerFileInputStream() throws IOException, XmlPullParserException {
        // [START maps_android_utils_kml_add_input_stream]
        InputStream inputStream = context.getResources().openRawResource(R.raw.kml_file);
        KmlLayer layer = new KmlLayer(map.getDelegate(), inputStream, context);
        // [END maps_android_utils_kml_add_input_stream]

        // [START maps_android_utils_kml_add_layer]
        layer.addLayerToMap();
        // [END maps_android_utils_kml_add_layer]
        map.getDelegate().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.422, -122.084), 16f));

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

    @SnippetItem(
            title = "9. Simple Heatmap",
            description = "What it does: Generates a HeatmapTileProvider visualization from police station coordinate data.\nHow to see the effect: A red/yellow density heatmap overlay displays over Melbourne showing location concentrations."
    )
    public void addHeatMap() {
        // [START maps_android_utils_heatmap_simple]
        List<LatLng> latLngs = new ArrayList<>();

        // Get the data: latitude/longitude positions of police stations.
        try {
            latLngs = readItems(R.raw.police_stations);
        } catch (JSONException e) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
            .data(latLngs)
            .build();

        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-37.8136, 144.9631), 10f));
        // [END maps_android_utils_heatmap_simple]
    }

    private List<LatLng> readItems(@RawRes int resource) throws JSONException {
        List<LatLng> result = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            result.add(new LatLng(lat, lng));
        }
        return result;
    }

    @SnippetItem(
            title = "10. Add Custom Heatmap",
            description = "What it does: Configures a custom color gradient (green to red), 0.7 opacity, and weighted data points.\nHow to see the effect: A custom green/red gradient heatmap overlay renders with custom transparency over the map."
    )
    public void addCustomHeatmap() {
        List<LatLng> latLngs = new ArrayList<>();
        try {
            latLngs = readItems(R.raw.police_stations);
        } catch (JSONException e) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }
        // [START maps_android_utils_heatmap_customize]
        // Create the gradient.
        int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
            0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        // Create the tile provider.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
            .data(latLngs)
            .gradient(gradient)
            .build();

        // Add the tile overlay to the map.
        TileOverlay tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-37.8136, 144.9631), 10f));
        // [END maps_android_utils_heatmap_customize]
        heatmapTileOverlay = tileOverlay;

        assert tileOverlay != null;

        // [START maps_android_utils_heatmap_customize_opacity]
        provider.setOpacity(0.7);
        tileOverlay.clearTileCache();
        // [END maps_android_utils_heatmap_customize_opacity]

        // [START maps_android_utils_heatmap_customize_dataset]
        List<WeightedLatLng> data = new ArrayList<>();
        for (LatLng latLng : latLngs) {
            data.add(new WeightedLatLng(latLng));
        }
        provider.updateData(data);
        tileOverlay.clearTileCache();
        // [END maps_android_utils_heatmap_customize_dataset]
    }

    @SnippetItem(
            title = "10b. Remove Custom Heatmap",
            description = "What it does: Removes the custom heatmap TileOverlay from the active GoogleMap instance.\nHow to see the effect: The heatmap color density overlay disappears completely from the viewport."
    )
    public void removeCustomHeatmap() {
        if (heatmapTileOverlay != null) {
            // [START maps_android_utils_heatmap_remove]
            heatmapTileOverlay.remove();
            // [END maps_android_utils_heatmap_remove]
        }
    }

    @SnippetItem(
            title = "11. Multilayer Collections Init",
            description = "What it does: Demonstrates sharing MarkerManager, PolylineManager, and PolygonManager across GeoJSON, KML, and ClusterManager.\nHow to see the effect: Unclustered markers, GeoJSON features, and KML layers co-exist without event or rendering conflict."
    )
    public void initMultilayer() throws IOException, JSONException, XmlPullParserException {
        // [START maps_android_utils_multilayer_init]
        MarkerManager markerManager = new MarkerManager(map.getDelegate());
        GroundOverlayManager groundOverlayManager = new GroundOverlayManager(map.getDelegate());
        PolygonManager polygonManager = new PolygonManager(map.getDelegate());
        PolylineManager polylineManager = new PolylineManager(map.getDelegate());
        // [END maps_android_utils_multilayer_init]

        // [START maps_android_utils_multilayer_manager]
        ClusterManager<MyItem> localClusterManager = new ClusterManager<>(context, map.getDelegate(), markerManager);
        GeoJsonLayer geoJsonLineLayer = new GeoJsonLayer(map.getDelegate(), R.raw.geojson_file, context, markerManager, polygonManager, polylineManager, groundOverlayManager);
        KmlLayer kmlPolylineLayer = new KmlLayer(map.getDelegate(), R.raw.kml_file, context, markerManager, polygonManager, polylineManager, groundOverlayManager, null);
        // [END maps_android_utils_multilayer_manager]

        // [START maps_android_utils_multilayer_unclustered_marker]
        MarkerManager.Collection markerCollection = markerManager.newCollection();
        markerCollection.addMarker(new MarkerOptions()
            .position(new LatLng(51.150000, -0.150032))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            .title("Unclustered marker"));
        // [END maps_android_utils_multilayer_unclustered_marker]

        // [START maps_android_utils_multilayer_kml_click_events]
        kmlPolylineLayer.addLayerToMap();
        kmlPolylineLayer.setOnFeatureClickListener(feature -> Toast.makeText(context,
            "KML polyline clicked: " + feature.getProperty("name"),
            Toast.LENGTH_SHORT).show());
        // [END maps_android_utils_multilayer_kml_click_events]

        // [START maps_android_utils_multilayer_marker_click_events]
        markerCollection.setOnMarkerClickListener(marker -> { Toast.makeText(context,
            "Marker clicked: " + marker.getTitle(),
                Toast.LENGTH_SHORT).show();
            return false;
        });
        // [END maps_android_utils_multilayer_marker_click_events]
    }
}
