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
import android.widget.SeekBar
import android.widget.TextView
import com.example.kotlindemos.R

/**
 * Fragment with "width" UI controls for Polylines, to be used in ViewPager.
 */
class PolylineWidthControlFragment : PolylineControlFragment(), SeekBar.OnSeekBarChangeListener {
    private lateinit var widthBar: SeekBar
    private lateinit var widthTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view = inflater.inflate(com.example.common_ui.R.layout.polyline_width_control_fragment, container, false)
        widthBar = view.findViewById(com.example.common_ui.R.id.widthSeekBar)
        widthBar.max = WIDTH_MAX
        widthBar.setOnSeekBarChangeListener(this)
        widthTextView = view.findViewById(com.example.common_ui.R.id.widthTextView)
        return view
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Don't do anything here.
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Don't do anything here.
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        val polyline = polyline ?:  return
        polyline.width = progress.toFloat()
        widthTextView.text = polyline.width.toString() + "px"
    }


    override fun refresh() {
        val polyline = polyline
        if (polyline == null) {
            widthBar.isEnabled = false
            widthBar.progress = 0
            widthTextView.text = ""
            return
        }
        widthBar.isEnabled = true
        val width: Float = polyline.width
        widthBar.progress = width.toInt()
        widthTextView.text = width.toString() + "px"
    }

    companion object {
        private const val WIDTH_MAX = 50
    }
}