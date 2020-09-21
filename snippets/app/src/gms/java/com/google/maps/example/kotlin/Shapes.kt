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

package com.google.maps.example.kotlin

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.example.R

internal class Shapes {

    private lateinit var map: GoogleMap

    private fun polylines() {
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
        // [END maps_android_shapes_polylines_polylineoptions]
    }

    private fun polygons() {
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

    private fun polygonAutocompletion() {
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

    private fun polygonHollow() {
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
        // [END maps_android_shapes_polygons_hollow]
    }

    private fun circles() {
        // [START maps_android_shapes_circles_circleoptions]
        // Instantiates a new CircleOptions object and defines the center and radius
        val circleOptions = CircleOptions()
            .center(LatLng(37.4, -122.1))
            .radius(1000.0) // In meters

        // Get back the mutable Circle
        val circle = map.addCircle(circleOptions)
        // [END maps_android_shapes_circles_circleoptions]
    }

    private fun circlesEvents() {
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
        map.setOnCircleClickListener {
            // Flip the r, g and b components of the circle's stroke color.
            val strokeColor = it.strokeColor xor 0x00ffffff
            it.strokeColor = strokeColor
        }
        // [END maps_android_shapes_circles_events]
    }

    private fun customAppearances() {
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
        polyline.pattern = pattern
        // [END maps_android_shapes_custom_appearances_stroke_pattern]

        // [START maps_android_shapes_custom_appearances_joint_type]
        polyline.jointType = JointType.ROUND
        // [END maps_android_shapes_custom_appearances_joint_type]

        // [START maps_android_shapes_custom_appearances_start_cap]
        polyline.startCap = RoundCap()
        // [END maps_android_shapes_custom_appearances_start_cap]

        // [START maps_android_shapes_custom_appearances_end_cap]
        polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow), 16F)
        // [END maps_android_shapes_custom_appearances_end_cap]
    }

    private fun associateData() {
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
        polyline.tag = "A"
        // [END maps_android_shapes_associate_data]
    }
}