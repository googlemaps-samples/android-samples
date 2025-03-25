/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kotlindemos.polyline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.example.kotlindemos.R

/**
 * Fragment with clickability, geodesic, and visibility UI controls for Polylines, to be used in
 * ViewPager.
 */
class PolylineOtherOptionsControlFragment : PolylineControlFragment(), View.OnClickListener {
    private lateinit var clickabilityCheckBox: CheckBox
    private lateinit var geodesicCheckBox: CheckBox
    private lateinit var visibilityCheckBox: CheckBox

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view = inflater.inflate(com.example.common_ui.R.layout.polyline_other_options_control_fragment, container, false)
        clickabilityCheckBox = view.findViewById(com.example.common_ui.R.id.clickabilityCheckBox)
        clickabilityCheckBox.setOnClickListener(this)
        geodesicCheckBox = view.findViewById(com.example.common_ui.R.id.geodesicCheckBox)
        geodesicCheckBox.setOnClickListener(this)
        visibilityCheckBox = view.findViewById(com.example.common_ui.R.id.visibilityCheckBox)
        visibilityCheckBox.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        val polyline = polyline ?: return
        when {
            view === clickabilityCheckBox -> {
                polyline.isClickable = clickabilityCheckBox.isChecked
            }
            view === geodesicCheckBox -> {
                polyline.isGeodesic = geodesicCheckBox.isChecked
            }
            view === visibilityCheckBox -> {
                polyline.isVisible = visibilityCheckBox.isChecked
            }
        }
    }

    override fun refresh() {
        clickabilityCheckBox.isChecked = polyline != null && polyline?.isClickable ?: false
        geodesicCheckBox.isChecked = polyline != null && polyline?.isGeodesic ?: false
        visibilityCheckBox.isChecked = polyline != null && polyline?.isVisible ?: false
    }
}