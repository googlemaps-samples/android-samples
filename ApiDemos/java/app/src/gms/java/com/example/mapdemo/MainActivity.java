/*
 * Copyright (C) 2012 The Android Open Source Project
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The main activity of the API library demo gallery.
 *
 * <p>The main layout lists the demonstrated features, with buttons to launch them.
 */
public final class MainActivity extends AppCompatActivity implements OnMapsSdkInitializedCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    /** A custom array adapter that shows a {@link FeatureView} containing details about the demo. */
    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /** @param demos An array containing the details of the demos to be displayed. */
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
        setContentView(R.layout.main);

        ListAdapter adapter = new CustomArrayAdapter(this, DemoDetailsList.DEMOS);

        ListView demoListView = (ListView) findViewById(R.id.list);
        if (demoListView != null) {
            demoListView.setAdapter(adapter);
            demoListView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    DemoDetails demo = (DemoDetails) parent.getItemAtPosition(position);
                    startActivity(new Intent(view.getContext(), demo.activityClass));
                });
        }

        Spinner spinner = (Spinner) findViewById(R.id.map_renderer_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter =
            ArrayAdapter.createFromResource(
                this, R.array.map_renderer_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(
            new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String preferredRendererName = ((String) spinner.getSelectedItem());
                    Renderer preferredRenderer;

                    if (preferredRendererName.equals(getString(R.string.latest))) {
                        preferredRenderer = Renderer.LATEST;
                    } else if (preferredRendererName.equals(getString(R.string.legacy))) {
                        preferredRenderer = Renderer.LEGACY;
                    } else if (preferredRendererName.equals(getString(R.string.default_renderer))) {
                        preferredRenderer = null;
                    } else {
                        Log.i(TAG, "Error setting renderer with name " + preferredRendererName);
                        return;
                    }
                    MapsInitializer.initialize(getApplicationContext(), preferredRenderer, MainActivity.this);

                    // Disable spinner since renderer cannot be changed once map is intitialized.
                    spinner.setEnabled(false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        Toast.makeText(
            getApplicationContext(),
            "All demo activities will use " + renderer.toString() + " renderer.",
            Toast.LENGTH_LONG)
            .show();
    }
}
