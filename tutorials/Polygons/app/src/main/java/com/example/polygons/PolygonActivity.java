package com.example.polygons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.polygons.R.id.map;

/**
 * An activity that displays a Google map with polygons to represent areas, and
 * polylines to represent paths or routes.
 */
public class PolygonActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener,
        GoogleMap.OnPolylineClickListener {

    private static final int PATTERN_GAP_LENGTH = 20;
    private static final int PATTERN_DASH_LENGTH = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        Polyline polyline1 = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .add(new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // Style the polyline.
        stylePolyline(polyline1);

        Polyline polyline2 = googleMap.addPolyline((new PolylineOptions())
                .clickable(true)
                .add(new LatLng(-29.501, 119.700),
                        new LatLng(-27.456, 119.672),
                        new LatLng(-25.971, 124.187),
                        new LatLng(-28.081, 126.555),
                        new LatLng(-28.848, 124.229),
                        new LatLng(-28.215, 123.938)));
        polyline2.setTag("B");
        stylePolyline(polyline2);

        // Add polygons to indicate areas on the map.
        Polygon polygon1 = googleMap.addPolygon((new PolygonOptions())
                .clickable(true)
                .add(new LatLng(-27.457, 153.040),
                        new LatLng(-33.852, 151.211),
                        new LatLng(-37.813, 144.962),
                        new LatLng(-34.928, 138.599)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon1.setTag("alpha");
        // Style the polygon.
        stylePolygon(polygon1);

        Polygon polygon2 = googleMap.addPolygon((new PolygonOptions())
                .clickable(true)
                .add(new LatLng(-31.673, 128.892),
                        new LatLng(-31.952, 115.857),
                        new LatLng(-17.785, 122.258),
                        new LatLng(-12.4258, 130.7932)));
        polygon2.setTag("beta");
        stylePolygon(polygon2);

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));

        // Set listeners for click events.
        googleMap.setOnPolygonClickListener(this);
        googleMap.setOnPolylineClickListener(this);
    }

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at one end of the line.
                polyline.setEndCap(
                        new CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow_black_48dp),
                                16));
                break;
            case "B":
                // Use a round cap at the end of the line.
                polyline.setEndCap(new RoundCap());
                break;
        }

        polyline.setStartCap(new RoundCap());
        polyline.setWidth(12);
        polyline.setColor(0xff000000);
        polyline.setJointType((JointType.ROUND));
    }

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> patterns = new ArrayList<>();
        int strokeColor = 0xff000000;
        int fillColor = 0xffffffff;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Create a stroke pattern of dashes, and define colors.
                patterns.add(new Gap(PATTERN_GAP_LENGTH));
                patterns.add(new Dash(PATTERN_DASH_LENGTH));
                strokeColor = 0xff388E3C;
                fillColor = 0xff81C784;
                break;
            case "beta":
                // Create a stroke pattern of dots and dashes, and define colors.
                patterns.add(new Dot());
                patterns.add(new Gap(PATTERN_GAP_LENGTH));
                patterns.add(new Dash(PATTERN_DASH_LENGTH));
                patterns.add(new Gap(PATTERN_GAP_LENGTH));
                strokeColor = 0xffF57F17;
                fillColor = 0xffF9A825;
                break;
        }

        polygon.setStrokePattern(patterns);
        polygon.setStrokeWidth(8);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    /**
     * Listens for clicks on a polygon.
     * @param polygon The polygon object that the user has clicked.
     */
    @Override
    public void onPolygonClick(Polygon polygon) {
        // Flip the values of the r, g and b components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);

        Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        Dot dot = new Dot();
        List<PatternItem> patterns = new ArrayList<>();
        if (polyline.getPattern() == null || !polyline.getPattern().contains(dot)) {
            patterns.add(new Gap(PATTERN_GAP_LENGTH));
            patterns.add(dot);
            polyline.setPattern(patterns);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }
}
