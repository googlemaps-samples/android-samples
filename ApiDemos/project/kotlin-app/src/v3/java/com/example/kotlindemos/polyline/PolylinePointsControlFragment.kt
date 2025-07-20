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
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.example.kotlindemos.R
import com.example.kotlindemos.anim.AnimationManager
import com.example.kotlindemos.model.MoveDirection
import com.google.android.libraries.maps.model.LatLng

/** Fragment with "points" UI controls for Polylines, to be used in ViewPager.  */
class PolylinePointsControlFragment : PolylineControlFragment(), View.OnTouchListener, View.OnFocusChangeListener, TextView.OnEditorActionListener {
    private var moveDirection: MoveDirection? = null
    private var stepSizeDeg = 0.0
    private var originalPoints: List<LatLng>? = null
    private var latDistance = 0.0
    private var lngDistance = 0.0
    private val animationManager: AnimationManager = AnimationManager(
        object : Runnable {
            override fun run() {
                if (polyline == null || moveDirection == null) {
                    return
                }
                // When the polyline moves offscreen, its coordinates will be clamped and caused the
                // shape to change. Using the original points for computing new points to make sure
                // the shape can be retained after the polyline moves offscreen.
                latDistance += moveDirection?.getLatDistance(stepSizeDeg) ?: 0.0
                lngDistance += moveDirection?.getLngDistance(stepSizeDeg) ?: 0.0
                polyline?.points = MoveDirection.movePointsInList(originalPoints ?: mutableListOf(), latDistance, lngDistance)
            }
        })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View {
        val view: View = inflater.inflate(com.example.common_ui.R.layout.polyline_points_control_fragment, container, false)
        (view.findViewById(com.example.common_ui.R.id.move_up) as View).setOnTouchListener(this)
        (view.findViewById(com.example.common_ui.R.id.move_down) as View).setOnTouchListener(this)
        (view.findViewById(com.example.common_ui.R.id.move_left) as View).setOnTouchListener(this)
        (view.findViewById(com.example.common_ui.R.id.move_right) as View).setOnTouchListener(this)
        val fpsEditText: EditText = view.findViewById(com.example.common_ui.R.id.fps_edittext) as EditText
        fpsEditText.setOnEditorActionListener(this)
        fpsEditText.onFocusChangeListener = this
        setFrameRate(fpsEditText)
        val stepEditText: EditText = view.findViewById(com.example.common_ui.R.id.step_edittext) as EditText
        stepEditText.setOnEditorActionListener(this)
        stepEditText.onFocusChangeListener = this
        setStepSize(stepEditText)
        moveDirection = null
        return view
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                setMoveDirection(view.id)
                animationManager.startAnimation()
                true
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                moveDirection = null
                animationManager.stopAnimation()
                true
            }
            else -> false
        }
    }

    private fun setMoveDirection(buttonId: Int) {
        moveDirection = when (buttonId) {
            com.example.common_ui.R.id.move_up -> {
                MoveDirection.UP
            }
            com.example.common_ui.R.id.move_down -> {
                MoveDirection.DOWN
            }
            com.example.common_ui.R.id.move_left -> {
                MoveDirection.LEFT
            }
            com.example.common_ui.R.id.move_right -> {
                MoveDirection.RIGHT
            }
            else -> {
                null
            }
        }
    }

    override fun onEditorAction(textView: TextView, actionId: Int, event: KeyEvent?): Boolean {
        // Only handle "Done" action triggered by the user tapping "Enter" on the keyboard.
        if (actionId != EditorInfo.IME_ACTION_DONE) {
            return false
        }
        val textViewId: Int = textView.getId()
        if (textViewId == com.example.common_ui.R.id.fps_edittext) {
            setFrameRate(textView)
        } else if (textViewId == com.example.common_ui.R.id.step_edittext) {
            setStepSize(textView)
        }
        return false
    }

    override fun onFocusChange(view: View, hasFocus: Boolean) {
        if (hasFocus) {
            return
        }
        val viewId: Int = view.getId()
        if (viewId == com.example.common_ui.R.id.fps_edittext) {
            setFrameRate(view as TextView)
        } else if (viewId == com.example.common_ui.R.id.step_edittext) {
            setStepSize(view as TextView)
        }
    }

    private fun setFrameRate(textView: TextView) {
        val newValue: String = textView.text.toString()
        try {
            animationManager.frameRateFps = newValue.toDouble()
        } catch (e: NumberFormatException) {
            textView.text = animationManager.frameRateFps.toString()
        }
    }

    private fun setStepSize(textView: TextView) {
        val newValue: String = textView.text.toString()
        try {
            stepSizeDeg = newValue.toDouble()
        } catch (e: NumberFormatException) {
            textView.text = stepSizeDeg.toString()
        }
    }

    override fun refresh() {
        latDistance = 0.0
        lngDistance = 0.0
        originalPoints = polyline?.points
    }
}