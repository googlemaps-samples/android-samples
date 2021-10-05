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
package com.example.polygons

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

/**
 * An activity that displays a Google map with polylines to represent paths or routes,
 * and polygons to represent areas.
 */
// [START maps_poly_activity_on_map_ready]
class PolyActivity : AppCompatActivity(), OnMapReadyCallback, OnPolylineClickListener, OnPolygonClickListener {

    // [START EXCLUDE]
    // [START maps_poly_activity_get_map_async]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }
    // [END maps_poly_activity_get_map_async]

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */
    // [END EXCLUDE]
    override fun onMapReady(googleMap: GoogleMap) {

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        // [START maps_poly_activity_add_polyline_set_tag]
        // [START maps_poly_activity_add_polyline]
        val polyline1 = googleMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                LatLng(-35.016, 143.321),
                LatLng(-34.747, 145.592),
                LatLng(-34.364, 147.891),
                LatLng(-33.501, 150.217),
                LatLng(-32.306, 149.248),
                LatLng(-32.491, 147.309)))
        // [END maps_poly_activity_add_polyline]
        // [START_EXCLUDE silent]
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.tag = "A"
        // [END maps_poly_activity_add_polyline_set_tag]
        // Style the polyline.
        stylePolyline(polyline1)

        val polyline2 = googleMap.addPolyline(PolylineOptions()
            .clickable(true)
            .add(
                LatLng(-29.501, 119.700),
                LatLng(-27.456, 119.672),
                LatLng(-25.971, 124.187),
                LatLng(-28.081, 126.555),
                LatLng(-28.848, 124.229),
                LatLng(-28.215, 123.938)))
        polyline2.tag = "B"
        stylePolyline(polyline2)

        // [START maps_poly_activity_add_polygon]
        // Add polygons to indicate areas on the map.
        val polygon1 = googleMap.addPolygon(PolygonOptions()
            .clickable(true)
            .add(
                LatLng(-27.457, 153.040),
                LatLng(-33.852, 151.211),
                LatLng(-37.813, 144.962),
                LatLng(-34.928, 138.599)))
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.tag = "alpha"
        // Style the polygon.
        // [END maps_poly_activity_add_polygon]
        stylePolygon(polygon1)

        val polygon2 = googleMap.addPolygon(PolygonOptions()
            .clickable(true)
            .add(
                LatLng(-31.673, 128.892),
                LatLng(-31.952, 115.857),
                LatLng(-17.785, 122.258),
                LatLng(-12.4258, 130.7932)))
        polygon2.tag = "beta"
        stylePolygon(polygon2)
        // [END_EXCLUDE]

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
        googleMap.setOnPolylineClickListener(this)
        googleMap.setOnPolygonClickListener(this)
    }
    // [END maps_poly_activity_on_map_ready]

    // [START maps_poly_activity_style_polyline]
    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private fun stylePolyline(polyline: Polyline) {
        // Get the data object stored with the polyline.
        val type = polyline.tag?.toString() ?: ""
        when (type) {
            "A" -> {
                // Use a custom bitmap as the cap at the start of the line.
                polyline.startCap = CustomCap(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10f)
            }
            "B" -> {
               // Use a round cap at the start of the line.
                polyline.startCap = RoundCap()
            }
        }
        polyline.endCap = RoundCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polyline.color = COLOR_BLACK_ARGB
        polyline.jointType = JointType.ROUND
    }
    // [END maps_poly_activity_style_polyline]

    // [START maps_poly_activity_on_polyline_click]
    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED = listOf(GAP, DOT)

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    override fun onPolylineClick(polyline: Polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if (polyline.pattern == null || !polyline.pattern!!.contains(DOT)) {
            polyline.pattern = PATTERN_POLYLINE_DOTTED
        } else {
            // The default pattern is a solid stroke.
            polyline.pattern = null
        }
        Toast.makeText(this, "Route type " + polyline.tag.toString(),
            Toast.LENGTH_SHORT).show()
    }
    // [END maps_poly_activity_on_polyline_click]

    /**
     * Listens for clicks on a polygon.
     * @param polygon The polygon object that the user has clicked.
     */
    override fun onPolygonClick(polygon: Polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        var color = polygon.strokeColor xor 0x00ffffff
        polygon.strokeColor = color
        color = polygon.fillColor xor 0x00ffffff
        polygon.fillColor = color
        Toast.makeText(this, "Area type ${polygon.tag?.toString()}", Toast.LENGTH_SHORT).show()
    }

    // [START maps_poly_activity_style_polygon]
    private val COLOR_WHITE_ARGB = -0x1
    private val COLOR_DARK_GREEN_ARGB = -0xc771c4
    private val COLOR_LIGHT_GREEN_ARGB = -0x7e387c
    private val COLOR_DARK_ORANGE_ARGB = -0xa80e9
    private val COLOR_LIGHT_ORANGE_ARGB = -0x657db
    private val POLYGON_STROKE_WIDTH_PX = 8
    private val PATTERN_DASH_LENGTH_PX = 20

    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dash.
    private val PATTERN_POLYGON_ALPHA = listOf(GAP, DASH)

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private val PATTERN_POLYGON_BETA = listOf(DOT, GAP, DASH, GAP)

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private fun stylePolygon(polygon: Polygon) {
        // Get the data object stored with the polygon.
        val type = polygon.tag?.toString() ?: ""
        var pattern: List<PatternItem>? = null
        var strokeColor = COLOR_BLACK_ARGB
        var fillColor = COLOR_WHITE_ARGB
        when (type) {
            "alpha" -> {
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA
                strokeColor = COLOR_DARK_GREEN_ARGB
                fillColor = COLOR_LIGHT_GREEN_ARGB
            }
            "beta" -> {
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA
                strokeColor = COLOR_DARK_ORANGE_ARGB
                fillColor = COLOR_LIGHT_ORANGE_ARGB
            }
        }
        polygon.strokePattern = pattern
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        polygon.strokeColor = strokeColor
        polygon.fillColor = fillColor
    }
    // [END maps_poly_activity_style_polygon]
}
