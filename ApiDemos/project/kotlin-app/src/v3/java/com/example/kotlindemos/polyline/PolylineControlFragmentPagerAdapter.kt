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

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PolylineControlFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val isLiteMode: Boolean
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val positionsToFragments = mutableMapOf<Int, PolylineControlFragment>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment: PolylineControlFragment = super.instantiateItem(container, position) as PolylineControlFragment
        positionsToFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        positionsToFragments.remove(position)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PolylineColorControlFragment()
            1 -> PolylineWidthControlFragment()
            2 -> PolylineCapControlFragment()
            3 -> PolylineJointControlFragment()
            4 -> PolylinePatternControlFragment()
            5 -> PolylinePointsControlFragment()
            6 -> PolylineSpansControlFragment.newInstance(isLiteMode)
            7 -> PolylineOtherOptionsControlFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int = 8

    override fun getPageTitle(position: Int): CharSequence? {
        // Ideally these strings should be localised, but let's not bother for a demo app.
        return when (position) {
            0 -> "Color"
            1 -> "Width"
            2 -> "Cap"
            3 -> "Joint"
            4 -> "Pattern"
            5 -> "Points"
            6 -> "Spans"
            7 -> "Other Options"
            else -> null
        }
    }

    fun getFragmentAtPosition(position: Int): PolylineControlFragment? {
        return positionsToFragments[position]
    }
}