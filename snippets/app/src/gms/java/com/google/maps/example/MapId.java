package com.google.maps.example;

import android.content.Context;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;

class MapId {

  private void fragment() {
    // [START maps_android_support_map_fragment_map_id]
    GoogleMapOptions options = new GoogleMapOptions()
        .mapId("YOUR_MAP_ID");
    SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
    // [END maps_android_support_map_fragment_map_id]
  }

  private void mapView(Context context) {
    // [START maps_android_mapview_map_id]
    GoogleMapOptions options = new GoogleMapOptions()
        .mapId("YOUR_MAP_ID");
    MapView mapView = new MapView(context, options);
    // [END maps_android_mapview_map_id]
  }
}
