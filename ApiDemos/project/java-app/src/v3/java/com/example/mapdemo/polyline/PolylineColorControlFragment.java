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

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.example.mapdemo.R;

/** Fragment with "color" UI controls for Polylines, to be used in ViewPager. */
public class PolylineColorControlFragment extends PolylineControlFragment implements OnSeekBarChangeListener {
    private static final int HUE_MAX = 359;
    private static final int ALPHA_MAX = 255;

    private SeekBar alphaBar;
    private SeekBar hueBar;
    private TextView argbTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(com.example.common_ui.R.layout.polyline_color_control_fragment, container, false);

        alphaBar = view.findViewById(com.example.common_ui.R.id.alphaSeekBar);
        alphaBar.setMax(ALPHA_MAX);
        alphaBar.setOnSeekBarChangeListener(this);

        hueBar = view.findViewById(com.example.common_ui.R.id.hueSeekBar);
        hueBar.setMax(HUE_MAX);
        hueBar.setOnSeekBarChangeListener(this);

        argbTextView = (TextView) view.findViewById(com.example.common_ui.R.id.argbTextView);
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
        if (polyline == null || !fromUser) {
            return;
        }

        if (seekBar == hueBar) {
            polyline.setColor(
                Color.HSVToColor(Color.alpha(polyline.getColor()), new float[] {progress, 1, 1}));
        } else if (seekBar == alphaBar) {
            float[] prevHSV = new float[3];
            Color.colorToHSV(polyline.getColor(), prevHSV);
            polyline.setColor(Color.HSVToColor(progress, prevHSV));
        }

        argbTextView.setText(String.format("0x%08X", polyline.getColor()));
    }

    @Override
    public void refresh() {
        if (polyline == null) {
            alphaBar.setEnabled(false);
            alphaBar.setProgress(0);
            hueBar.setEnabled(false);
            hueBar.setProgress(0);
            argbTextView.setText("");
            return;
        }

        int color = polyline.getColor();
        alphaBar.setEnabled(true);
        alphaBar.setProgress(Color.alpha(color));

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hueBar.setEnabled(true);
        hueBar.setProgress((int) hsv[0]);

        argbTextView.setText(String.format("0x%08X", color));
    }
}
