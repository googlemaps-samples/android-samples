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

import java.util.List;

/** Model class containing metadata and items for a grouped category of snippets. */
public class SnippetGroupInfo {
    private final String title;
    private final String description;
    private final List<SnippetItemInfo> items;

    public SnippetGroupInfo(String title, String description, List<SnippetItemInfo> items) {
        this.title = title;
        this.description = description;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<SnippetItemInfo> getItems() {
        return items;
    }
}
