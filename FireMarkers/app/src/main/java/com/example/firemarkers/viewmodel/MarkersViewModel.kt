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
package com.example.firemarkers.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firemarkers.data.FirebaseConnection
import com.example.firemarkers.data.ShapeData
import com.example.firemarkers.data.ShapePoint
import com.example.firemarkers.model.MarkerData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.ktx.utils.withSphericalLinearInterpolation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel for the FireMarkers application, demonstrating integration of Google Maps Platform
 * with Firebase Realtime Database for synchronized marker animations.
 *
 * This class serves as a reference implementation for Android developers looking to integrate
 * Google Maps with Firebase. It illustrates basic concepts of real-time data synchronization
 * and animated map markers.
 *
 * **Note:** While this ViewModel demonstrates Firebase integration, the Firebase-specific
 * implementation aspects are for illustrative purposes and may not represent the most
 * optimized or production-ready patterns for all Firebase use cases. The primary focus
 * is on showcasing Google Maps Platform capabilities with a real-time backend.
 *
 * It manages the state for the map screen, including the markers and their synchronized animation.
 *
 * This ViewModel is the core of the application's logic, orchestrating data flow between the
 * Firebase Realtime Database and the UI. It employs a controller/agent architecture to ensure
 * that animations are perfectly synchronized across all connected devices.
 *
 * ### Architecture and Synchronization
 *
 * A key concept is the **controller/agent** model. At any given time, only one device acts as the
 * "controller." This device is responsible for:
 * 1.  Running the animation loop (`startAnimationDriver`).
 * 2.  Calculating the animation's progress (`fraction`).
 * 3.  Writing the updated animation state to the `/animation` node in Firebase.
 * 4.  Seeding the initial marker data or clearing all markers.
 *
 * All other connected devices act as "agents." They passively listen for changes to the `/animation`
 * node and update their UI accordingly. A device can become the controller by calling `takeControl()`.
 *
 * The shared state, including whether the animation is running, its progress, and the current
 * controller's ID, is stored in the `AnimationState` data class and persisted in Firebase.
 *
 * ### State Management and Data Flow
 *
 * This ViewModel uses `StateFlow` to expose data to the Compose UI in a reactive way.
 * - **`_markers`**: A private `MutableStateFlow` holding the raw list of `MarkerData` fetched
 *   from the `/markers` node in Firebase.
 * - **`_animationStateDB`**: A private `MutableStateFlow` that mirrors the state of the `/animation`
 *   node in Firebase.
 * - **`markers`**: A public `StateFlow` that is the main output for the UI. It's created by using
 *   the `combine` operator on `_markers` and `_animationStateDB`. For each emission, it calculates
 *   the new interpolated geographic coordinates and colors for every marker based on the current
 *   animation `fraction`, ensuring a smooth visual transition.
 * - **`isController`**, **`animationRunning`**, **`hasMarkers`**: These are simple `StateFlow`s
 *   derived from the primary state flows to control UI elements like buttons and icons.
 *
 * @param firebaseConnection The Hilt-injected provider for the Firebase Realtime Database instance.
 */
@HiltViewModel
class MarkersViewModel @Inject constructor(
    private val firebaseConnection: FirebaseConnection
) : ViewModel() {

    internal val viewModelId = UUID.randomUUID().toString().substring(0, 4)
    private val _markers = MutableStateFlow<List<MarkerData>>(emptyList())
    private val _animationStateDB = MutableStateFlow(AnimationState())
    private var animationJob: Job? = null

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents = _errorEvents.asSharedFlow()

    val markers: StateFlow<List<MarkerData>> = combine(
        _markers,
        _animationStateDB
    ) { markers, animState ->
        if (markers.isEmpty()) {
            markers
        } else {
            updateMarkers(markers, animState.fraction)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val animationRunning: StateFlow<Boolean> = _animationStateDB.map { it.running }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val hasMarkers: StateFlow<Boolean> = _markers.map { it.isNotEmpty() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val isController: StateFlow<Boolean> = _animationStateDB.map { it.controllerId == viewModelId }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        Log.d(TAG, "[$viewModelId] ViewModel initialized.")
        listenForMarkerUpdates()
        listenForAnimationState()
    }

    private fun listenForMarkerUpdates() {
        firebaseConnection.database.getReference("markers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val markerList = snapshot.children.mapNotNull { it.getValue(MarkerData::class.java) }
                    _markers.value = markerList
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error, "markers")
                }
            })
    }

    private fun listenForAnimationState() {
        firebaseConnection.database.getReference("animation")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val animState = snapshot.getValue(AnimationState::class.java) ?: AnimationState()
                    _animationStateDB.value = animState
                    Log.d(TAG, "[$viewModelId] DB anim state received: $animState")


                    val shouldDrive = animState.controllerId == viewModelId && animState.running
                    val isDriving = animationJob?.isActive == true

                    Log.d(TAG, "[$viewModelId] Evaluating driver state: shouldDrive=$shouldDrive, isDriving=$isDriving")

                    if (shouldDrive && !isDriving) {
                        Log.d(TAG, "[$viewModelId] Condition met: Starting animation driver.")
                        startAnimationDriver()
                    } else if (!shouldDrive && isDriving) {
                        Log.d(TAG, "[$viewModelId] Condition met: Stopping animation driver.")
                        animationJob?.cancel()
                        animationJob = null
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    handleDatabaseError(error, "animation")
                }
            })
    }

    private fun handleDatabaseError(error: DatabaseError, context: String) {
        val msg = if (error.code == DatabaseError.PERMISSION_DENIED) {
            "Permission Denied ($context): Check your Firebase Database Rules."
        } else {
            "Database error ($context): ${error.message}"
        }
        Log.e(TAG, "[$viewModelId] $msg")
        viewModelScope.launch {
            _errorEvents.emit(msg)
        }
    }

    /**
     * Toggles the animation state (running/paused) in Firebase.
     *
     * This action is only permitted if the current ViewModel instance is the designated controller.
     * If a non-controller attempts to toggle the animation, the action is ignored.
     * The updated animation state is written to the `/animation` node in Firebase, which
     * then propagates to all connected clients.
     */
    fun toggleAnimation() {
        if (_animationStateDB.value.controllerId != viewModelId) {
            Log.w(TAG, "[$viewModelId] Agent tried to toggle animation. Ignoring.")
            return
        }

        val animRef = firebaseConnection.database.getReference("animation")
        val currentState = _animationStateDB.value
        val newState = currentState.copy(running = !currentState.running)
        Log.d(TAG, "[$viewModelId] toggleAnimation: Writing new state to DB: $newState")
        animRef.setValue(newState.toMap())
    }

    /**
     * Attempts to designate the current ViewModel instance as the animation controller.
     *
     * This updates the `controllerId` in the `/animation` node in Firebase to this ViewModel's
     * unique ID. When successful, this instance will become responsible for driving the animation.
     * The animation is automatically paused when control is taken.
     */
    fun takeControl() {
        Log.d(TAG, "[$viewModelId] takeControl: Attempting to become controller.")
        val animRef = firebaseConnection.database.getReference("animation")
        val currentState = _animationStateDB.value
        val newState = currentState.copy(
            controllerId = viewModelId,
            running = false
        )
        Log.d(TAG, "[$viewModelId] takeControl: Writing new state to DB: $newState")
        animRef.setValue(newState.toMap())
    }

    private fun startAnimationDriver() {
        animationJob?.cancel()
        animationJob = viewModelScope.launch {
            var loopState = _animationStateDB.value
            Log.d(TAG, "[$viewModelId] Animation driver started with initial state: $loopState")

            while (isActive) {
                val animRef = firebaseConnection.database.getReference("animation")

                var nextFraction = loopState.fraction + loopState.direction * ANIMATION_STEP_SIZE
                var nextDirection = loopState.direction

                if (nextFraction >= 1.0 || nextFraction <= 0.0) {
                    nextFraction = nextFraction.coerceIn(0.0, 1.0)
                    nextDirection *= -1
                }

                loopState = loopState.copy(
                    fraction = nextFraction,
                    direction = nextDirection
                )
                Log.d(TAG, "[$viewModelId] Driver loop tick: Writing new state to DB: $loopState")
                animRef.setValue(loopState.toMap())

                if (nextFraction >= 1.0 || nextFraction <= 0.0) {
                    delay(PAUSE_DURATION)
                } else {
                    delay(ANIMATION_DELAY)
                }
            }
        }
    }

    private fun updateMarkers(markers: List<MarkerData>, fraction: Double): List<MarkerData> {
        val newLocations = Shape.locationPairs.map { (startPoint, endPoint) ->
            startPoint.withSphericalLinearInterpolation(endPoint, fraction)
        }
        val colors = Shape.colorPairs.map { (a, b) -> a + (b - a) * fraction }

        return markers.mapIndexed { index, marker ->
            marker.copy(
                latitude = newLocations[index].latitude,
                longitude = newLocations[index].longitude,
                label = if (fraction > 0.5) Shape.Tree.label else Shape.JackOLantern.label,
                color = colors[index].toFloat()
            )
        }
    }

    /**
     * Seeds the Firebase Realtime Database with an initial set of marker data.
     *
     * This operation is only allowed if the current ViewModel instance is the controller,
     * or if no controller is currently assigned (allowing the first user to initialize the data).
     * It populates the `/markers` node with a predefined set of `MarkerData` objects
     * representing the "Jack-o'-lantern" shape.
     */
    fun seedDatabase() {
        // Only the controller can seed the database.
        // The `isNotEmpty` check allows the very first user of the app (when no controller
        // is assigned yet) to claim control and seed the initial data.
        if (_animationStateDB.value.controllerId != viewModelId && _animationStateDB.value.controllerId.isNotEmpty()) {
            Log.w(TAG, "[$viewModelId] Agent cannot seed. Take control first.")
            return
        }
        Log.d(TAG, "[$viewModelId] Seeding database as controller.")
        val animState = AnimationState(controllerId = viewModelId)
        firebaseConnection.database.getReference("animation").setValue(animState.toMap())
        viewModelScope.launch {
            val databaseReference = firebaseConnection.database.getReference("markers")
            val markersToSeed = Shape.JackOLantern.shapeData.map { shapePoint ->
                val markerId = databaseReference.push().key ?: ""
                MarkerData(
                    id = markerId,
                    latitude = BOULDER_LAT_LNG.latitude + shapePoint.latOffset,
                    longitude = BOULDER_LAT_LNG.longitude + shapePoint.lonOffset,
                    label = "Pumpkin",
                    style = "jack-o-lantern",
                    color = shapePoint.color
                )
            }
            databaseReference.setValue(markersToSeed)
        }
    }

    /**
     * Clears all marker data from the Firebase Realtime Database.
     *
     * This operation is only allowed if the current ViewModel instance is the controller,
     * or if no controller is currently assigned (allowing the first user to clear data).
     * It removes all data from the `/markers` node in Firebase and resets the animation state.
     */
    fun clearMarkers() {
        if (_animationStateDB.value.controllerId != viewModelId && _animationStateDB.value.controllerId.isNotEmpty()) {
            Log.w(TAG, "[$viewModelId] Agent cannot clear. Take control first.")
            return
        }
        Log.d(TAG, "[$viewModelId] Clearing markers as controller.")
        firebaseConnection.database.getReference("animation").setValue(AnimationState(controllerId = viewModelId))
        firebaseConnection.database.getReference("markers").removeValue()
    }

    /**
     * Represents the complete, synchronized state of the animation.
     * This state is stored in the Firebase Realtime Database under the `/animation` node
     * and serves as the single source of truth for all connected clients.
     *
     * @property running True if the animation should be playing, false if paused.
     *                   Note: This property is named `running` instead of `isRunning` to avoid
     *                   a known issue with Firebase's automatic deserialization of Kotlin
     *                   data classes where properties prefixed with "is" may not be correctly
     *                   mapped.
     * @property fraction The current progress of the animation, from 0.0 (start shape) to 1.0 (end shape).
     * @property direction The current direction of the animation (-1.0 or 1.0).
     * @property controllerId The unique ID of the ViewModel instance currently driving the animation.
     * @property timestamp The server-side timestamp of the last state update.
     */
    internal data class AnimationState(
        val running: Boolean = false,
        val fraction: Double = 0.0,
        val direction: Double = 1.0,
        val controllerId: String = "",
        val timestamp: Long = 0
    ) {
        /**
         * Converts this object to a Map for writing to Firebase.
         * This is necessary to include the `ServerValue.TIMESTAMP`, which allows Firebase
         * to write a consistent, server-side timestamp for when the update occurred.
         */
        fun toMap(): Map<String, Any> {
            return mapOf(
                "running" to running,
                "fraction" to fraction,
                "direction" to direction,
                "controllerId" to controllerId,
                "timestamp" to ServerValue.TIMESTAMP
            )
        }
    }

    private sealed class Shape(val shapeData: List<ShapePoint>, val label: String) {
        val locations = shapeData.map {
            BOULDER_LAT_LNG + it
        }
        val colors = shapeData.map { it.color }

        data object Tree : Shape(ShapeData.christmasTreeShape, "Tree")
        data object JackOLantern : Shape(ShapeData.jackOLanternShape, "Pumpkin")

        companion object {
            val locationPairs by lazy { JackOLantern.locations.zip(Tree.locations) }
            val colorPairs by lazy { JackOLantern.colors.zip(Tree.colors) }
        }
    }

    companion object {
        private const val TAG = "MarkersViewModel"
        private val BOULDER_LAT_LNG = LatLng(40.0150, -105.2705)
        private val ANIMATION_DELAY = 100.milliseconds
        private val PAUSE_DURATION = 1000.milliseconds
        private const val ANIMATION_STEP_SIZE = 0.05
    }
}

private operator fun LatLng.plus(other: ShapePoint): LatLng {
    return LatLng(other.latOffset + this.latitude, other.lonOffset + this.longitude)
}