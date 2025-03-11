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
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import com.example.kotlindemos.R
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.Polyline
import com.google.android.libraries.maps.model.StrokeStyle
import com.google.android.libraries.maps.model.StyleSpan
import com.google.android.libraries.maps.model.TextureStyle

/**
 * Fragment with span count UI controls for [com.google.android.libraries.maps.model.Polyline], to
 * be used in ViewPager.
 *
 *
 * When span count is updated from the slider, the selected polyline will be updated with that
 * number of spans. Each span will either have polyline color or the inverted color, and span
 * lengths are equally divided by number of segments in the polyline.
 */
class PolylineSpansControlFragment : PolylineControlFragment(), SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
    private lateinit var spanCountBar: SeekBar
    private lateinit var spanCountTextView: TextView
    private lateinit var gradientToggle: CompoundButton
    private lateinit var polylineStampStyleRadioGroup: RadioGroup
    private var spanCount = 0
    private val polylineSpanCounts = mutableMapOf<Polyline, Int>()
    private val polylineGradientStates = mutableMapOf<Polyline, Boolean>()
    private var isLiteMode = false
    private var selectedStampStyleId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isLiteMode = arguments?.getBoolean(IS_LITE_MODE_KEY) ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view = inflater.inflate(com.example.common_ui.R.layout.polyline_spans_control_fragment, container, false)
        spanCountBar = view.findViewById(com.example.common_ui.R.id.spansSeekBar)
        spanCountBar.max = SPAN_COUNT_MAX
        spanCountBar.setOnSeekBarChangeListener(this)
        spanCountTextView = view.findViewById(com.example.common_ui.R.id.spansTextView)
        gradientToggle = view.findViewById(com.example.common_ui.R.id.gradientToggle)
        gradientToggle.setOnCheckedChangeListener { _, isChecked ->
            polyline?.let {
                polylineGradientStates[it] = isChecked
            }
            updateSpans()
        }
        polylineStampStyleRadioGroup = view.findViewById(com.example.common_ui.R.id.polyline_stamp_style_radio_group)
        polylineStampStyleRadioGroup.visibility = View.INVISIBLE
        if (isLiteMode) {
            gradientToggle.visibility = View.INVISIBLE
            polylineStampStyleRadioGroup.visibility = View.INVISIBLE
        }
        polylineSpanCounts.clear()
        polylineGradientStates.clear()
        selectedStampStyleId = 0
        polylineStampStyleRadioGroup.setOnCheckedChangeListener(this)
        return view
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        spanCount = progress
        polyline?.let {
            polylineSpanCounts.put(it, spanCount)
        }
        spanCountTextView.text = spanCount.toString()
        updateSpans()
    }

    private fun generateSpans(count: Int): List<StyleSpan> {
        val polyline = polyline ?: return emptyList()
        val invertedPolylineColor: Int = polyline.color xor 0x00ffffff
        val newSpans = mutableListOf<StyleSpan>()
        for (i in 0 until count) {
            val color = if (i % 2 == 0) polyline.color else invertedPolylineColor
            val segmentCount = (polyline.points.size - 1).toDouble() / count
            val strokeStyleBuilder: StrokeStyle.Builder = if (gradientToggle.isChecked) StrokeStyle.gradientBuilder(polyline.color, invertedPolylineColor) else StrokeStyle.colorBuilder(color)
            if (selectedStampStyleId == com.example.common_ui.R.id.polyline_texture_style) {
                strokeStyleBuilder.stamp(
                    TextureStyle.newBuilder(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.ook))
                        .build())
            }
            newSpans.add(StyleSpan(strokeStyleBuilder.build(), segmentCount))
        }
        return newSpans
    }

    private fun updateSpans() {
        val polyline = polyline ?: return
        polyline.setSpans(generateSpans(spanCount))
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Don't do anything here.
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Don't do anything here.
    }

    override fun refresh() {
        val polyline = polyline
        if (polyline == null) {
            spanCountBar.isEnabled = false
            spanCountBar.progress = 0
            spanCountTextView.text = ""
            gradientToggle.isChecked = false
            gradientToggle.isEnabled = false
            polylineStampStyleRadioGroup.clearCheck()
            polylineStampStyleRadioGroup.isEnabled = false
            return
        }
        if (!polylineSpanCounts.containsKey(polyline)) {
            polylineSpanCounts[polyline] = 0
        }
        if (!polylineGradientStates.containsKey(polyline)) {
            polylineGradientStates[polyline] = false
        }
        spanCountBar.isEnabled = true
        spanCountBar.progress = polylineSpanCounts[polyline] ?: 0
        spanCountTextView.text = polylineSpanCounts[polyline].toString()
        if (!isLiteMode) {
            gradientToggle.isEnabled = true
            gradientToggle.isChecked = polylineGradientStates[polyline] ?: false
            polylineStampStyleRadioGroup.isEnabled = true
            polylineStampStyleRadioGroup.check(selectedStampStyleId)
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        selectedStampStyleId = checkedId
        updateSpans()
    }

    /**
     * Resets the span states of a polyline.
     *
     *
     * Because there's no getter for polyline spans, this is needed for the polyline demo
     * activity
     * to update span control UI components.
     */
    fun resetSpanState(polyline: Polyline?) {
        polylineSpanCounts.remove(polyline)
        polylineGradientStates.remove(polyline)
    }

    companion object {
        private const val IS_LITE_MODE_KEY = "isLiteMode"
        private const val SPAN_COUNT_MAX = 20
        fun newInstance(isLiteMode: Boolean): PolylineSpansControlFragment {
            val polylineSpansControlFragment = PolylineSpansControlFragment()
            val args = Bundle()
            args.putBoolean(IS_LITE_MODE_KEY, isLiteMode)
            polylineSpansControlFragment.arguments = args
            return polylineSpansControlFragment
        }
    }
}