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

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;

/**
 * The main activity of the API library demo gallery.
 * The main layout lists the demonstrated features, with buttons to launch them.
 */
class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener, OnMapsSdkInitializedCallback {

    private val TAG = MainActivity::class.java.simpleName

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val demo: DemoDetails = parent?.adapter?.getItem(position) as DemoDetails
        startActivity(Intent(this, demo.activityClass))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listAdapter: ListAdapter = CustomArrayAdapter(this, DemoDetailsList.DEMOS)

        // Find the view that will show empty message if there is no demo in DemoDetailsList.DEMOS
        val emptyMessage = findViewById<View>(R.id.empty)
        with(findViewById<ListView>(R.id.list)) {
            adapter = listAdapter
            onItemClickListener = this@MainActivity
            emptyView = emptyMessage
        }

        if (BuildConfig.MAPS_API_KEY.isEmpty()) {
            Toast.makeText(this, "Add your own API key in local.properties as MAPS_API_KEY=YOUR_API_KEY", Toast.LENGTH_LONG).show()
        }

        val spinner = findViewById<Spinner>(R.id.map_renderer_spinner)
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.map_renderer_spinner_array, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val preferredRendererName = spinner.selectedItem as String
                val preferredRenderer: Renderer?
                preferredRenderer = if (preferredRendererName == getString(R.string.latest)) {
                    Renderer.LATEST
                } else if (preferredRendererName == getString(R.string.legacy)) {
                    Renderer.LEGACY
                } else if (preferredRendererName == getString(R.string.default_renderer)) {
                    null
                } else {
                    Log.i(
                        TAG,
                        "Error setting renderer with name $preferredRendererName"
                    )
                    return
                }
                MapsInitializer.initialize(applicationContext, preferredRenderer, this@MainActivity)

                // Disable spinner since renderer cannot be changed once map is intitialized.
                spinner.isEnabled = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onMapsSdkInitialized(renderer: Renderer) {
        Toast.makeText(
            this,
            "All demo activities will use the $renderer renderer.",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     *
     * @property context current activity
     * @property demos An array containing the details of the demos to be displayed.
     */
    @SuppressLint("ResourceType")
    class CustomArrayAdapter(context: Context, demos: List<DemoDetails>) :
            ArrayAdapter<DemoDetails>(context, R.layout.feature, demos) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val demo: DemoDetails? = getItem(position)
            return (convertView as? FeatureView ?: FeatureView(context)).apply {
                if (demo != null) {
                    setTitleId(demo.titleId)
                    setDescriptionId(demo.descriptionId)
                    contentDescription = resources.getString(demo.titleId)
                }
            }
        }
    }
}
