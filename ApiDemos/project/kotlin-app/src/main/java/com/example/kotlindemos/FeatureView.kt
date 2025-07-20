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

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView

/**
 * A widget that describes an activity that demonstrates a feature.
 */
class FeatureView(context: Context) : FrameLayout(context) {
    /**
     * Constructs a feature view by inflating layout/feature.xml.
     */
    init {
        val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(com.example.common_ui.R.layout.feature, this)
    }

    /**
     * Set the resource id of the title of the demo.
     *
     * @property titleId the resource id of the title of the demo
     */
    fun setTitleId(titleId: Int) {
        findViewById<TextView>(com.example.common_ui.R.id.title).setText(titleId)
    }

    /**
     * Set the resource id of the description of the demo.
     *
     * @property descriptionId the resource id of the description of the demo
     */
    fun setDescriptionId(descriptionId: Int) {
        (findViewById<TextView>(com.example.common_ui.R.id.description)).setText(descriptionId)
    }
}