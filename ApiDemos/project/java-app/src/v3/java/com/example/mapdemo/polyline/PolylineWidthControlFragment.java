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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.mapdemo.R;

/**
 * Fragment with "width" UI controls for Polylines, to be used in ViewPager.
 */
public class PolylineWidthControlFragment extends PolylineControlFragment implements
    SeekBar.OnSeekBarChangeListener {

    private static final int WIDTH_MAX = 50;
    private SeekBar widthBar;
    private TextView widthTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(com.example.common_ui.R.layout.polyline_width_control_fragment, container, false);
        widthBar = view.findViewById(com.example.common_ui.R.id.widthSeekBar);
        widthBar.setMax(WIDTH_MAX);
        widthBar.setOnSeekBarChangeListener(this);
        widthTextView = view.findViewById(com.example.common_ui.R.id.widthTextView);
        return view;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (polyline == null) {
            return;
        }

        polyline.setWidth(progress);
        widthTextView.setText(polyline.getWidth() + "px");
    }

    @Override
    public void refresh() {
        if (polyline == null) {
            widthBar.setEnabled(false);
            widthBar.setProgress(0);
            widthTextView.setText("");
            return;
        }

        widthBar.setEnabled(true);
        float width = polyline.getWidth();
        widthBar.setProgress((int) width);
        widthTextView.setText(width + "px");
    }
}
