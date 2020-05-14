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

import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.HashMap;
import java.util.Map;

public class PolylineControlFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 8;

    private final Map<Integer, PolylineControlFragment> positionsToFragments;
    private final boolean isLiteMode;

    public PolylineControlFragmentPagerAdapter(FragmentManager fragmentManager, boolean isLiteMode) {
        super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        positionsToFragments = new HashMap<>();
        this.isLiteMode = isLiteMode;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PolylineControlFragment fragment =
            (PolylineControlFragment) super.instantiateItem(container, position);
        positionsToFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        positionsToFragments.remove(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PolylineColorControlFragment();
            case 1:
                return new PolylineWidthControlFragment();
            case 2:
                return new PolylineCapControlFragment();
            case 3:
                return new PolylineJointControlFragment();
            case 4:
                return new PolylinePatternControlFragment();
            case 5:
                return new PolylinePointsControlFragment();
            case 6:
                return PolylineSpansControlFragment.newInstance(isLiteMode);
            case 7:
                return new PolylineOtherOptionsControlFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Ideally these strings should be localised, but let's not bother for a demo app.
        switch (position) {
            case 0:
                return "Color";
            case 1:
                return "Width";
            case 2:
                return "Cap";
            case 3:
                return "Joint";
            case 4:
                return "Pattern";
            case 5:
                return "Points";
            case 6:
                return "Spans";
            case 7:
                return "Other Options";
            default:
                return null;
        }
    }

    public PolylineControlFragment getFragmentAtPosition(int position) {
        return positionsToFragments.get(position);
    }
}
