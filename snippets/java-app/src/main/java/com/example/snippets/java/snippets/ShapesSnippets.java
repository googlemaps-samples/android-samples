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

import android.graphics.Color;
import com.example.snippets.java.R;
import com.example.snippets.java.TrackedMap;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
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
import com.google.android.gms.maps.model.StampStyle;
import com.google.android.gms.maps.model.StrokeStyle;
import com.google.android.gms.maps.model.StyleSpan;
import com.google.android.gms.maps.model.TextureStyle;
import java.util.Arrays;
import java.util.List;

@SnippetGroup(
        title = "Shapes",
        description = "Snippets demonstrating shapes, custom styled polylines, polygons, and circles."
)
public class ShapesSnippets {

    private final TrackedMap map;

    public ShapesSnippets(TrackedMap map) {
        this.map = map;
    }

    @SnippetItem(
            title = "1. Simple Polyline",
            description = "What it does: Creates a polyline path and connects sequential coordinates to form a rectangular boundary line.\nHow to see the effect: A solid stroke outline connects four geographic coordinates on the map."
    )
    public void polylines() {
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
        if (polyline != null) {
            boolean isClickable = polyline.isClickable();
            boolean isGeodesic = polyline.isGeodesic();
            boolean isVisible = polyline.isVisible();
            int color = polyline.getColor();
            float width = polyline.getWidth();
            float zIndex = polyline.getZIndex();
            List<LatLng> points = polyline.getPoints();
        }
        // [END maps_android_shapes_polylines_polylineoptions]
    }

    @SnippetItem(
            title = "2. Simple Polygon",
            description = "What it does: Constructs a filled polygon covering a rectangular geographic region.\nHow to see the effect: A solid filled rectangular polygon shape overlays the map surface."
    )
    public void polygons() {
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

    @SnippetItem(
            title = "3. Polygon Autocompletion",
            description = "What it does: Demonstrates that open coordinate paths automatically join the last point back to origin.\nHow to see the effect: Unclosed polygon vertices automatically connect to form a closed, filled shape."
    )
    public void polygonAutocompletion() {
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

    @SnippetItem(
            title = "4. Hollow Polygon",
            description = "What it does: Adds interior coordinate hole paths (cutouts) inside an outer filled polygon boundary.\nHow to see the effect: A solid polygon displays with a transparent window cutout in its center."
    )
    public void polygonHollow() {
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
        if (hollowPolygon != null) {
            boolean isClickable = hollowPolygon.isClickable();
            boolean isGeodesic = hollowPolygon.isGeodesic();
            boolean isVisible = hollowPolygon.isVisible();
            int fillColor = hollowPolygon.getFillColor();
            int strokeColor = hollowPolygon.getStrokeColor();
            float strokeWidth = hollowPolygon.getStrokeWidth();
            float zIndex = hollowPolygon.getZIndex();
            List<LatLng> points = hollowPolygon.getPoints();
            List<List<LatLng>> holes = hollowPolygon.getHoles();
        }
        // [END maps_android_shapes_polygons_hollow]
    }

    @SnippetItem(
            title = "5. Circle",
            description = "What it does: Draws a circular shape specified by center coordinates and radius in meters.\nHow to see the effect: A circular overlay ring appears surrounding the center point with a 1,000 meter radius."
    )
    public void circles() {
        // [START maps_android_shapes_circles_circleoptions]
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
            .center(new LatLng(37.4, -122.1))
            .radius(1000); // In meters

        // Get back the mutable Circle
        Circle circle = map.addCircle(circleOptions);
        if (circle != null) {
            boolean isClickable = circle.isClickable();
            boolean isVisible = circle.isVisible();
            LatLng center = circle.getCenter();
            double radius = circle.getRadius();
            int fillColor = circle.getFillColor();
            float strokeWidth = circle.getStrokeWidth();
            float zIndex = circle.getZIndex();
        }
        // [END maps_android_shapes_circles_circleoptions]
    }

    @SnippetItem(
            title = "6. Circle Click Event",
            description = "What it does: Attaches a touch listener directly to a clickable circle shape overlay.\nHow to see the effect: Tap inside the circle shape; its border stroke color immediately flips."
    )
    public void circlesEvents() {
        // [START maps_android_shapes_circles_events]
        Circle circle = map.addCircle(new CircleOptions()
            .center(new LatLng(37.4, -122.1))
            .radius(1000)
            .strokeWidth(10)
            .strokeColor(Color.GREEN)
            .fillColor(Color.argb(128, 255, 0, 0))
            .clickable(true));

        map.getDelegate().setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                // Flip the r, g and b components of the circle's stroke color.
                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);
            }
        });
        // [END maps_android_shapes_circles_events]
    }

    @SnippetItem(
            title = "7. Custom Polyline Appearance",
            description = "What it does: Configures custom stroke patterns (dots and dashes), joint types, and custom cap icons.\nHow to see the effect: The polyline line renders with a dashed pattern and custom arrow icon end-cap."
    )
    public void customAppearances() {
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

    @SnippetItem(
            title = "8. Associate Data Tag",
            description = "What it does: Attaches a custom metadata tag object ('A') to a clickable polyline.\nHow to see the effect: Click listeners can inspect the polyline's tag property for business logic routing."
    )
    public void associateData() {
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

    @SnippetItem(
            title = "9. Multicolored Polyline Spans",
            description = "What it does: Applies multiple StyleSpan stroke colors to distinct segments along a single polyline.\nHow to see the effect: The polyline shifts colors from red to green across different segment lengths."
    )
    public void multicoloredPolyline() {
        // [START maps_android_polyline_multicolored]
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(47.6677146,-122.3470447), new LatLng(47.6442757,-122.2814693))
                .addSpan(new StyleSpan(Color.RED))
                .addSpan(new StyleSpan(Color.GREEN)));
        // [END maps_android_polyline_multicolored]
    }

    @SnippetItem(
            title = "10. Multicolored Gradient Polyline",
            description = "What it does: Creates a smooth color gradient transition across a polyline segment using StrokeStyle.gradientBuilder.\nHow to see the effect: The polyline line smoothly blends from red to yellow along its continuous path."
    )
    public void multicoloredGradientPolyline() {
        // [START maps_android_polyline_gradient]
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(47.6677146,-122.3470447), new LatLng(47.6442757,-122.2814693))
                .addSpan(new StyleSpan(StrokeStyle.gradientBuilder(Color.RED, Color.YELLOW).build())));
        // [END maps_android_polyline_gradient]
    }

    @SnippetItem(
            title = "11. Stamped Texture Polyline",
            description = "What it does: Stamps a repeating bitmap texture (walking dots) onto the polyline stroke using TextureStyle.\nHow to see the effect: A repeating sequence of walking dot icons renders along the polyline path."
    )
    public void stampedPolyline() {
        // [START maps_android_polyline_stamped]
        StampStyle stampStyle =
                TextureStyle.newBuilder(BitmapDescriptorFactory.fromResource(R.drawable.walking_dot)).build();
        StyleSpan span = new StyleSpan(StrokeStyle.colorBuilder(Color.RED).stamp(stampStyle).build());
        map.addPolyline(new PolylineOptions()
                .add(new LatLng(47.6677146,-122.3470447), new LatLng(47.6442757,-122.2814693))
                .addSpan(span));
        // [END maps_android_polyline_stamped]
    }
}
