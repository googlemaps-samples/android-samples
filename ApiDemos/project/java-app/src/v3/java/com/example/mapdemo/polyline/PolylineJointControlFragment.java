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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.example.mapdemo.R;
import com.google.android.libraries.maps.model.JointType;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragment with "joint" UI controls for Polylines, to be used in ViewPager.
 */
public class PolylineJointControlFragment extends PolylineControlFragment implements
    OnCheckedChangeListener {

    private final Map<Integer, Integer> radioIdToJointType = new HashMap<>();
    private RadioGroup jointRadioGroup;

    public PolylineJointControlFragment() {
        radioIdToJointType.put(com.example.common_ui.R.id.joint_radio_default, JointType.DEFAULT);
        radioIdToJointType.put(com.example.common_ui.R.id.joint_radio_bevel, JointType.BEVEL);
        radioIdToJointType.put(com.example.common_ui.R.id.joint_radio_round, JointType.ROUND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(com.example.common_ui.R.layout.polyline_joint_control_fragment, container, false);
        jointRadioGroup = view.findViewById(com.example.common_ui.R.id.joint_radio);
        jointRadioGroup.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (polyline == null) {
            return;
        }

        Integer jointType = radioIdToJointType.get(checkedId);
        if (jointType != null) {
            polyline.setJointType(jointType);
        }
    }

    @Override
    public void refresh() {
        if (polyline == null) {
            jointRadioGroup.clearCheck();
            for (int i = 0; i < jointRadioGroup.getChildCount(); i++) {
                jointRadioGroup.getChildAt(i).setEnabled(false);
            }
            return;
        }

        for (int i = 0; i < jointRadioGroup.getChildCount(); i++) {
            jointRadioGroup.getChildAt(i).setEnabled(true);
        }

        switch (polyline.getJointType()) {
            case JointType.DEFAULT:
                jointRadioGroup.check(com.example.common_ui.R.id.joint_radio_default);
                break;
            case JointType.BEVEL:
                jointRadioGroup.check(com.example.common_ui.R.id.joint_radio_bevel);
                break;
            case JointType.ROUND:
                jointRadioGroup.check(com.example.common_ui.R.id.joint_radio_round);
                break;
            default:
                jointRadioGroup.clearCheck();
        }
    }
}
