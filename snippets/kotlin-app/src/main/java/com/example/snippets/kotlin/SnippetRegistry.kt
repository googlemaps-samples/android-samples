/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snippets.kotlin

import android.content.Context
import com.example.snippets.kotlin.annotations.SnippetGroup
import com.example.snippets.kotlin.annotations.SnippetItem
import com.example.snippets.kotlin.snippets.CameraControlSnippets
import com.example.snippets.kotlin.snippets.CloudCustomizationSnippets
import com.example.snippets.kotlin.snippets.DataDrivenBoundarySnippets
import com.example.snippets.kotlin.snippets.DatasetLayerSnippets
import com.example.snippets.kotlin.snippets.EventsSnippets
import com.example.snippets.kotlin.snippets.KtxSnippets
import com.example.snippets.kotlin.snippets.MapInitSnippets
import com.example.snippets.kotlin.snippets.MarkerSnippets
import com.example.snippets.kotlin.snippets.MyLocationSnippets
import com.example.snippets.kotlin.snippets.OverlaySnippets
import com.example.snippets.kotlin.snippets.ShapesSnippets
import com.example.snippets.kotlin.snippets.StreetViewSnippets
import com.example.snippets.kotlin.snippets.UtilsSnippets
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.TileOverlay
import kotlinx.coroutines.CoroutineScope

data class SnippetGroupInfo(
    val title: String,
    val description: String,
    val items: List<SnippetItemInfo>,
)

data class SnippetItemInfo(
    val title: String,
    val description: String,
    val groupTitle: String,
    val action: (Context, GoogleMap, CoroutineScope) -> Unit,
)

object SnippetRegistry {
    private val addedElements = mutableListOf<Any>()

    fun clearTrackedItems() {
        addedElements.forEach { item ->
            try {
                when (item) {
                    is Marker -> item.remove()
                    is Polyline -> item.remove()
                    is Polygon -> item.remove()
                    is Circle -> item.remove()
                    is GroundOverlay -> item.remove()
                    is TileOverlay -> item.remove()
                }
            } catch (e: Exception) {
                // Ignore failures to avoid crashing if already cleanup or invalid
            }
        }
        addedElements.clear()
    }

    private val snippetClasses = listOf(
        CameraControlSnippets::class.java,
        CloudCustomizationSnippets::class.java,
        DataDrivenBoundarySnippets::class.java,
        DatasetLayerSnippets::class.java,
        EventsSnippets::class.java,
        KtxSnippets::class.java,
        MapInitSnippets::class.java,
        MarkerSnippets::class.java,
        MyLocationSnippets::class.java,
        OverlaySnippets::class.java,
        ShapesSnippets::class.java,
        StreetViewSnippets::class.java,
        UtilsSnippets::class.java,
    )

    /**
     * Scans annotated classes to build the hierarchical snippet model.
     */
    fun getSnippetGroups(): List<SnippetGroupInfo> {
        val groups = mutableListOf<SnippetGroupInfo>()

        for (clazz in snippetClasses) {
            val groupAnnotation = clazz.getAnnotation(SnippetGroup::class.java) ?: continue
            val items = mutableListOf<SnippetItemInfo>()

            for (method in clazz.declaredMethods) {
                val itemAnnotation = method.getAnnotation(SnippetItem::class.java) ?: continue

                items.add(
                    SnippetItemInfo(
                        title = itemAnnotation.title,
                        description = itemAnnotation.description,
                        groupTitle = groupAnnotation.title,
                        action = { context, map, scope ->
                            try {
                                val trackedMap = TrackedMap(map, addedElements)
                                val instance = createInstance(clazz, context, trackedMap, scope)
                                // Invoke method, passing parameters if needed, or assuming no-arg
                                // Most snippet methods take no args because they use initialized class fields
                                if (method.parameterCount == 0) {
                                    method.invoke(instance)
                                } else if (method.parameterCount == 1 && method.parameterTypes[0] == Context::class.java) {
                                    method.invoke(instance, context)
                                } else {
                                    // Fallback or log error
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                    ),
                )
            }

            if (items.isNotEmpty()) {
                items.sortBy { it.title }
                groups.add(
                    SnippetGroupInfo(
                        title = groupAnnotation.title,
                        description = groupAnnotation.description,
                        items = items,
                    ),
                )
            }
        }
        return groups
    }

    private fun createInstance(clazz: Class<*>, context: Context, map: TrackedMap, scope: CoroutineScope): Any {
        return try {
            clazz.getConstructor(Context::class.java, TrackedMap::class.java, CoroutineScope::class.java).newInstance(context, map, scope)
        } catch (e: Exception) {
            try {
                clazz.getConstructor(TrackedMap::class.java, CoroutineScope::class.java).newInstance(map, scope)
            } catch (e: Exception) {
                try {
                    clazz.getConstructor(Context::class.java, TrackedMap::class.java).newInstance(context, map)
                } catch (e: Exception) {
                    try {
                        clazz.getConstructor(TrackedMap::class.java).newInstance(map)
                    } catch (e: Exception) {
                        clazz.getConstructor().newInstance()
                    }
                }
            }
        }
    }

    val snippets: Map<String, SnippetItemInfo> by lazy {
        getSnippetGroups().flatMap { group ->
            group.items.map { item -> "${item.groupTitle} - ${item.title}" to item }
        }.toMap()
    }
}
