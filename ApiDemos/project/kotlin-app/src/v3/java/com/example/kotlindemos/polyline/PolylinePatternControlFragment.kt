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
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.kotlindemos.R
import com.google.android.libraries.maps.model.Dash
import com.google.android.libraries.maps.model.Dot
import com.google.android.libraries.maps.model.Gap
import com.google.android.libraries.maps.model.PatternItem
import java.util.ArrayList
import java.util.Random

/** Fragment with "pattern" UI controls for Polylines, to be used in ViewPager.  */
class PolylinePatternControlFragment : PolylineControlFragment(), OnClickListener {
    private lateinit var patternSolidBtn: Button
    private lateinit var patternDottedBtn: Button
    private lateinit var patternDashedBtn: Button
    private lateinit var patternMixedBtn: Button
    private lateinit var patternTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view = inflater.inflate(com.example.common_ui.R.layout.polyline_pattern_control_fragment, container, false)
        patternTextView = view.findViewById(com.example.common_ui.R.id.patternTextView)
        patternSolidBtn = view.findViewById(com.example.common_ui.R.id.patternSolidBtn)
        patternDottedBtn = view.findViewById(com.example.common_ui.R.id.patternDottedBtn)
        patternDashedBtn = view.findViewById(com.example.common_ui.R.id.patternDashedBtn)
        patternMixedBtn = view.findViewById(com.example.common_ui.R.id.patternMixedBtn)
        patternSolidBtn.setOnClickListener(this)
        patternDottedBtn.setOnClickListener(this)
        patternDashedBtn.setOnClickListener(this)
        patternMixedBtn.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        if (polyline == null) {
            return
        }
        val id: Int = view.id
        val pattern: List<PatternItem>?
        when (id) {
            com.example.common_ui.R.id.patternSolidBtn -> {
                pattern = null
            }
            com.example.common_ui.R.id.patternDottedBtn -> {
                pattern = listOf(Dot(), Gap(RANDOM.nextFloat() * MAX_GAP_LENGTH))
            }
            com.example.common_ui.R.id.patternDashedBtn -> {
                pattern = listOf(
                    Dash(RANDOM.nextFloat() * MAX_DASH_LENGTH),
                    Gap(RANDOM.nextFloat() * MAX_GAP_LENGTH))
            }
            com.example.common_ui.R.id.patternMixedBtn -> {
                val size: Int = 2 * (1 + RANDOM.nextInt(MAX_PATTERN_SIZE / 2))
                pattern = ArrayList(size)
                for (i in 0 until size) {
                    if (i % 2 == 0) {
                        pattern.add(
                            if (RANDOM.nextBoolean()) Dot() else Dash(RANDOM.nextFloat() * MAX_DASH_LENGTH))
                    } else {
                        pattern.add(Gap(RANDOM.nextFloat() * MAX_GAP_LENGTH))
                    }
                }
            }
            else -> {
                throw IllegalStateException("Unknown button")
            }
        }
        polyline?.pattern = pattern
        patternTextView.text = toString(pattern)
    }

    override fun refresh() {
        val enabled = polyline != null
        patternSolidBtn.isEnabled = enabled
        patternDottedBtn.isEnabled = enabled
        patternDashedBtn.isEnabled = enabled
        patternMixedBtn.isEnabled = enabled
        patternTextView.text = if (polyline == null) "" else polyline?.pattern.toString()
    }

    companion object {
        private const val MAX_PATTERN_SIZE = 6
        private const val MAX_DASH_LENGTH = 200
        private const val MAX_GAP_LENGTH = 100
        private val RANDOM: Random = Random()
        private fun toString(pattern: List<PatternItem>?): String {
            if (pattern == null) {
                return "<SOLID>"
            }
            val sb = StringBuilder("")
            for (item in pattern) {
                when (item) {
                    is Dot -> {
                        sb.append("DOT")
                    }
                    is Dash -> {
                        sb.append(String.format("%.1fpx-DASH", (item as Dash).length))
                    }
                    is Gap -> {
                        sb.append(String.format("%.1fpx-GAP", (item as Gap).length))
                    }
                }
                sb.append(" ")
            }
            sb.append("<repeat>")
            return sb.toString()
        }
    }
}