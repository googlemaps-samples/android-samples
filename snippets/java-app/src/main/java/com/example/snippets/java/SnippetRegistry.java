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

package com.example.snippets.java;

import android.content.Context;
import com.example.snippets.java.annotations.SnippetGroup;
import com.example.snippets.java.annotations.SnippetItem;
import com.example.snippets.java.snippets.CameraControlSnippets;
import com.example.snippets.java.snippets.CloudCustomizationSnippets;
import com.example.snippets.java.snippets.DataDrivenBoundarySnippets;
import com.example.snippets.java.snippets.DatasetLayerSnippets;
import com.example.snippets.java.snippets.EventsSnippets;
import com.example.snippets.java.snippets.MapInitSnippets;
import com.example.snippets.java.snippets.MarkerSnippets;
import com.example.snippets.java.snippets.MyLocationSnippets;
import com.example.snippets.java.snippets.OverlaySnippets;
import com.example.snippets.java.snippets.ShapesSnippets;
import com.example.snippets.java.snippets.StreetViewSnippets;
import com.example.snippets.java.snippets.UtilsSnippets;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.TileOverlay;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SnippetRegistry {

    private static final List<Object> addedElements = new ArrayList<>();

    public static void clearTrackedItems() {
        for (Object item : addedElements) {
            try {
                if (item instanceof Marker) ((Marker) item).remove();
                else if (item instanceof Polyline) ((Polyline) item).remove();
                else if (item instanceof Polygon) ((Polygon) item).remove();
                else if (item instanceof Circle) ((Circle) item).remove();
                else if (item instanceof GroundOverlay) ((GroundOverlay) item).remove();
                else if (item instanceof TileOverlay) ((TileOverlay) item).remove();
            } catch (Exception e) {
                /* ignore */
            }
        }
        addedElements.clear();
    }

    private static final List<Class<?>> snippetClasses =
            Arrays.asList(
                    CameraControlSnippets.class,
                    CloudCustomizationSnippets.class,
                    DataDrivenBoundarySnippets.class,
                    DatasetLayerSnippets.class,
                    EventsSnippets.class,
                    MapInitSnippets.class,
                    MarkerSnippets.class,
                    MyLocationSnippets.class,
                    OverlaySnippets.class,
                    ShapesSnippets.class,
                    StreetViewSnippets.class,
                    UtilsSnippets.class
            );

    /** Scans annotated classes to build the hierarchical snippet model. */
    public static List<SnippetGroupInfo> getSnippetGroups() {
        List<SnippetGroupInfo> groups = new ArrayList<>();

        for (Class<?> clazz : snippetClasses) {
            SnippetGroup groupAnnotation = clazz.getAnnotation(SnippetGroup.class);
            if (groupAnnotation == null) continue;

            List<SnippetItemInfo> items = new ArrayList<>();

            for (Method method : clazz.getDeclaredMethods()) {
                SnippetItem itemAnnotation = method.getAnnotation(SnippetItem.class);
                if (itemAnnotation == null) continue;

                items.add(
                        new SnippetItemInfo(
                                itemAnnotation.title(),
                                itemAnnotation.description(),
                                groupAnnotation.title(),
                                (context, map) -> {
                                    try {
                                        TrackedMap trackedMap =
                                                new TrackedMap(map, addedElements);
                                        Object instance =
                                                createInstance(clazz, context, trackedMap);
                                        if (method.getParameterCount() == 0) {
                                            method.invoke(instance);
                                        } else if (method.getParameterCount() == 1
                                                && method.getParameterTypes()[0] == Context.class) {
                                            method.invoke(instance, context);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }));
            }

            if (!items.isEmpty()) {
                items.sort(Comparator.comparing(SnippetItemInfo::getTitle));
                groups.add(
                        new SnippetGroupInfo(
                                groupAnnotation.title(), groupAnnotation.description(), items));
            }
        }
        return groups;
    }

    private static Object createInstance(Class<?> clazz, Context context, TrackedMap map)
            throws Exception {
        try {
            return clazz.getConstructor(Context.class, TrackedMap.class)
                    .newInstance(context, map);
        } catch (NoSuchMethodException e1) {
            try {
                return clazz.getConstructor(TrackedMap.class).newInstance(map);
            } catch (NoSuchMethodException e2) {
                return clazz.getConstructor().newInstance();
            }
        }
    }

    public static final Map<String, SnippetItemInfo> snippets = new LinkedHashMap<>();

    static {
        for (SnippetGroupInfo group : getSnippetGroups()) {
            for (SnippetItemInfo item : group.getItems()) {
                snippets.put(group.getTitle() + " - " + item.getTitle(), item);
            }
        }
    }
}
