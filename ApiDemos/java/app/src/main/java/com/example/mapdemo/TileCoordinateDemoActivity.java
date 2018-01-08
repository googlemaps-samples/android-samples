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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

/**
 * This demonstrates tile overlay coordinates.
 */
public class TileCoordinateDemoActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tile_coordinate_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        TileProvider coordTileProvider = new CoordTileProvider(this.getApplicationContext());
        map.addTileOverlay(new TileOverlayOptions().tileProvider(coordTileProvider));
    }

    private static class CoordTileProvider implements TileProvider {

        private static final int TILE_SIZE_DP = 256;

        private final float mScaleFactor;

        private final Bitmap mBorderTile;

        public CoordTileProvider(Context context) {
            /* Scale factor based on density, with a 0.6 multiplier to increase tile generation
             * speed */
            mScaleFactor = context.getResources().getDisplayMetrics().density * 0.6f;
            Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Paint.Style.STROKE);
            mBorderTile = Bitmap.createBitmap((int) (TILE_SIZE_DP * mScaleFactor),
                    (int) (TILE_SIZE_DP * mScaleFactor), android.graphics.Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBorderTile);
            canvas.drawRect(0, 0, TILE_SIZE_DP * mScaleFactor, TILE_SIZE_DP * mScaleFactor,
                    borderPaint);
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            Bitmap coordTile = drawTileCoords(x, y, zoom);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            coordTile.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] bitmapData = stream.toByteArray();
            return new Tile((int) (TILE_SIZE_DP * mScaleFactor),
                    (int) (TILE_SIZE_DP * mScaleFactor), bitmapData);
        }

        private Bitmap drawTileCoords(int x, int y, int zoom) {
            // Synchronize copying the bitmap to avoid a race condition in some devices.
            Bitmap copy = null;
            synchronized (mBorderTile) {
                copy = mBorderTile.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
            }
            Canvas canvas = new Canvas(copy);
            String tileCoords = "(" + x + ", " + y + ")";
            String zoomLevel = "zoom = " + zoom;
            /* Paint is not thread safe. */
            Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(18 * mScaleFactor);
            canvas.drawText(tileCoords, TILE_SIZE_DP * mScaleFactor / 2,
                    TILE_SIZE_DP * mScaleFactor / 2, mTextPaint);
            canvas.drawText(zoomLevel, TILE_SIZE_DP * mScaleFactor / 2,
                    TILE_SIZE_DP * mScaleFactor * 2 / 3, mTextPaint);
            return copy;
        }
    }
}
