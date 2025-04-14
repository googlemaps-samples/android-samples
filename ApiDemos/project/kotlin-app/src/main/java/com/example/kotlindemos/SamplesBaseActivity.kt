// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.kotlindemos

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


open class SamplesBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    companion object {
        /**
         * Applies insets to the container view to properly handle window insets.
         *
         * @param container the container view to apply insets to
         */
        fun applyInsets(container: View) {
            ViewCompat.setOnApplyWindowInsetsListener(
                container,
                OnApplyWindowInsetsListener { view: View?, insets: WindowInsetsCompat? ->
                    val innerPadding =
                        insets!!.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                    view!!.setPadding(
                        innerPadding.left,
                        innerPadding.top,
                        innerPadding.right,
                        innerPadding.bottom
                    )
                    insets
                }
            )
        }
    }
}