package com.google.maps.example.kotlin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddAMap() {
  // [START maps_android_compose_add_a_map]
  val singapore = LatLng(1.35, 103.87)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(singapore, 10f)
  }
  GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState
  ) {
    Marker(
      state = MarkerState(position = singapore),
      title = "Singapore",
      snippet = "Marker in Singapore"
    )
  }
  // [END maps_android_compose_add_a_map]
}

@Composable
fun Properties() {
  // [START maps_android_compose_set_properties]
  var uiSettings by remember { mutableStateOf(MapUiSettings()) }
  var properties by remember {
    mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
  }

  Box(Modifier.fillMaxSize()) {
    GoogleMap(
      modifier = Modifier.matchParentSize(),
      properties = properties,
      uiSettings = uiSettings
    )
    Switch(
      checked = uiSettings.zoomControlsEnabled,
      onCheckedChange = {
        uiSettings = uiSettings.copy(zoomControlsEnabled = it)
      }
    )
  }
  // [END maps_android_compose_set_properties]
}
