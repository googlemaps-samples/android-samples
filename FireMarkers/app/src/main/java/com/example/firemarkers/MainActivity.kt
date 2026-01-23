// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.firemarkers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.firemarkers.ui.theme.FireMarkersTheme
import com.example.firemarkers.viewmodel.MarkersViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.AndroidEntryPoint

import androidx.core.view.WindowCompat
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            FireMarkersTheme {
                MapScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/**
 * The main screen of the application. This composable function displays the Google Map,
 * observes the list of markers from the [MarkersViewModel], and renders them. It also
 * provides a dynamic TopAppBar that changes its controls based on whether the current
 * device is the "controller" of the animation or an "agent" observing it.
 *
 * @param modifier The modifier to be applied to the MapScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val viewModel: MarkersViewModel = hiltViewModel()
    val markers by viewModel.markers.collectAsState()
    val animationRunning by viewModel.animationRunning.collectAsState()
    val hasMarkers by viewModel.hasMarkers.collectAsState()
    val isController by viewModel.isController.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val boulder = LatLng(40.0150, -105.2705)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(boulder, 10f)
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FireMarkers",
                        fontSize = 20.sp
                    )
                },
                actions = {
                    if (isController) {
                        Text("🎄") // Tree emoji for controller
                        IconButton(
                            onClick = { viewModel.toggleAnimation() },
                            enabled = hasMarkers
                        ) {
                            Icon(
                                imageVector = if (animationRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Animate Shape"
                            )
                        }
                        IconButton(onClick = { viewModel.seedDatabase() }) {
                            Icon(
                                imageVector = Icons.Default.AddLocation,
                                contentDescription = "Seed Database"
                            )
                        }
                        IconButton(onClick = { viewModel.clearMarkers() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Markers"
                            )
                        }
                    } else {
                        // Agent UI
                        IconButton(onClick = { viewModel.takeControl() }) {
                            Text("🎃") // Pumpkin emoji for agent
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            markers.forEach { markerData ->
                Marker(
                    state = MarkerState(position = LatLng(markerData.latitude, markerData.longitude)),
                    title = markerData.label,
                    icon = BitmapDescriptorFactory.defaultMarker(markerData.color)
                )
            }
        }
    }
}
