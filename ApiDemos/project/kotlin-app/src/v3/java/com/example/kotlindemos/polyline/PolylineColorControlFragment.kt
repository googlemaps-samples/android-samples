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

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.example.kotlindemos.R

/** Fragment with "color" UI controls for Polylines, to be used in ViewPager.  */
class PolylineColorControlFragment : PolylineControlFragment(), OnSeekBarChangeListener {
    private lateinit var alphaBar: SeekBar
    private lateinit var hueBar: SeekBar
    private lateinit var argbTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view: View = inflater.inflate(com.example.common_ui.R.layout.polyline_color_control_fragment, container, false)
        alphaBar = view.findViewById(com.example.common_ui.R.id.alphaSeekBar)
        alphaBar.max = ALPHA_MAX
        alphaBar.setOnSeekBarChangeListener(this)
        hueBar = view.findViewById(com.example.common_ui.R.id.hueSeekBar)
        hueBar.max = HUE_MAX
        hueBar.setOnSeekBarChangeListener(this)
        argbTextView = view.findViewById(com.example.common_ui.R.id.argbTextView) as TextView
        return view
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Don't do anything here.
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Don't do anything here.
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val polyline = polyline
        if (polyline == null || !fromUser) {
            return
        }
        if (seekBar === hueBar) {
            polyline.color = Color.HSVToColor(Color.alpha(polyline.color), floatArrayOf(progress.toFloat(), 1f, 1f))
        } else if (seekBar === alphaBar) {
            val prevHSV = FloatArray(3)
            Color.colorToHSV(polyline.color, prevHSV)
            polyline.color = Color.HSVToColor(progress, prevHSV)
        }
        argbTextView.text = String.format("0x%08X", polyline.color)
    }

    override fun refresh() {
        val polyline = polyline
        if (polyline == null) {
            alphaBar.isEnabled = false
            alphaBar.progress = 0
            hueBar.isEnabled = false
            hueBar.progress = 0
            argbTextView.text = ""
            return
        }
        val color: Int = polyline.color
        alphaBar.isEnabled = true
        alphaBar.progress = Color.alpha(color)
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hueBar.isEnabled = true
        hueBar.progress = hsv[0].toInt()
        argbTextView.text = String.format("0x%08X", color)
    }

    companion object {
        private const val HUE_MAX = 359
        private const val ALPHA_MAX = 255
    }
}