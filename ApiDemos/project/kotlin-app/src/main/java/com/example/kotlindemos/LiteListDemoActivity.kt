/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kotlindemos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * This shows to include a map in lite mode in a RecyclerView.
 * Note the use of the view holder pattern with the
 * [com.google.android.gms.maps.OnMapReadyCallback].
 */
class LiteListDemoActivity : SamplesBaseActivity() {

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    private val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(this, 2)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var mapAdapter: RecyclerView.Adapter<MapAdapter.ViewHolder>

    /**
     * RecycleListener that completely clears the [com.google.android.gms.maps.GoogleMap]
     * attached to a row in the RecyclerView.
     * Sets the map type to [com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE] and clears
     * the map.
     */
    private val recycleListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as MapAdapter.ViewHolder
        mapHolder.clearView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.common_ui.R.layout.lite_list_demo)

        mapAdapter = MapAdapter()

        // Initialise the RecyclerView.
        recyclerView = findViewById<RecyclerView>(com.example.common_ui.R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = mapAdapter
            setRecyclerListener(recycleListener)
        }
        applyInsets(findViewById<View?>(com.example.common_ui.R.id.map_container))
    }

    /** Create options menu to switch between the linear and grid layout managers. */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lite_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        recyclerView.layoutManager = when (item.itemId) {
            com.example.common_ui.R.id.layout_linear -> linearLayoutManager
            com.example.common_ui.R.id.layout_grid -> gridLayoutManager
            else -> return false
        }
        return true
    }

    /**
     * Adapter that displays a title and [com.google.android.gms.maps.MapView] for each item.
     * The layout is defined in `lite_list_demo_row.xml`. It contains a MapView
     * that is programatically initialised when onCreateViewHolder is called.
     */
    inner class MapAdapter : RecyclerView.Adapter<MapAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindView(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflated = LayoutInflater.from(parent.context)
                    .inflate(com.example.common_ui.R.layout.lite_list_demo_row, parent, false)
            return ViewHolder(inflated)
        }

        override fun getItemCount() = listLocations.size

        /** A view holder for the map and title. */
        inner class ViewHolder(view: View) :
                RecyclerView.ViewHolder(view),
                OnMapReadyCallback {

            private val layout: View = view
            private val mapView: MapView = layout.findViewById(com.example.common_ui.R.id.lite_listrow_map)
            private val title: TextView = layout.findViewById(com.example.common_ui.R.id.lite_listrow_text)
            private lateinit var map: GoogleMap
            private lateinit var latLng: LatLng

            /** Initialises the MapView by calling its lifecycle methods */
            init {
                with(mapView) {
                    // Initialise the MapView
                    onCreate(null)
                    // Set the map ready callback to receive the GoogleMap object
                    getMapAsync(this@ViewHolder)
                }
            }

            private fun setMapLocation() {
                if (!::map.isInitialized) return
                with(map) {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                    addMarker(MarkerOptions().position(latLng))
                    mapType = GoogleMap.MAP_TYPE_NORMAL
                    setOnMapClickListener {
                        Toast.makeText(this@LiteListDemoActivity, "Clicked on ${title.text}",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onMapReady(googleMap: GoogleMap) {
                MapsInitializer.initialize(applicationContext)
                // If map is not initialised properly
                map = googleMap
                setMapLocation()
            }

            /** This function is called when the RecyclerView wants to bind the ViewHolder. */
            fun bindView(position: Int) {
                listLocations[position].let {
                    latLng = it.second
                    mapView.tag = this
                    title.text = it.first
                    // We need to call setMapLocation from here because RecyclerView might use the
                    // previously loaded maps
                    setMapLocation()
                }
            }

            /** This function is called by the recycleListener, when we need to clear the map. */
            fun clearView() {
                with(map) {
                    // Clear the map and free up resources by changing the map type to none
                    clear()
                    mapType = GoogleMap.MAP_TYPE_NONE
                }
            }
        }
    }

    /** A list of locations of to show in the RecyclerView */
    private val listLocations: List<Pair<String, LatLng>> = listOf(
            Pair("Cape Town", LatLng(-33.920455, 18.466941)),
            Pair("Beijing", LatLng(39.937795, 116.387224)),
            Pair("Bern", LatLng(46.948020, 7.448206)),
            Pair("Breda", LatLng(51.589256, 4.774396)),
            Pair("Brussels", LatLng(50.854509, 4.376678)),
            Pair("Copenhagen", LatLng(55.679423, 12.577114)),
            Pair("Hannover", LatLng(52.372026, 9.735672)),
            Pair("Helsinki", LatLng(60.169653, 24.939480)),
            Pair("Hong Kong", LatLng(22.325862, 114.165532)),
            Pair("Istanbul", LatLng(41.034435, 28.977556)),
            Pair("Johannesburg", LatLng(-26.202886, 28.039753)),
            Pair("Lisbon", LatLng(38.707163, -9.135517)),
            Pair("London", LatLng(51.500208, -0.126729)),
            Pair("Madrid", LatLng(40.420006, -3.709924)),
            Pair("Mexico City", LatLng(19.427050, -99.127571)),
            Pair("Moscow", LatLng(55.750449, 37.621136)),
            Pair("New York", LatLng(40.750580, -73.993584)),
            Pair("Oslo", LatLng(59.910761, 10.749092)),
            Pair("Paris", LatLng(48.859972, 2.340260)),
            Pair("Prague", LatLng(50.087811, 14.420460)),
            Pair("Rio de Janeiro", LatLng(-22.90187, -43.232437)),
            Pair("Rome", LatLng(41.889998, 12.500162)),
            Pair("Sao Paolo", LatLng(-22.863878, -43.244097)),
            Pair("Seoul", LatLng(37.560908, 126.987705)),
            Pair("Stockholm", LatLng(59.330650, 18.067360)),
            Pair("Sydney", LatLng(-33.873651, 151.2068896)),
            Pair("Taipei", LatLng(25.022112, 121.478019)),
            Pair("Tokyo", LatLng(35.670267, 139.769955)),
            Pair("Tulsa Oklahoma", LatLng(36.149777, -95.993398)),
            Pair("Vaduz", LatLng(47.141076, 9.521482)),
            Pair("Vienna", LatLng(48.209206, 16.372778)),
            Pair("Warsaw", LatLng(52.235474, 21.004057)),
            Pair("Wellington", LatLng(-41.286480, 174.776217)),
            Pair("Winnipeg", LatLng(49.875832, -97.150726))
    )
}
