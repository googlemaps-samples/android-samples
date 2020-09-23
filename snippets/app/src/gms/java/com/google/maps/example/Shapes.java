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

package com.google.maps.example;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.Arrays;
import java.util.List;

class Shapes {
    private GoogleMap map;

    private void polylines() {
        // [START maps_android_shapes_polylines_polylineoptions]
        // Instantiates a new Polyline object and adds points to define a rectangle
        PolylineOptions polylineOptions = new PolylineOptions()
            .add(new LatLng(37.35, -122.0))
            .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
            .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
            .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
            .add(new LatLng(37.35, -122.0)); // Closes the polyline.

        // Get back the mutable Polyline
        Polyline polyline = map.addPolyline(polylineOptions);
        // [END maps_android_shapes_polylines_polylineoptions]
    }

    private void polygons() {
        // [START maps_android_shapes_polygons_polygonoptions]
        // Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions polygonOptions = new PolygonOptions()
            .add(new LatLng(37.35, -122.0),
                new LatLng(37.45, -122.0),
                new LatLng(37.45, -122.2),
                new LatLng(37.35, -122.2),
                new LatLng(37.35, -122.0));

        // Get back the mutable Polygon
        Polygon polygon = map.addPolygon(polygonOptions);
        // [END maps_android_shapes_polygons_polygonoptions]
    }

    private void polygonAutocompletion() {
        // [START maps_android_shapes_polygons_autocompletion]
        Polygon polygon1 = map.addPolygon(new PolygonOptions()
            .add(new LatLng(0, 0),
                new LatLng(0, 5),
                new LatLng(3, 5),
                new LatLng(0, 0))
            .strokeColor(Color.RED)
            .fillColor(Color.BLUE));

        Polygon polygon2 = map.addPolygon(new PolygonOptions()
            .add(new LatLng(0, 0),
                new LatLng(0, 5),
                new LatLng(3, 5))
            .strokeColor(Color.RED)
            .fillColor(Color.BLUE));
        // [END maps_android_shapes_polygons_autocompletion]
    }

    private void polygonHollow() {
        // [START maps_android_shapes_polygons_hollow]
        List<LatLng> hole = Arrays.asList(new LatLng(1, 1),
            new LatLng(1, 2),
            new LatLng(2, 2),
            new LatLng(2, 1),
            new LatLng(1, 1));
        Polygon hollowPolygon = map.addPolygon(new PolygonOptions()
            .add(new LatLng(0, 0),
                new LatLng(0, 5),
                new LatLng(3, 5),
                new LatLng(3, 0),
                new LatLng(0, 0))
            .addHole(hole)
            .fillColor(Color.BLUE));
        // [END maps_android_shapes_polygons_hollow]
    }

    private void circles() {
        // [START maps_android_shapes_circles_circleoptions]
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
            .center(new LatLng(37.4, -122.1))
            .radius(1000); // In meters

        // Get back the mutable Circle
        Circle circle = map.addCircle(circleOptions);
        // [END maps_android_shapes_circles_circleoptions]
    }

    private void circlesEvents() {
        // [START maps_android_shapes_circles_events]
        Circle circle = map.addCircle(new CircleOptions()
            .center(new LatLng(37.4, -122.1))
            .radius(1000)
            .strokeWidth(10)
            .strokeColor(Color.GREEN)
            .fillColor(Color.argb(128, 255, 0, 0))
            .clickable(true));

        map.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                // Flip the r, g and b components of the circle's stroke color.
                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);
            }
        });
        // [END maps_android_shapes_circles_events]
    }

    private void customAppearances() {
        // [START maps_android_shapes_custom_appearances]
        Polyline polyline = map.addPolyline(new PolylineOptions()
            .add(new LatLng(-37.81319, 144.96298), new LatLng(-31.95285, 115.85734))
            .width(25)
            .color(Color.BLUE)
            .geodesic(true));
        // [END maps_android_shapes_custom_appearances]

        // [START maps_android_shapes_custom_appearances_stroke_pattern]
        List<PatternItem> pattern = Arrays.asList(
            new Dot(), new Gap(20), new Dash(30), new Gap(20));
        polyline.setPattern(pattern);
        // [END maps_android_shapes_custom_appearances_stroke_pattern]

        // [START maps_android_shapes_custom_appearances_joint_type]
        polyline.setJointType(JointType.ROUND);
        // [END maps_android_shapes_custom_appearances_joint_type]

        // [START maps_android_shapes_custom_appearances_start_cap]
        polyline.setStartCap(new RoundCap());
        // [END maps_android_shapes_custom_appearances_start_cap]

        // [START maps_android_shapes_custom_appearances_end_cap]
        polyline.setEndCap(
            new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow), 16));
        // [END maps_android_shapes_custom_appearances_end_cap]
    }

    private void associateData() {
        // [START maps_android_shapes_associate_data]
        Polyline polyline = map.addPolyline((new PolylineOptions())
            .clickable(true)
            .add(new LatLng(-35.016, 143.321),
                new LatLng(-34.747, 145.592),
                new LatLng(-34.364, 147.891),
                new LatLng(-33.501, 150.217),
                new LatLng(-32.306, 149.248),
                new LatLng(-32.491, 147.309)));

        polyline.setTag("A");
        // [END maps_android_shapes_associate_data]
    }
}
