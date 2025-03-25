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

package com.example.mapdemo.polyline;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.example.mapdemo.R;
import com.example.mapdemo.anim.AnimationManager;
import com.example.mapdemo.model.MoveDirection;
import com.google.android.libraries.maps.model.LatLng;
import java.util.List;

/** Fragment with "points" UI controls for Polylines, to be used in ViewPager. */
public class PolylinePointsControlFragment extends PolylineControlFragment implements View.OnTouchListener, View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private MoveDirection moveDirection;
    private double stepSizeDeg;
    private List<LatLng> originalPoints;
    private double latDistance = 0;
    private double lngDistance = 0;

    private final AnimationManager animationManager =
        new AnimationManager(
            new Runnable() {
                @Override
                public void run() {
                    if ((polyline == null) || (moveDirection == null)) {
                        return;
                    }
                    // When the polyline moves offscreen, its coordinates will be clamped and caused the
                    // shape to change. Using the original points for computing new points to make sure
                    // the shape can be retained after the polyline moves offscreen.
                    latDistance += moveDirection.getLatDistance(stepSizeDeg);
                    lngDistance += moveDirection.getLngDistance(stepSizeDeg);
                    polyline.setPoints(
                        MoveDirection.movePointsInList(originalPoints, latDistance, lngDistance));
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(com.example.common_ui.R.layout.polyline_points_control_fragment, container, false);

        view.findViewById(com.example.common_ui.R.id.move_up).setOnTouchListener(this);
        view.findViewById(com.example.common_ui.R.id.move_down).setOnTouchListener(this);
        view.findViewById(com.example.common_ui.R.id.move_left).setOnTouchListener(this);
        view.findViewById(com.example.common_ui.R.id.move_right).setOnTouchListener(this);

        EditText fpsEditText = (EditText) view.findViewById(com.example.common_ui.R.id.fps_edittext);
        fpsEditText.setOnEditorActionListener(this);
        fpsEditText.setOnFocusChangeListener(this);
        setFrameRate(fpsEditText);

        EditText stepEditText = (EditText) view.findViewById(com.example.common_ui.R.id.step_edittext);
        stepEditText.setOnEditorActionListener(this);
        stepEditText.setOnFocusChangeListener(this);
        setStepSize(stepEditText);

        moveDirection = null;
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setMoveDirection(view.getId());
                animationManager.startAnimation();
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                moveDirection = null;
                animationManager.stopAnimation();
                return true;
            default:
                return false;
        }
    }

    private void setMoveDirection(int buttonId) {
        if (buttonId == com.example.common_ui.R.id.move_up) {
            moveDirection = MoveDirection.UP;
        } else if (buttonId == com.example.common_ui.R.id.move_down) {
            moveDirection = MoveDirection.DOWN;
        } else if (buttonId == com.example.common_ui.R.id.move_left) {
            moveDirection = MoveDirection.LEFT;
        } else if (buttonId == com.example.common_ui.R.id.move_right) {
            moveDirection = MoveDirection.RIGHT;
        } else {
            moveDirection = null;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        // Only handle "Done" action triggered by the user tapping "Enter" on the keyboard.
        if (actionId != EditorInfo.IME_ACTION_DONE) {
            return false;
        }

        int textViewId = textView.getId();
        if (textViewId == com.example.common_ui.R.id.fps_edittext) {
            setFrameRate(textView);
        } else if (textViewId == com.example.common_ui.R.id.step_edittext) {
            setStepSize(textView);
        }
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            return;
        }

        int viewId = view.getId();
        if (viewId == com.example.common_ui.R.id.fps_edittext) {
            setFrameRate((TextView) view);
        } else if (viewId == com.example.common_ui.R.id.step_edittext) {
            setStepSize((TextView) view);
        }
    }

    private void setFrameRate(TextView textView) {
        String newValue = String.valueOf(textView.getText());
        try {
            animationManager.setFrameRateFps(Double.parseDouble(newValue));
        } catch (NumberFormatException e) {
            textView.setText(Double.toString(animationManager.getFrameRateFps()));
        }
    }

    private void setStepSize(TextView textView) {
        String newValue = String.valueOf(textView.getText());
        try {
            stepSizeDeg = Double.parseDouble(newValue);
        } catch (NumberFormatException e) {
            textView.setText(Double.toString(stepSizeDeg));
        }
    }

    @Override
    public void refresh() {
        latDistance = 0;
        lngDistance = 0;
        originalPoints = polyline.getPoints();
    }
}
