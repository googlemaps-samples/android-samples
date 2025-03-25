// Copyright 2020 Google LLC
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.common_ui.R
import com.google.android.gms.maps.SupportMapFragment

/**
 * This shows how to add a map to a ViewPager. Note the use of
 * [ViewGroup.requestTransparentRegion] to reduce jankiness.
 */
class MapInPagerDemoActivity : SamplesBaseActivity() {

  /** Called when the activity is first created.  */
  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.map_in_pager_demo)
    val adapter = MyAdapter(supportFragmentManager)
    val pager = findViewById<ViewPager>(R.id.pager)
    pager.adapter = adapter

    // This is required to avoid a black flash when the map is loaded.  The flash is due
    // to the use of a SurfaceView as the underlying view of the map.
    pager.requestTransparentRegion(pager)
    applyInsets(findViewById<View?>(R.id.map_container))
  }

  /** A simple fragment that displays a TextView.  */
  class TextFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              bundle: Bundle?): View? {
      return inflater.inflate(R.layout.text_fragment, container, false)
    }
  }

  /** A simple FragmentPagerAdapter that returns two TextFragment and a SupportMapFragment.  */
  class MyAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
      return 3
    }

    override fun getItem(position: Int): Fragment {
      return when (position) {
        0, 1 -> TextFragment()
        2 -> SupportMapFragment.newInstance()
        else -> Fragment()
      }
    }
  }
}