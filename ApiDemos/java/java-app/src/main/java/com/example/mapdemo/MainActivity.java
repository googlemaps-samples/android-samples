/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mapdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The main activity of the API library demo gallery.
 *
 * <p>The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends SamplesBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /** A custom array adapter that shows a {@link FeatureView} containing details about the demo. */
    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /** @param demos An array containing the details of the demos to be displayed. */
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, com.example.common_ui.R.layout.feature, com.example.common_ui.R.id.title, demos);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            DemoDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            Resources resources = getContext().getResources();
            String title = resources.getString(demo.titleId);
            String description = resources.getString(demo.descriptionId);
            featureView.setContentDescription(title + ". " + description);

            return featureView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.main);

        ListAdapter adapter = new CustomArrayAdapter(this, DemoDetailsList.DEMOS);

        ListView demoListView = findViewById(com.example.common_ui.R.id.list);
        if (demoListView != null) {
            demoListView.setAdapter(adapter);
            demoListView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    DemoDetails demo = (DemoDetails) parent.getItemAtPosition(position);
                    startActivity(new Intent(view.getContext(), demo.activityClass));
                });
        }
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }
}
