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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snippets.common.R

class SnippetGroupAdapter(
    private val allGroups: List<SnippetGroupInfo>,
    private val listener: (SnippetItemInfo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private val collapsedGroups = mutableSetOf<String>()
    private val visibleItems = mutableListOf<Any>()

    init {
        updateVisibleItems()
    }

    private fun updateVisibleItems() {
        visibleItems.clear()
        for (group in allGroups) {
            visibleItems.add(group)
            if (group.title !in collapsedGroups) {
                visibleItems.addAll(group.items)
            }
        }
    }

    private fun toggleGroup(group: SnippetGroupInfo) {
        if (group.title in collapsedGroups) {
            collapsedGroups.remove(group.title)
        } else {
            collapsedGroups.add(group.title)
        }
        updateVisibleItems()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (visibleItems[position] is SnippetGroupInfo) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.list_item_group_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.list_item_snippet, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = visibleItems[position]
        if (holder is HeaderViewHolder) {
            val group = item as SnippetGroupInfo
            holder.headerTitle.text = group.title
            holder.itemView.setOnClickListener { toggleGroup(group) }
        } else if (holder is ItemViewHolder) {
            val snippet = item as SnippetItemInfo
            holder.title.text = snippet.title
            holder.description.text = snippet.description
            holder.itemView.setOnClickListener { listener(snippet) }
        }
    }

    override fun getItemCount(): Int = visibleItems.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTitle: TextView = view.findViewById(R.id.headerTitle)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
    }
}
