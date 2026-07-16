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

import android.graphics.Color
import com.example.snippets.kotlin.R
import com.example.snippets.kotlin.TrackedMap
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.StrokeStyle
import com.google.android.gms.maps.model.StyleSpan
import com.google.android.gms.maps.model.TextureStyle

@SnippetGroup(
    title = "Shapes",
    description = "Snippets demonstrating shapes, custom styled polylines, polygons, and circles."
)
class ShapesSnippets(private val map: TrackedMap) {

    @SnippetItem(
        title = "1. Simple Polyline",
        description = "What it does: Creates a polyline path and connects sequential coordinates to form a rectangular boundary line.\nHow to see the effect: A solid stroke outline connects four geographic coordinates on the map.",
    )
    fun polylines() {
        // [START maps_android_shapes_polylines_polylineoptions]
        // Instantiates a new Polyline object and adds points to define a rectangle
        val polylineOptions = PolylineOptions()
            .add(LatLng(37.35, -122.0))
            .add(LatLng(37.45, -122.0)) // North of the previous point, but at the same longitude
            .add(LatLng(37.45, -122.2)) // Same latitude, and 30km to the west
            .add(LatLng(37.35, -122.2)) // Same longitude, and 16km to the south
            .add(LatLng(37.35, -122.0)) // Closes the polyline.

        // Get back the mutable Polyline
        val polyline = map.addPolyline(polylineOptions)
        polyline?.let {
            val isClickable = it.isClickable
            val isGeodesic = it.isGeodesic
            val isVisible = it.isVisible
            val color = it.color
            val width = it.width
            val zIndex = it.zIndex
            val points = it.points
        }
        // [END maps_android_shapes_polylines_polylineoptions]
    }

    @SnippetItem(
        title = "2. Simple Polygon",
        description = "What it does: Constructs a filled polygon covering a rectangular geographic region.\nHow to see the effect: A solid filled rectangular polygon shape overlays the map surface.",
    )
    fun polygons() {
        // [START maps_android_shapes_polygons_polygonoptions]
        // Instantiates a new Polygon object and adds points to define a rectangle
        val rectOptions = PolygonOptions()
            .add(
                LatLng(37.35, -122.0),
                LatLng(37.45, -122.0),
                LatLng(37.45, -122.2),
                LatLng(37.35, -122.2),
                LatLng(37.35, -122.0)
            )

        // Get back the mutable Polygon
        val polygon = map.addPolygon(rectOptions)
        // [END maps_android_shapes_polygons_polygonoptions]
    }

    @SnippetItem(
        title = "3. Polygon Autocompletion",
        description = "What it does: Demonstrates that open coordinate paths automatically join the last point back to origin.\nHow to see the effect: Unclosed polygon vertices automatically connect to form a closed, filled shape.",
    )
    fun polygonAutocompletion() {
        // [START maps_android_shapes_polygons_autocompletion]
        val polygon1 = map.addPolygon(
            PolygonOptions()
                .add(
                    LatLng(0.0, 0.0),
                    LatLng(0.0, 5.0),
                    LatLng(3.0, 5.0),
                    LatLng(0.0, 0.0)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE)
        )
        val polygon2 = map.addPolygon(
            PolygonOptions()
                .add(
                    LatLng(0.0, 0.0),
                    LatLng(0.0, 5.0),
                    LatLng(3.0, 5.0)
                )
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE)
        )
        // [END maps_android_shapes_polygons_autocompletion]
    }

    @SnippetItem(
        title = "4. Hollow Polygon",
        description = "What it does: Adds interior coordinate hole paths (cutouts) inside an outer filled polygon boundary.\nHow to see the effect: A solid polygon displays with a transparent window cutout in its center.",
    )
    fun polygonHollow() {
        // [START maps_android_shapes_polygons_hollow]
        val hole = listOf(
            LatLng(1.0, 1.0),
            LatLng(1.0, 2.0),
            LatLng(2.0, 2.0),
            LatLng(2.0, 1.0),
            LatLng(1.0, 1.0)
        )
        val hollowPolygon = map.addPolygon(
            PolygonOptions()
                .add(
                    LatLng(0.0, 0.0),
                    LatLng(0.0, 5.0),
                    LatLng(3.0, 5.0),
                    LatLng(3.0, 0.0),
                    LatLng(0.0, 0.0)
                )
                .addHole(hole)
                .fillColor(Color.BLUE)
        )
        hollowPolygon?.let {
            val isClickable = it.isClickable
            val isGeodesic = it.isGeodesic
            val isVisible = it.isVisible
            val fillColor = it.fillColor
            val strokeColor = it.strokeColor
            val strokeWidth = it.strokeWidth
            val zIndex = it.zIndex
            val points = it.points
            val holes = it.holes
        }
        // [END maps_android_shapes_polygons_hollow]
    }

    @SnippetItem(
        title = "5. Circle",
        description = "What it does: Draws a circular shape specified by center coordinates and radius in meters.\nHow to see the effect: A circular overlay ring appears surrounding the center point with a 1,000 meter radius.",
    )
    fun circles() {
        // [START maps_android_shapes_circles_circleoptions]
        // Instantiates a new CircleOptions object and defines the center and radius
        val circleOptions = CircleOptions()
            .center(LatLng(37.4, -122.1))
            .radius(1000.0) // In meters

        // Get back the mutable Circle
        val circle = map.addCircle(circleOptions)
        circle?.let {
            val isClickable = it.isClickable
            val isVisible = it.isVisible
            val center = it.center
            val radius = it.radius
            val fillColor = it.fillColor
            val strokeWidth = it.strokeWidth
            val zIndex = it.zIndex
        }
        // [END maps_android_shapes_circles_circleoptions]
    }

    @SnippetItem(
        title = "6. Circle Click Event",
        description = "What it does: Attaches a touch listener directly to a clickable circle shape overlay.\nHow to see the effect: Tap inside the circle shape; its border stroke color immediately flips.",
    )
    fun circlesEvents() {
        // [START maps_android_shapes_circles_events]
        val circle = map.addCircle(
            CircleOptions()
                .center(LatLng(37.4, -122.1))
                .radius(1000.0)
                .strokeWidth(10f)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(128, 255, 0, 0))
                .clickable(true)
        )
        map.delegate.setOnCircleClickListener {
            // Flip the r, g and b components of the circle's stroke color.
            val strokeColor = it.strokeColor xor 0x00ffffff
            it.strokeColor = strokeColor
        }
        // [END maps_android_shapes_circles_events]
    }

    @SnippetItem(
        title = "7. Custom Polyline Appearance",
        description = "What it does: Configures custom stroke patterns (dots and dashes), joint types, and custom cap icons.\nHow to see the effect: The polyline line renders with a dashed pattern and custom arrow icon end-cap.",
    )
    fun customAppearances() {
        // [START maps_android_shapes_custom_appearances]
        val polyline = map.addPolyline(
            PolylineOptions()
                .add(LatLng(-37.81319, 144.96298), LatLng(-31.95285, 115.85734))
                .width(25f)
                .color(Color.BLUE)
                .geodesic(true)
        )
        // [END maps_android_shapes_custom_appearances]

        // [START maps_android_shapes_custom_appearances_stroke_pattern]
        val pattern = listOf(
            Dot(), Gap(20F), Dash(30F), Gap(20F)
        )
        polyline?.pattern = pattern
        // [END maps_android_shapes_custom_appearances_stroke_pattern]

        // [START maps_android_shapes_custom_appearances_joint_type]
        polyline?.jointType = JointType.ROUND
        // [END maps_android_shapes_custom_appearances_joint_type]

        // [START maps_android_shapes_custom_appearances_start_cap]
        polyline?.startCap = RoundCap()
        // [END maps_android_shapes_custom_appearances_start_cap]

        // [START maps_android_shapes_custom_appearances_end_cap]
        polyline?.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow), 16F)
        // [END maps_android_shapes_custom_appearances_end_cap]
    }

    @SnippetItem(
        title = "8. Associate Data Tag",
        description = "What it does: Attaches a custom metadata tag object ('A') to a clickable polyline.\nHow to see the effect: Click listeners can inspect the polyline's tag property for business logic routing.",
    )
    fun associateData() {
        // [START maps_android_shapes_associate_data]
        val polyline = map.addPolyline(
            PolylineOptions()
                .clickable(true)
                .add(
                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.364, 147.891),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)
                )
        )
        polyline?.tag = "A"
        // [END maps_android_shapes_associate_data]
    }

    @SnippetItem(
        title = "9. Multicolored Polyline Spans",
        description = "What it does: Applies multiple StyleSpan stroke colors to distinct segments along a single polyline.\nHow to see the effect: The polyline shifts colors from red to green across different segment lengths.",
    )
    fun multicoloredPolyline() {
        // [START maps_android_polyline_multicolored]
        val line = map.addPolyline(
            PolylineOptions()
                .add(LatLng(47.6677146, -122.3470447), LatLng(47.6442757, -122.2814693))
                .addSpan(StyleSpan(Color.RED))
                .addSpan(StyleSpan(Color.GREEN))
        )
        // [END maps_android_polyline_multicolored]
    }

    @SnippetItem(
        title = "10. Multicolored Gradient Polyline",
        description = "What it does: Creates a smooth color gradient transition across a polyline segment using StrokeStyle.gradientBuilder.\nHow to see the effect: The polyline line smoothly blends from red to yellow along its continuous path.",
    )
    fun multicoloredGradientPolyline() {
        // [START maps_android_polyline_gradient]
        val line = map.addPolyline(
            PolylineOptions()
                .add(LatLng(47.6677146, -122.3470447), LatLng(47.6442757, -122.2814693))
                .addSpan(
                    StyleSpan(
                        StrokeStyle.gradientBuilder(
                            Color.RED,
                            Color.YELLOW
                        ).build()
                    )
                )
        )
        // [END maps_android_polyline_gradient]
    }

    @SnippetItem(
        title = "11. Stamped Texture Polyline",
        description = "What it does: Stamps a repeating bitmap texture (walking dots) onto the polyline stroke using TextureStyle.\nHow to see the effect: A repeating sequence of walking dot icons renders along the polyline path.",
    )
    fun stampedPolyline() {
        // [START maps_android_polyline_stamped]
        val stampStyle =
            TextureStyle.newBuilder(BitmapDescriptorFactory.fromResource(R.drawable.walking_dot)).build()
        val span = StyleSpan(StrokeStyle.colorBuilder(Color.RED).stamp(stampStyle).build())
        map.addPolyline(
            PolylineOptions()
                .add(LatLng(47.6677146, -122.3470447), LatLng(47.6442757, -122.2814693))
                .addSpan(span)
        )
        // [END maps_android_polyline_stamped]
    }
}
