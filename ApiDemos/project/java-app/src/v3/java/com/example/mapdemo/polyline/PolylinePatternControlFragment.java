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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.mapdemo.R;
import com.google.android.libraries.maps.model.Dash;
import com.google.android.libraries.maps.model.Dot;
import com.google.android.libraries.maps.model.Gap;
import com.google.android.libraries.maps.model.PatternItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Fragment with "pattern" UI controls for Polylines, to be used in ViewPager. */
public class PolylinePatternControlFragment extends PolylineControlFragment implements
    OnClickListener {

    private static final int MAX_PATTERN_SIZE = 6;
    private static final int MAX_DASH_LENGTH = 200;
    private static final int MAX_GAP_LENGTH = 100;
    private static final Random RANDOM = new Random();

    private Button patternSolidBtn;
    private Button patternDottedBtn;
    private Button patternDashedBtn;
    private Button patternMixedBtn;
    private TextView patternTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(com.example.common_ui.R.layout.polyline_pattern_control_fragment, container, false);
        patternTextView = view.findViewById(com.example.common_ui.R.id.patternTextView);

        patternSolidBtn = view.findViewById(com.example.common_ui.R.id.patternSolidBtn);
        patternDottedBtn = view.findViewById(com.example.common_ui.R.id.patternDottedBtn);
        patternDashedBtn = view.findViewById(com.example.common_ui.R.id.patternDashedBtn);
        patternMixedBtn = view.findViewById(com.example.common_ui.R.id.patternMixedBtn);

        patternSolidBtn.setOnClickListener(this);
        patternDottedBtn.setOnClickListener(this);
        patternDashedBtn.setOnClickListener(this);
        patternMixedBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (polyline == null) {
            return;
        }

        int id = view.getId();
        List<PatternItem> pattern;
        if (id == com.example.common_ui.R.id.patternSolidBtn) {
            pattern = null;
        } else if (id == com.example.common_ui.R.id.patternDottedBtn) {
            pattern = Arrays.asList(new Dot(), new Gap(RANDOM.nextFloat() * MAX_GAP_LENGTH));
        } else if (id == com.example.common_ui.R.id.patternDashedBtn) {
            pattern =
                Arrays.asList(
                    new Dash(RANDOM.nextFloat() * MAX_DASH_LENGTH),
                    new Gap(RANDOM.nextFloat() * MAX_GAP_LENGTH));
        } else if (id == com.example.common_ui.R.id.patternMixedBtn) {
            int size = 2 * (1 + RANDOM.nextInt(MAX_PATTERN_SIZE / 2));
            pattern = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                if ((i % 2) == 0) {
                    pattern.add(
                        RANDOM.nextBoolean() ? new Dot() : new Dash(RANDOM.nextFloat() * MAX_DASH_LENGTH));
                } else {
                    pattern.add(new Gap(RANDOM.nextFloat() * MAX_GAP_LENGTH));
                }
            }
        } else {
            throw new IllegalStateException("Unknown button");
        }

        polyline.setPattern(pattern);
        patternTextView.setText(toString(pattern));
    }

    @Override
    public void refresh() {
        boolean enabled = (polyline != null);
        patternSolidBtn.setEnabled(enabled);
        patternDottedBtn.setEnabled(enabled);
        patternDashedBtn.setEnabled(enabled);
        patternMixedBtn.setEnabled(enabled);
        patternTextView.setText((polyline == null) ? "" : toString(polyline.getPattern()));
    }

    private static String toString(List<PatternItem> pattern) {
        if (pattern == null) {
            return "<SOLID>";
        }

        StringBuilder sb = new StringBuilder("");
        for (PatternItem item : pattern) {
            if (item instanceof Dot) {
                sb.append("DOT");
            } else if (item instanceof Dash) {
                sb.append(String.format("%.1fpx-DASH", ((Dash) item).length));
            } else if (item instanceof Gap) {
                sb.append(String.format("%.1fpx-GAP", ((Gap) item).length));
            }
            sb.append(" ");
        }
        sb.append("<repeat>");
        return sb.toString();
    }
}
