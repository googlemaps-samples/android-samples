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

/** Model class containing metadata and execution logic for an individual snippet item. */
public class SnippetItemInfo {
    private final String title;
    private final String description;
    private final String groupTitle;
    private final SnippetAction action;

    public SnippetItemInfo(
            String title, String description, String groupTitle, SnippetAction action) {
        this.title = title;
        this.description = description;
        this.groupTitle = groupTitle;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public SnippetAction getAction() {
        return action;
    }
}
