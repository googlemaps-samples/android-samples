// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.firemarkers.data

import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * A data class to represent a single point in a shape definition.
 * @param latOffset The latitude offset from the shape's center.
 * @param lonOffset The longitude offset from the shape's center.
 * @param color The hue for the marker's color.
 */
data class ShapePoint(val latOffset: Double, val lonOffset: Double, val color: Float)

/**
 * A static object that holds the vector definitions for shapes.
 */
object ShapeData {

    // Base color hues for the shapes
    private const val ORANGE_HUE = BitmapDescriptorFactory.HUE_ORANGE
    private const val BLACK_HUE = 0f // Black doesn't have a hue, we'll handle it
    private const val GREEN_HUE = BitmapDescriptorFactory.HUE_GREEN
    private const val BROWN_HUE = 30f // Brownish hue
    private const val YELLOW_HUE = BitmapDescriptorFactory.HUE_YELLOW
    private const val RED_HUE = BitmapDescriptorFactory.HUE_RED
    private const val BLUE_HUE = BitmapDescriptorFactory.HUE_AZURE

    /**
     * Defines the points for a Jack-O'-Lantern shape.
     * Total points: 56
     */
    val jackOLanternShape: List<ShapePoint> = listOf(
        // Pumpkin Outline (scaled up)
        ShapePoint(0.2, 0.0, ORANGE_HUE),
        ShapePoint(0.196, 0.04, ORANGE_HUE),
        ShapePoint(0.184, 0.08, ORANGE_HUE),
        ShapePoint(0.16, 0.12, ORANGE_HUE),
        ShapePoint(0.12, 0.16, ORANGE_HUE),
        ShapePoint(0.08, 0.184, ORANGE_HUE),
        ShapePoint(0.04, 0.196, ORANGE_HUE),
        ShapePoint(0.0, 0.2, ORANGE_HUE),
        ShapePoint(-0.04, 0.196, ORANGE_HUE),
        ShapePoint(-0.08, 0.184, ORANGE_HUE),
        ShapePoint(-0.12, 0.16, ORANGE_HUE),
        ShapePoint(-0.16, 0.12, ORANGE_HUE),
        ShapePoint(-0.184, 0.08, ORANGE_HUE),
        ShapePoint(-0.196, 0.04, ORANGE_HUE),
        ShapePoint(-0.2, 0.0, ORANGE_HUE),
        ShapePoint(-0.196, -0.04, ORANGE_HUE),
        ShapePoint(-0.184, -0.08, ORANGE_HUE),
        ShapePoint(-0.16, -0.12, ORANGE_HUE),
        ShapePoint(-0.12, -0.16, ORANGE_HUE),
        ShapePoint(-0.08, -0.184, ORANGE_HUE),
        ShapePoint(-0.04, -0.196, ORANGE_HUE),
        ShapePoint(0.0, -0.2, ORANGE_HUE),
        ShapePoint(0.04, -0.196, ORANGE_HUE),
        ShapePoint(0.08, -0.184, ORANGE_HUE),
        ShapePoint(0.12, -0.16, ORANGE_HUE),
        ShapePoint(0.16, -0.12, ORANGE_HUE),
        ShapePoint(0.184, -0.08, ORANGE_HUE),
        ShapePoint(0.196, -0.04, ORANGE_HUE),

        // Left Eye
        ShapePoint(0.1, -0.1, BLACK_HUE),
        ShapePoint(0.04, -0.06, BLACK_HUE),
        ShapePoint(0.04, -0.14, BLACK_HUE),

        // Right Eye
        ShapePoint(0.1, 0.1, BLACK_HUE),
        ShapePoint(0.04, 0.06, BLACK_HUE),
        ShapePoint(0.04, 0.14, BLACK_HUE),

        // Nose
        ShapePoint(-0.02, 0.0, BLACK_HUE),
        ShapePoint(-0.06, -0.04, BLACK_HUE),
        ShapePoint(-0.06, 0.04, BLACK_HUE),

        // Mouth with teeth (more points)
        ShapePoint(-0.1, -0.12, BLACK_HUE),
        ShapePoint(-0.12, -0.1, BLACK_HUE), // down
        ShapePoint(-0.1, -0.08, BLACK_HUE), // up (tooth)
        ShapePoint(-0.12, -0.06, BLACK_HUE), // down
        ShapePoint(-0.1, -0.04, BLACK_HUE), // up (tooth)
        ShapePoint(-0.12, -0.02, BLACK_HUE), // down
        ShapePoint(-0.1, 0.0, BLACK_HUE),   // up (center)
        ShapePoint(-0.12, 0.02, BLACK_HUE),  // down
        ShapePoint(-0.1, 0.04, BLACK_HUE),   // up (tooth)
        ShapePoint(-0.12, 0.06, BLACK_HUE),  // down
        ShapePoint(-0.1, 0.08, BLACK_HUE),   // up (tooth)
        ShapePoint(-0.12, 0.1, BLACK_HUE),  // down
        ShapePoint(-0.1, 0.12, BLACK_HUE),
        ShapePoint(-0.14, 0.0, BLACK_HUE), // Center dip
        ShapePoint(-0.15, -0.12, BLACK_HUE),
        ShapePoint(-0.15, 0.12, BLACK_HUE),
        ShapePoint(0.16, 0.0, ORANGE_HUE), // Extra point to match count
        ShapePoint(0.0, 0.0, ORANGE_HUE), // Extra point to match count
        ShapePoint(-0.16, 0.0, ORANGE_HUE) // Extra point to match count
    )

    /**
     * Defines the points for a Christmas Tree shape.
     * Must have the same number of points as jackOLanternShape.
     * Total points: 56
     */
    val christmasTreeShape: List<ShapePoint> = listOf(
        // Star Topper (5 points)
        ShapePoint(0.22, 0.0, YELLOW_HUE),
        ShapePoint(0.19, 0.03, YELLOW_HUE),
        ShapePoint(0.19, -0.03, YELLOW_HUE),
        ShapePoint(0.17, 0.05, YELLOW_HUE),
        ShapePoint(0.17, -0.05, YELLOW_HUE),

        // Top Tier (6 points)
        ShapePoint(0.15, 0.0, GREEN_HUE),
        ShapePoint(0.12, 0.08, GREEN_HUE),
        ShapePoint(0.12, -0.08, GREEN_HUE),
        ShapePoint(0.13, 0.04, GREEN_HUE),
        ShapePoint(0.13, -0.04, GREEN_HUE),
        ShapePoint(0.10, 0.0, GREEN_HUE),

        // Middle Tier (10 points)
        ShapePoint(0.08, 0.12, GREEN_HUE),
        ShapePoint(0.08, -0.12, GREEN_HUE),
        ShapePoint(0.06, 0.08, GREEN_HUE),
        ShapePoint(0.06, -0.08, GREEN_HUE),
        ShapePoint(0.04, 0.16, GREEN_HUE),
        ShapePoint(0.04, -0.16, GREEN_HUE),
        ShapePoint(0.02, 0.12, GREEN_HUE),
        ShapePoint(0.02, -0.12, GREEN_HUE),
        ShapePoint(0.0, 0.08, GREEN_HUE),
        ShapePoint(0.0, -0.08, GREEN_HUE),

        // Bottom Tier (14 points)
        ShapePoint(-0.02, 0.22, GREEN_HUE),
        ShapePoint(-0.02, -0.22, GREEN_HUE),
        ShapePoint(-0.04, 0.18, GREEN_HUE),
        ShapePoint(-0.04, -0.18, GREEN_HUE),
        ShapePoint(-0.06, 0.24, GREEN_HUE),
        ShapePoint(-0.06, -0.24, GREEN_HUE),
        ShapePoint(-0.08, 0.20, GREEN_HUE),
        ShapePoint(-0.08, -0.20, GREEN_HUE),
        ShapePoint(-0.1, 0.16, GREEN_HUE),
        ShapePoint(-0.1, -0.16, GREEN_HUE),
        ShapePoint(-0.12, 0.12, GREEN_HUE),
        ShapePoint(-0.12, -0.12, GREEN_HUE),
        ShapePoint(-0.14, 0.08, GREEN_HUE),
        ShapePoint(-0.14, -0.08, GREEN_HUE),

        // Trunk (4 points)
        ShapePoint(-0.16, 0.04, BROWN_HUE),
        ShapePoint(-0.16, -0.04, BROWN_HUE),
        ShapePoint(-0.2, 0.04, BROWN_HUE),
        ShapePoint(-0.2, -0.04, BROWN_HUE),

        // Ornaments (17 points)
        ShapePoint(0.11, 0.0, RED_HUE),
        ShapePoint(0.07, 0.05, BLUE_HUE),
        ShapePoint(0.07, -0.05, YELLOW_HUE),
        ShapePoint(0.03, 0.1, RED_HUE),
        ShapePoint(0.03, -0.1, BLUE_HUE),
        ShapePoint(-0.01, 0.15, YELLOW_HUE),
        ShapePoint(-0.01, -0.15, RED_HUE),
        ShapePoint(-0.05, 0.2, BLUE_HUE),
        ShapePoint(-0.05, -0.2, YELLOW_HUE),
        ShapePoint(-0.09, 0.15, RED_HUE),
        ShapePoint(-0.09, -0.15, BLUE_HUE),
        ShapePoint(-0.13, 0.0, YELLOW_HUE),
        ShapePoint(0.0, 0.0, RED_HUE),
        ShapePoint(-0.04, 0.0, BLUE_HUE),
        ShapePoint(-0.08, 0.0, YELLOW_HUE),
        ShapePoint(-0.12, 0.05, RED_HUE),
        ShapePoint(-0.12, -0.05, BLUE_HUE)
    )
}
