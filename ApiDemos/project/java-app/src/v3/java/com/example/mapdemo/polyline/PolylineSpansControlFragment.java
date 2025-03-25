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
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.mapdemo.R;
import com.google.android.libraries.maps.model.BitmapDescriptorFactory;
import com.google.android.libraries.maps.model.Polyline;
import com.google.android.libraries.maps.model.StrokeStyle;
import com.google.android.libraries.maps.model.StyleSpan;
import com.google.android.libraries.maps.model.TextureStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment with span count UI controls for {@link com.google.android.libraries.maps.model.Polyline}, to
 * be used in ViewPager.
 *
 * <p>When span count is updated from the slider, the selected polyline will be updated with that
 * number of spans. Each span will either have polyline color or the inverted color, and span
 * lengths are equally divided by number of segments in the polyline.
 */
public class PolylineSpansControlFragment extends PolylineControlFragment
    implements SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {

    private static final String IS_LITE_MODE_KEY = "isLiteMode";
    private static final int SPAN_COUNT_MAX = 20;
    private SeekBar spanCountBar;
    private TextView spanCountTextView;
    private CompoundButton gradientToggle;
    private int spanCount = 0;
    private final Map<Polyline, Integer> polylineSpanCounts = new HashMap<>();
    private final Map<Polyline, Boolean> polylineGradientStates = new HashMap<>();
    private boolean isLiteMode = false;
    private int selectedStampStyleId;
    private RadioGroup polylineStampStyleRadioGroup;

    static PolylineSpansControlFragment newInstance(boolean isLiteMode) {
        PolylineSpansControlFragment polylineSpansControlFragment = new PolylineSpansControlFragment();

        Bundle args = new Bundle();
        args.putBoolean(IS_LITE_MODE_KEY, isLiteMode);
        polylineSpansControlFragment.setArguments(args);

        return polylineSpansControlFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLiteMode = getArguments().getBoolean(IS_LITE_MODE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(com.example.common_ui.R.layout.polyline_spans_control_fragment, container, false);

        spanCountBar = view.findViewById(com.example.common_ui.R.id.spansSeekBar);
        spanCountBar.setMax(SPAN_COUNT_MAX);
        spanCountBar.setOnSeekBarChangeListener(this);

        spanCountTextView = view.findViewById(com.example.common_ui.R.id.spansTextView);

        gradientToggle = view.findViewById(com.example.common_ui.R.id.gradientToggle);
        gradientToggle.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                polylineGradientStates.put(polyline, isChecked);
                updateSpans();
            });

        polylineStampStyleRadioGroup = view.findViewById(com.example.common_ui.R.id.polyline_stamp_style_radio_group);
        polylineStampStyleRadioGroup.setVisibility(View.INVISIBLE);

        if (isLiteMode) {
            gradientToggle.setVisibility(View.INVISIBLE);
            polylineStampStyleRadioGroup.setVisibility(View.INVISIBLE);
        }

        polylineSpanCounts.clear();
        polylineGradientStates.clear();
        selectedStampStyleId = 0;
        polylineStampStyleRadioGroup.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        spanCount = progress;
        polylineSpanCounts.put(polyline, spanCount);
        spanCountTextView.setText(String.valueOf(spanCount));
        updateSpans();
    }

    private List<StyleSpan> generateSpans(int count) {
        int invertedPolylineColor = polyline.getColor() ^ 0x00ffffff;
        List<StyleSpan> newSpans = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int color = i % 2 == 0 ? polyline.getColor() : invertedPolylineColor;
            double segmentCount = (double) (polyline.getPoints().size() - 1) / count;
            StrokeStyle.Builder strokeStyleBuilder =
                gradientToggle.isChecked()
                    ? StrokeStyle.gradientBuilder(polyline.getColor(), invertedPolylineColor)
                    : StrokeStyle.colorBuilder(color);

            if (selectedStampStyleId == com.example.common_ui.R.id.polyline_texture_style) {
                strokeStyleBuilder.stamp(
                    TextureStyle.newBuilder(BitmapDescriptorFactory.fromResource(com.example.common_ui.R.drawable.ook))
                        .build());
            }

            newSpans.add(new StyleSpan(strokeStyleBuilder.build(), segmentCount));
        }
        return newSpans;
    }

    private void updateSpans() {
        if (polyline == null) {
            return;
        }

        polyline.setSpans(generateSpans(spanCount));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Don't do anything here.
    }

    @Override
    public void refresh() {
        if (polyline == null) {
            spanCountBar.setEnabled(false);
            spanCountBar.setProgress(0);
            spanCountTextView.setText("");
            gradientToggle.setChecked(false);
            gradientToggle.setEnabled(false);
            polylineStampStyleRadioGroup.clearCheck();
            polylineStampStyleRadioGroup.setEnabled(false);
            return;
        }

        if (!polylineSpanCounts.containsKey(polyline)) {
            polylineSpanCounts.put(polyline, 0);
        }
        if (!polylineGradientStates.containsKey(polyline)) {
            polylineGradientStates.put(polyline, false);
        }

        spanCountBar.setEnabled(true);
        spanCountBar.setProgress(polylineSpanCounts.get(polyline));
        spanCountTextView.setText(String.valueOf(polylineSpanCounts.get(polyline)));

        if (!isLiteMode) {
            gradientToggle.setEnabled(true);
            gradientToggle.setChecked(polylineGradientStates.get(polyline));

            polylineStampStyleRadioGroup.setEnabled(true);
            polylineStampStyleRadioGroup.check(selectedStampStyleId);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        selectedStampStyleId = checkedId;
        updateSpans();
    }

    /**
     * Resets the span states of a polyline.
     *
     * <p>Because there's no getter for polyline spans, this is needed for the polyline demo
     * activity
     * to update span control UI components.
     */
    void resetSpanState(Polyline polyline) {
        polylineSpanCounts.remove(polyline);
        polylineGradientStates.remove(polyline);
    }
}
