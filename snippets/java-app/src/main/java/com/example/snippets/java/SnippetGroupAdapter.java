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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.snippets.common.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SnippetGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final List<SnippetGroupInfo> allGroups;
    private final Set<String> collapsedGroups = new HashSet<>();
    private final List<Object> visibleItems = new ArrayList<>();
    private final OnSnippetClickListener listener;

    public interface OnSnippetClickListener {
        void onSnippetClick(SnippetItemInfo item);
    }

    public SnippetGroupAdapter(List<SnippetGroupInfo> groups, OnSnippetClickListener listener) {
        this.allGroups = groups;
        this.listener = listener;
        updateVisibleItems();
    }

    private void updateVisibleItems() {
        visibleItems.clear();
        for (SnippetGroupInfo group : allGroups) {
            visibleItems.add(group);
            if (!collapsedGroups.contains(group.getTitle())) {
                visibleItems.addAll(group.getItems());
            }
        }
    }

    private void toggleGroup(SnippetGroupInfo group) {
        if (collapsedGroups.contains(group.getTitle())) {
            collapsedGroups.remove(group.getTitle());
        } else {
            collapsedGroups.add(group.getTitle());
        }
        updateVisibleItems();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = visibleItems.get(position);
        if (item instanceof SnippetGroupInfo) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.list_item_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.list_item_snippet, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = visibleItems.get(position);
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            SnippetGroupInfo group = (SnippetGroupInfo) item;
            headerHolder.headerTitle.setText(group.getTitle());
            headerHolder.itemView.setOnClickListener(v -> toggleGroup(group));
        } else {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            SnippetItemInfo snippet = (SnippetItemInfo) item;
            itemHolder.title.setText(snippet.getTitle());
            itemHolder.description.setText(snippet.getDescription());
            itemHolder.itemView.setOnClickListener(v -> listener.onSnippetClick(snippet));
        }
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView headerTitle;

        HeaderViewHolder(View view) {
            super(view);
            headerTitle = view.findViewById(R.id.headerTitle);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;

        ItemViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
        }
    }
}
