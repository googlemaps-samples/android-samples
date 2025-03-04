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


package com.example.mapdemo;

import androidx.appcompat.app.AppCompatActivity;

/**
 * A simple POJO that holds the details about the demo that are used by the List Adapter.
 */
public class DemoDetails {

    /**
     * The resource id of the title of the demo.
     */
    public final int titleId;

    /**
     * The resources id of the description of the demo.
     */
    public final int descriptionId;

    /**
     * The demo activity's class.
     */
    public final Class<? extends SamplesBaseActivity> activityClass;

    public DemoDetails(
            int titleId, int descriptionId, Class<? extends SamplesBaseActivity> activityClass) {
        this.titleId = titleId;
        this.descriptionId = descriptionId;
        this.activityClass = activityClass;
    }
}
