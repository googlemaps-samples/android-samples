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
import com.google.android.libraries.maps.model.JointType

/**
 * Fragment with "joint" UI controls for Polylines, to be used in ViewPager.
 */
class PolylineJointControlFragment : PolylineControlFragment(), OnCheckedChangeListener {
    private val radioIdToJointType = mutableMapOf<Int, Int>()
    private lateinit var jointRadioGroup: RadioGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view = inflater.inflate(com.example.common_ui.R.layout.polyline_joint_control_fragment, container, false)
        jointRadioGroup = view.findViewById(com.example.common_ui.R.id.joint_radio)
        jointRadioGroup.setOnCheckedChangeListener(this)
        return view
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (polyline == null) {
            return
        }
        val jointType = radioIdToJointType[checkedId]
        if (jointType != null) {
            polyline?.jointType = jointType
        }
    }

    override fun refresh() {
        if (polyline == null) {
            jointRadioGroup.clearCheck()
            for (i in 0 until jointRadioGroup.childCount) {
                jointRadioGroup.getChildAt(i).isEnabled = false
            }
            return
        }
        for (i in 0 until jointRadioGroup.childCount) {
            jointRadioGroup.getChildAt(i).isEnabled = true
        }
        when (polyline?.jointType) {
            JointType.DEFAULT -> jointRadioGroup.check(com.example.common_ui.R.id.joint_radio_default)
            JointType.BEVEL -> jointRadioGroup.check(com.example.common_ui.R.id.joint_radio_bevel)
            JointType.ROUND -> jointRadioGroup.check(com.example.common_ui.R.id.joint_radio_round)
            else -> jointRadioGroup.clearCheck()
        }
    }

    init {
        radioIdToJointType[com.example.common_ui.R.id.joint_radio_default] = JointType.DEFAULT
        radioIdToJointType[com.example.common_ui.R.id.joint_radio_bevel] = JointType.BEVEL
        radioIdToJointType[com.example.common_ui.R.id.joint_radio_round] = JointType.ROUND
    }
}