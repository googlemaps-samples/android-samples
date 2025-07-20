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
import android.widget.RadioGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.example.kotlindemos.R
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.ButtCap
import com.google.android.libraries.maps.model.Cap
import com.google.android.libraries.maps.model.CustomCap
import com.google.android.libraries.maps.model.RoundCap
import com.google.android.libraries.maps.model.SquareCap

/**
 * Fragment with "cap" UI controls for Polylines, to be used in ViewPager.
 */
class PolylineCapControlFragment : PolylineControlFragment(), OnCheckedChangeListener {
    private val radioIdToStartCap = mutableMapOf<Int, Cap>()
    private val radioIdToEndCap = mutableMapOf<Int, Cap>()
    private lateinit var startCapRadioGroup: RadioGroup
    private lateinit var endCapRadioGroup: RadioGroup

    init {
        radioIdToStartCap[com.example.common_ui.R.id.start_cap_radio_butt] = ButtCap()
        radioIdToStartCap[com.example.common_ui.R.id.start_cap_radio_square] = SquareCap()
        radioIdToStartCap[com.example.common_ui.R.id.start_cap_radio_round] = RoundCap()
        radioIdToStartCap[com.example.common_ui.R.id.start_cap_radio_custom_chevron] = CustomCap(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.chevron), CHEVRON_REF_WIDTH)
        radioIdToStartCap[com.example.common_ui.R.id.start_cap_radio_custom_ook] = CustomCap(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.ook), OOK_REF_WIDTH)
        radioIdToEndCap[com.example.common_ui.R.id.end_cap_radio_butt] = ButtCap()
        radioIdToEndCap[com.example.common_ui.R.id.end_cap_radio_square] = SquareCap()
        radioIdToEndCap[com.example.common_ui.R.id.end_cap_radio_round] = RoundCap()
        radioIdToEndCap[com.example.common_ui.R.id.end_cap_radio_custom_chevron] = CustomCap(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.chevron),
            CHEVRON_REF_WIDTH)
        radioIdToEndCap[com.example.common_ui.R.id.end_cap_radio_custom_ook] = CustomCap(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.ook), OOK_REF_WIDTH)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view: View = inflater.inflate(com.example.common_ui.R.layout.polyline_cap_control_fragment, container, false)
        startCapRadioGroup = view.findViewById(com.example.common_ui.R.id.start_cap_radio)
        endCapRadioGroup = view.findViewById(com.example.common_ui.R.id.end_cap_radio)
        startCapRadioGroup.setOnCheckedChangeListener(this)
        endCapRadioGroup.setOnCheckedChangeListener(this)
        return view
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        if (polyline == null) {
            return
        }
        val groupId = group.getId()
        if (groupId == com.example.common_ui.R.id.start_cap_radio) {
            val startCap = radioIdToStartCap[checkedId]
            startCap?.let {
                polyline?.setStartCap(it)
            }
        } else if (groupId == com.example.common_ui.R.id.end_cap_radio) {
            val endCap = radioIdToEndCap[checkedId]
            endCap?.let {
                polyline?.setEndCap(it)
            }
        }
    }

    override fun refresh() {
        if (polyline == null) {
            startCapRadioGroup.clearCheck()
            for (i in 0 until startCapRadioGroup.getChildCount()) {
                startCapRadioGroup.getChildAt(i).setEnabled(false)
            }
            endCapRadioGroup.clearCheck()
            for (i in 0 until endCapRadioGroup.getChildCount()) {
                endCapRadioGroup.getChildAt(i).setEnabled(false)
            }
            return
        }
        for (i in 0 until startCapRadioGroup.getChildCount()) {
            startCapRadioGroup.getChildAt(i).setEnabled(true)
        }
        when (val startCap = polyline?.startCap) {
            is ButtCap -> {
                startCapRadioGroup.check(com.example.common_ui.R.id.start_cap_radio_butt)
            }
            is SquareCap -> {
                startCapRadioGroup.check(com.example.common_ui.R.id.start_cap_radio_square)
            }
            is RoundCap -> {
                startCapRadioGroup.check(com.example.common_ui.R.id.start_cap_radio_round)
            }
            is CustomCap -> {
                startCapRadioGroup.check(
                    if (isOok(startCap)) com.example.common_ui.R.id.start_cap_radio_custom_ook else com.example.common_ui.R.id.start_cap_radio_custom_chevron)
            }
            else -> {
                startCapRadioGroup.clearCheck()
            }
        }
        for (i in 0 until endCapRadioGroup.getChildCount()) {
            endCapRadioGroup.getChildAt(i).isEnabled = true
        }
        when (val endCap = polyline?.endCap) {
            is ButtCap -> {
                endCapRadioGroup.check(com.example.common_ui.R.id.end_cap_radio_butt)
            }
            is SquareCap -> {
                endCapRadioGroup.check(com.example.common_ui.R.id.end_cap_radio_square)
            }
            is RoundCap -> {
                endCapRadioGroup.check(com.example.common_ui.R.id.end_cap_radio_round)
            }
            is CustomCap -> {
                endCapRadioGroup.check(
                    if (isOok(endCap)) com.example.common_ui.R.id.end_cap_radio_custom_ook else com.example.common_ui.R.id.end_cap_radio_custom_chevron)
            }
            else -> {
                endCapRadioGroup.clearCheck()
            }
        }
    }

    companion object {
        // We require Ook's refWidth > chevron's refWidth so isOok(CustomCap) can distinguish
        // between the two CustomCaps by simple comparison of CustomCap.getBitmapRefWidth() against
        // CHEVRON_VERSUS_OOK_REF_WIDTH_THRESHOLD, for the purpose of refreshing "cap" UI radio
        // buttons. See isOok() for details on why this is necessary.
        private const val CHEVRON_REF_WIDTH = 15.0f
        private const val OOK_REF_WIDTH = 32.0f
        private const val CHEVRON_VERSUS_OOK_REF_WIDTH_THRESHOLD = 0.5f * (CHEVRON_REF_WIDTH + OOK_REF_WIDTH)
        private fun isOok(customCap: CustomCap): Boolean {
            return customCap.refWidth >= CHEVRON_VERSUS_OOK_REF_WIDTH_THRESHOLD
        }
    }
}