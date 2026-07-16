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

package com.example.snippets.kotlin.snippets

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.annotation.RawRes
import android.widget.Toast
import com.example.snippets.common.R
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.Feature
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineString
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPoint
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPlacemark
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Scanner
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException

@SnippetGroup(
    title = "Utility Library",
    description = "Snippets demonstrating marker clustering, heatmaps, GeoJSON, KML, and Multilayer managers."
)
class UtilsSnippets(private val context: Context, private val map: TrackedMap) {

    private var clusterManager: ClusterManager<MyItem>? = null
    private var geoJsonLayer: GeoJsonLayer? = null
    private var heatmapTileOverlay: TileOverlay? = null

    // [START maps_android_utils_clustering_cluster_item]
    class MyItem(
        lat: Double,
        lng: Double,
        private val title: String,
        private val snippet: String
    ) : ClusterItem {

        private val position: LatLng = LatLng(lat, lng)

        override fun getPosition(): LatLng {
            return position
        }

        override fun getTitle(): String {
            return title
        }

        override fun getSnippet(): String {
            return snippet
        }

        override fun getZIndex(): Float {
            return 0f
        }
    }
    // [END maps_android_utils_clustering_cluster_item]

    @SnippetItem(
        title = "1. Marker Clustering Setup",
        description = "What it does: Initializes a ClusterManager with 10 markers in close geographic proximity in London.\nHow to see the effect: Zooming out aggregates markers into numbered cluster circles; zooming in breaks them into pins.",
    )
    fun setUpClusterer() {
        // [START maps_android_utils_clustering_cluster_manager]
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        val manager = ClusterManager<MyItem>(context, map.delegate)
        clusterManager = manager

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        map.setOnCameraIdleListener(manager)
        map.setOnMarkerClickListener(manager)

        // Add cluster items (markers) to the cluster manager.
        addItems()
        manager.cluster()
        // [END maps_android_utils_clustering_cluster_manager]
    }

    private fun addItems() {
        val manager = clusterManager ?: return
        // Set some lat/lng coordinates to start with.
        var lat = 51.5145160
        var lng = -0.1270060

        val items = mutableListOf<MyItem>()
        // Add ten cluster items in close proximity, for purposes of this example.
        for (i in 0..9) {
            val offset = i / 60.0
            lat += offset
            lng += offset
            val offsetItem = MyItem(lat, lng, "Title $i", "Snippet $i")
            items.add(offsetItem)
        }
        manager.addItems(items)
    }

    @SnippetItem(
        title = "2. Disable Cluster Animation",
        description = "What it does: Disables smooth position transition animations on ClusterManager.\nHow to see the effect: When zooming or panning, cluster pins immediately snap to position without smooth sliding.",
    )
    fun clusterAnimation() {
        clusterManager?.let {
            // [START maps_android_utils_clustering_animation_off]
            it.setAnimation(false)
            // [END maps_android_utils_clustering_animation_off]
        }
    }

    @SnippetItem(
        title = "3. Add Clustering Info Window Item",
        description = "What it does: Adds a single ClusterItem with a custom title and snippet to the ClusterManager.\nHow to see the effect: Tap the individual cluster item pin to view its custom title and snippet callout.",
    )
    fun infoWindow() {
        clusterManager?.let {
            // [START maps_android_utils_clustering_info_window]
            // Set the lat/long coordinates for the marker.
            val lat = 51.5009
            val lng = -0.122

            // Set the title and snippet strings.
            val title = "This is the title"
            val snippet = "and this is the snippet."

            // Create a cluster item for the marker and set the title and snippet using the constructor.
            val infoWindowItem = MyItem(lat, lng, title, snippet)

            // Add the cluster item (marker) to the cluster manager.
            it.addItem(infoWindowItem)
            // [END maps_android_utils_clustering_info_window]
        }
    }

    @SnippetItem(
        title = "3b. Clear Cluster Items",
        description = "What it does: Clears all items and clusters from the ClusterManager.\nHow to see the effect: All cluster pin circles and individual cluster markers disappear from the map view.",
    )
    fun clearClusterItems() {
        // [START maps_android_utils_clustering_clear]
        clusterManager?.clearItems()
        clusterManager?.cluster()
        // [END maps_android_utils_clustering_clear]
    }

    @SnippetItem(
        title = "3c. Remove Single Cluster Item",
        description = "What it does: Removes a specified single item from the active ClusterManager collection.\nHow to see the effect: The target marker pin is removed and surrounding cluster count numbers decrement.",
    )
    fun removeSingleClusterItem() {
        // [START maps_android_utils_clustering_remove]
        val item = MyItem(51.5145160, -0.1270060, "Title to remove", "Snippet")
        clusterManager?.removeItem(item)
        clusterManager?.cluster()
        // [END maps_android_utils_clustering_remove]
    }

    @SnippetItem(
        title = "3d. Cluster Listeners",
        description = "What it does: Registers click listeners for clusters, cluster items, and info window popups.\nHow to see the effect: Tapping a cluster circle or item displays a Toast notification with cluster details.",
    )
    fun demonstrateClusterListeners() {
        // [START maps_android_utils_clustering_listeners]
        val manager = clusterManager ?: return
        manager.setOnClusterClickListener { cluster ->
            Toast.makeText(context, "Cluster clicked: ${cluster.size} items", Toast.LENGTH_SHORT).show()
            false
        }
        manager.setOnClusterItemClickListener { item ->
            Toast.makeText(context, "Cluster item clicked: ${item.title}", Toast.LENGTH_SHORT).show()
            false
        }
        manager.setOnClusterItemInfoWindowClickListener { item ->
            Toast.makeText(context, "Cluster item info window clicked: ${item.title}", Toast.LENGTH_SHORT).show()
        }
        // [END maps_android_utils_clustering_listeners]
    }

    @SnippetItem(
        title = "4. GeoJSON Layer from JSONObject",
        description = "What it does: Constructs a GeoJsonLayer programmatically from an in-memory JSON object schema.\nHow to see the effect: Parsed GeoJSON points, lines, or polygons instantiate onto the map instance.",
    )
    fun addGeoJsonLayerJsonObject() {
        // [START maps_android_util_geojson_add_jsonobject]
        val geoJsonData: JSONObject? = // JSONObject containing the GeoJSON data
            // [START_EXCLUDE silent]
            null
            // [END_EXCLUDE]
        val layer = GeoJsonLayer(map.delegate, geoJsonData)
        // [END maps_android_util_geojson_add_jsonobject]
    }

    @SnippetItem(
        title = "5. Add GeoJSON Layer from File",
        description = "What it does: Imports and renders a GeoJSON dataset file from raw resources onto the map view.\nHow to see the effect: US geographic boundary lines and features render over the map in red vector lines.",
    )
    fun addGeoJsonLayerFile() {
        // [START maps_android_util_geojson_add_file]
        val layer = GeoJsonLayer(map.delegate, R.raw.geojson_file, context)
        // [END maps_android_util_geojson_add_file]
        geoJsonLayer = layer

        // [START maps_android_util_geojson_add_layer_to_map]
        layer.addLayerToMap()
        // [END maps_android_util_geojson_add_layer_to_map]
        map.delegate.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(38.0, -97.0), 3f))
    }

    @SnippetItem(
        title = "5b. Remove GeoJSON Layer",
        description = "What it does: Removes the imported GeoJSON layer from the active GoogleMap instance.\nHow to see the effect: All vector polylines and points associated with the GeoJSON dataset disappear.",
    )
    fun removeGeoJsonLayerFile() {
        geoJsonLayer?.let {
            // [START maps_android_util_geojson_remove_layer]
            it.removeLayerFromMap()
            // [END maps_android_util_geojson_remove_layer]
        }
    }

    @SnippetItem(
        title = "6. GeoJSON Features and Styling",
        description = "What it does: Programmatically iterates, styles, and adds custom point and linestring GeoJsonFeatures.\nHow to see the effect: Draggable markers and styled lines render according to default GeoJson feature styles.",
    )
    fun geoJsonFeature() {
        val layer = GeoJsonLayer(map.delegate, null)

        // [START maps_android_util_geojson_point_feature]
        val point = GeoJsonPoint(LatLng(0.0, 0.0))
        val properties = hashMapOf("Ocean" to "South Atlantic")
        val pointFeature = GeoJsonFeature(point, "Origin", properties, null)
        // [END maps_android_util_geojson_point_feature]

        // [START maps_android_util_geojson_point_feature_add]
        layer.addFeature(pointFeature)
        // [END maps_android_util_geojson_point_feature_add]

        // [START maps_android_util_geojson_point_feature_remove]
        layer.removeFeature(pointFeature)
        // [END maps_android_util_geojson_point_feature_remove]

        // [START maps_android_util_geojson_point_feature_access]
        for (feature in layer.features) {
            // Do something to the feature
            // [START_EXCLUDE silent]
            // [START maps_android_util_geojson_point_feature_has_property]
            if (feature.hasProperty("Ocean")) {
                val oceanProperty = feature.getProperty("Ocean")
            }
            // [END maps_android_util_geojson_point_feature_has_property]
            // [END_EXCLUDE]
        }
        // [END maps_android_util_geojson_point_feature_access]

        // [START maps_android_util_geojson_geometry_click_events]
        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener { feature ->
            Log.i("GeoJsonClick", "Feature clicked: ${feature.getProperty("title")}")
        }
        // [END maps_android_util_geojson_geometry_click_events]

        // [START maps_android_util_geojson_style]
        val pointStyle = layer.defaultPointStyle
        pointStyle.isDraggable = true
        pointStyle.title = "Hello, World!"
        pointStyle.snippet = "I am a draggable marker"
        val lineStyle = layer.defaultLineStringStyle
        val polygonStyle = layer.defaultPolygonStyle
        // [END maps_android_util_geojson_style]

        // [START maps_android_util_geojson_style_specific]
        // Create a new feature containing a linestring
        val lineStringArray: MutableList<LatLng> = ArrayList()
        lineStringArray.add(LatLng(0.0, 0.0))
        lineStringArray.add(LatLng(50.0, 50.0))
        val lineString = GeoJsonLineString(lineStringArray)
        val lineStringFeature = GeoJsonFeature(lineString, null, null, null)

        // Set the color of the linestring to red
        val lineStringStyle = GeoJsonLineStringStyle()
        lineStringStyle.color = Color.RED

        // Set the style of the feature
        lineStringFeature.lineStringStyle = lineStringStyle
        // [END maps_android_util_geojson_style_specific]
    }

    @SnippetItem(
        title = "7. KML Layer from File Resource",
        description = "What it does: Imports KML 3D building polygons from raw resource files and renders them over Mountain View.\nHow to see the effect: KML campus polygon outlines and placemarks render over the Google Campus.",
    )
    fun addKmlLayerFile() {
        // [START maps_android_utils_kml_add_file]
        val layer = KmlLayer(map.delegate, R.raw.kml_file, context)
        // [END maps_android_utils_kml_add_file]
        layer.addLayerToMap()
        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(37.422, -122.084), 16f))
    }

    @SnippetItem(
        title = "8. KML Layer from Input Stream",
        description = "What it does: Parses a KML dataset stream, iterates nested placemark containers, and binds click listeners.\nHow to see the effect: Tap any KML feature line or placemark to trigger a log and view container hierarchy.",
    )
    fun addKmlLayerFileInputStream() {
        // [START maps_android_utils_kml_add_input_stream]
        val inputStream: InputStream? = context.resources.openRawResource(R.raw.kml_file)
        val layer = KmlLayer(map.delegate, inputStream, context)
        // [END maps_android_utils_kml_add_input_stream]

        // [START maps_android_utils_kml_add_layer]
        layer.addLayerToMap()
        // [END maps_android_utils_kml_add_layer]
        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(37.422, -122.084), 16f))

        // [START maps_android_utils_kml_access_containers]
        for (containers in layer.containers) {
            // Do something to container
        }
        // [END maps_android_utils_kml_access_containers]

        // [START maps_android_utils_kml_access_placemarks]
        for (placemark in layer.placemarks) {
            // Do something to Placemark
        }
        // [END maps_android_utils_kml_access_placemarks]

        // [START maps_android_utils_kml_access_properties]
        for (container in layer.containers) {
            if (container.hasProperty("name")) {
                Log.i("KML", container.getProperty("name"))
            }
        }
        // [END maps_android_utils_kml_access_properties]

        // [START maps_android_utils_kml_click_listener]
        // Set a listener for geometry clicked events.
        layer.setOnFeatureClickListener { feature ->
            Log.i("KML", "Feature clicked: ${feature.id}")
        }
        // [END maps_android_utils_kml_click_listener]
    }

    // [START maps_android_utils_kml_access_containers_nested]
    fun accessContainers(containers: Iterable<KmlContainer>) {
        for (container in containers) {
            if (container.hasContainers()) {
                accessContainers(container.containers)
            }
        }
    } // [END maps_android_utils_kml_access_containers_nested]

    @SnippetItem(
        title = "9. Simple Heatmap",
        description = "What it does: Generates a HeatmapTileProvider visualization from police station coordinate data.\nHow to see the effect: A red/yellow density heatmap overlay displays over Melbourne showing location concentrations.",
    )
    fun addHeatMap() {
        // [START maps_android_utils_heatmap_simple]
        var latLngs: List<LatLng?>? = null

        // Get the data: latitude/longitude positions of police stations.
        try {
            latLngs = readItems(R.raw.police_stations)
        } catch (e: JSONException) {
            Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG).show()
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        val provider = HeatmapTileProvider.Builder()
            .data(latLngs?.filterNotNull() ?: emptyList())
            .build()

        // Add a tile overlay to the map, using the heat map tile provider.
        val overlay = map.addTileOverlay(TileOverlayOptions().tileProvider(provider))
        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(-37.8136, 144.9631), 10f))
        // [END maps_android_utils_heatmap_simple]
    }

    private fun readItems(@RawRes resource: Int): List<LatLng?> {
        val result: MutableList<LatLng?> = ArrayList()
        val inputStream = context.resources.openRawResource(resource)
        val json = Scanner(inputStream).useDelimiter("\\A").next()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val `object` = array.getJSONObject(i)
            val lat = `object`.getDouble("lat")
            val lng = `object`.getDouble("lng")
            result.add(LatLng(lat, lng))
        }
        return result
    }

    @SnippetItem(
        title = "10. Add Custom Heatmap",
        description = "What it does: Configures a custom color gradient (green to red), 0.7 opacity, and weighted data points.\nHow to see the effect: A custom green/red gradient heatmap overlay renders with custom transparency over the map.",
    )
    fun addCustomHeatmap() {
        val latLngs = try {
            readItems(R.raw.police_stations).filterNotNull()
        } catch (e: Exception) {
            emptyList()
        }
        // [START maps_android_utils_heatmap_customize]
        // Create the gradient.
        val colors = intArrayOf(
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 0, 0) // red
        )
        val startPoints = floatArrayOf(0.2f, 1f)
        val gradient = Gradient(colors, startPoints)

        // Create the tile provider.
        val provider = HeatmapTileProvider.Builder()
            .data(latLngs)
            .gradient(gradient)
            .build()

        // Add the tile overlay to the map.
        val tileOverlay = map.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(provider)
        )
        map.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(com.google.android.gms.maps.model.LatLng(-37.8136, 144.9631), 10f))
        // [END maps_android_utils_heatmap_customize]
        heatmapTileOverlay = tileOverlay

        // [START maps_android_utils_heatmap_customize_opacity]
        provider.setOpacity(0.7)
        tileOverlay?.clearTileCache()
        // [END maps_android_utils_heatmap_customize_opacity]

        // [START maps_android_utils_heatmap_customize_dataset]
        val data = latLngs.map { WeightedLatLng(it) }
        provider.setWeightedData(data)
        tileOverlay?.clearTileCache()
        // [END maps_android_utils_heatmap_customize_dataset]
    }

    @SnippetItem(
        title = "10b. Remove Custom Heatmap",
        description = "What it does: Removes the custom heatmap TileOverlay from the active GoogleMap instance.\nHow to see the effect: The heatmap color density overlay disappears completely from the viewport.",
    )
    fun removeCustomHeatmap() {
        heatmapTileOverlay?.let {
            // [START maps_android_utils_heatmap_remove]
            it.remove()
            // [END maps_android_utils_heatmap_remove]
        }
    }

    @SnippetItem(
        title = "11. Multilayer Collections Init",
        description = "What it does: Demonstrates sharing MarkerManager, PolylineManager, and PolygonManager across GeoJSON, KML, and ClusterManager.\nHow to see the effect: Unclustered markers, GeoJSON features, and KML layers co-exist without event or rendering conflict.",
    )
    fun initMultilayer() {
        // [START maps_android_utils_multilayer_init]
        val markerManager = MarkerManager(map.delegate)
        val groundOverlayManager = GroundOverlayManager(map.delegate)
        val polygonManager = PolygonManager(map.delegate)
        val polylineManager = PolylineManager(map.delegate)
        // [END maps_android_utils_multilayer_init]

        // [START maps_android_utils_multilayer_manager]
        val clusterManager = ClusterManager<MyItem>(context, map.delegate, markerManager)
        val geoJsonLineLayer = GeoJsonLayer(
            map.delegate,
            R.raw.geojson_file,
            context,
            markerManager,
            polygonManager,
            polylineManager,
            groundOverlayManager
        )
        val kmlPolylineLayer = KmlLayer(
            map.delegate,
            R.raw.kml_file,
            context,
            markerManager,
            polygonManager,
            polylineManager,
            groundOverlayManager,
            null
        )
        // [END maps_android_utils_multilayer_manager]

        // [START maps_android_utils_multilayer_unclustered_marker]
        val markerCollection = markerManager.newCollection()
        markerCollection.addMarker(
            MarkerOptions()
                .position(LatLng(51.150000, -0.150032))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Unclustered marker")
        )
        // [END maps_android_utils_multilayer_unclustered_marker]

        // [START maps_android_utils_multilayer_kml_click_events]
        kmlPolylineLayer.addLayerToMap()
        kmlPolylineLayer.setOnFeatureClickListener { feature: Feature ->
            Toast.makeText(
                context,
                "KML polyline clicked: ${feature.getProperty("name")}",
                Toast.LENGTH_SHORT
            ).show()
        }
        // [END maps_android_utils_multilayer_kml_click_events]

        // [START maps_android_utils_multilayer_marker_click_events]
        markerCollection.setOnMarkerClickListener { marker ->
            Toast.makeText(
                context,
                "Marker clicked: ${marker.title}",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }
}
