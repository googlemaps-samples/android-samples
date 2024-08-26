/*
 * Copyright 2023 Google LLC
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

package com.example.kotlindemos

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import java.util.ArrayList

class StyledMapDemoActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var mSelectedStyleId = R.string.style_label_default

    companion object {
        private const val TAG = "StyledMapDemoActivity"
        private const val SELECTED_STYLE = "selected_style"
        private val SYDNEY = LatLng(-33.8688, 151.2093)
    }

    private val mStyleIds = intArrayOf(
            R.string.style_label_retro,
            R.string.style_label_night,
            R.string.style_label_grayscale,
            R.string.style_label_no_pois_no_transit,
            R.string.style_label_default
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mSelectedStyleId = savedInstanceState.getInt(SELECTED_STYLE)
        }
        setContentView(R.layout.styled_map_demo)

        val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SELECTED_STYLE, mSelectedStyleId)
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 14f))
        setSelectedStyle()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.styled_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_style_choose) {
            showStylesDialog()
        }
        return true
    }

    private fun showStylesDialog() {
        val styleNames = ArrayList<String>()
        for (style in mStyleIds) {
            styleNames.add(getString(style))
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.style_choose))
        builder.setItems(styleNames.toTypedArray<CharSequence>(),
                DialogInterface.OnClickListener { _, which ->
                    mSelectedStyleId = mStyleIds[which]
                    val msg = getString(R.string.style_set_to, getString(mSelectedStyleId))
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    setSelectedStyle()
                })
        builder.show()
    }

    private fun setSelectedStyle() {
        val style: MapStyleOptions?
        val id = mSelectedStyleId
        style = when (id) {
            R.string.style_label_retro ->
                MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro)
            R.string.style_label_night ->
                MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night)
            R.string.style_label_grayscale ->
                MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_grayscale)
            R.string.style_label_no_pois_no_transit ->
                MapStyleOptions(
                        "[" +
                                "  {" +
                                "    \"featureType\":\"poi.business\"," +
                                "    \"elementType\":\"all\"," +
                                "    \"stylers\":[" +
                                "      {" +
                                "        \"visibility\":\"off\"" +
                                "      }" +
                                "    ]" +
                                "  }," +
                                "  {" +
                                "    \"featureType\":\"transit\"," +
                                "    \"elementType\":\"all\"," +
                                "    \"stylers\":[" +
                                "      {" +
                                "        \"visibility\":\"off\"" +
                                "      }" +
                                "    ]" +
                                "  }" +
                                "]"
                )
            R.string.style_label_default -> null
            else -> return
        }
        mMap?.setMapStyle(style)
    }
}
