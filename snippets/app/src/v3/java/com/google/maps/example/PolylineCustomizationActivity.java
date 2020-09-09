package com.google.maps.example;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.maps.GoogleMap;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.LatLng;
import com.google.android.libraries.maps.model.Polyline;
import com.google.android.libraries.maps.model.PolylineOptions;
import com.google.android.libraries.maps.model.StampStyle;
import com.google.android.libraries.maps.model.StrokeStyle;
import com.google.android.libraries.maps.model.StyleSpan;
import com.google.android.libraries.maps.model.TextureStyle;

public class PolylineCustomizationActivity extends AppCompatActivity {

    private GoogleMap map;

    private void multicoloredPolyline() {
        // [START maps_android_polyline_multicolored]
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(47.6677146,-122.3470447), new LatLng(47.6442757,-122.2814693))
                .addSpan(new StyleSpan(Color.RED))
                .addSpan(new StyleSpan(Color.GREEN)));
        // [END maps_android_polyline_multicolored]
    }

    private void multicoloredGradientPolyline() {
        // [START maps_android_polyline_gradient]
        Polyline line = map.addPolyline(new PolylineOptions()
                .add(new LatLng(47.6677146,-122.3470447), new LatLng(47.6442757,-122.2814693))
                .addSpan(new StyleSpan(StrokeStyle.gradientBuilder(Color.RED, Color.YELLOW).build())));
        // [END maps_android_polyline_gradient]
    }

    private void stampedPolyline() {
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