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

package com.example.common_ui.catalog.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.common_ui.R
import com.example.common_ui.catalog.Complexity
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.db.SampleEvaluationEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class SampleCardAdapter(
    private val onSampleClick: (SampleItem) -> Unit,
    private val onInfoClick: (SampleItem) -> Unit,
    private val onStatusClick: (SampleItem, SampleEvaluationEntity?) -> Unit
) : ListAdapter<SampleItem, SampleCardAdapter.SampleViewHolder>(SampleDiffCallback()) {

    var currentFramework: Framework = Framework.KOTLIN_VIEWS
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isReviewerMode: Boolean = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var evaluationsMap: Map<String, SampleEvaluationEntity> = emptyMap()

    fun updateEvaluations(evaluations: List<SampleEvaluationEntity>) {
        evaluationsMap = evaluations.associateBy { it.sampleId }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sample_card, parent, false)
        return SampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.card_view)
        private val textCategory: TextView = itemView.findViewById(R.id.text_category)
        private val chipComplexity: Chip = itemView.findViewById(R.id.chip_complexity)
        private val btnStatusBadge: MaterialButton = itemView.findViewById(R.id.btn_status_badge)
        private val textTitle: TextView = itemView.findViewById(R.id.text_title)
        private val textDescription: TextView = itemView.findViewById(R.id.text_description)
        private val textNotesPreview: TextView = itemView.findViewById(R.id.text_notes_preview)
        private val textTags: TextView = itemView.findViewById(R.id.text_tags)
        private val btnInfo: MaterialButton = itemView.findViewById(R.id.btn_info)
        private val btnLaunch: MaterialButton = itemView.findViewById(R.id.btn_launch)

        fun bind(sample: SampleItem) {
            textCategory.text = sample.category
            textTitle.text = sample.title
            textDescription.text = sample.description

            // Complexity chip
            chipComplexity.text = "${sample.complexity.badge} ${sample.complexity.displayName}"

            // Hashtags
            if (sample.tags.isNotEmpty()) {
                textTags.visibility = View.VISIBLE
                textTags.text = sample.tags.joinToString(" ")
            } else {
                textTags.visibility = View.GONE
            }

            // Reviewer evaluation
            val evaluation = evaluationsMap[sample.id]
            val status = ReviewStatus.fromString(evaluation?.status)

            if (isReviewerMode) {
                btnStatusBadge.visibility = View.VISIBLE
                btnStatusBadge.text = status.displayName
                when (status) {
                    ReviewStatus.PASSING -> {
                        btnStatusBadge.setIconResource(R.drawable.ic_status_passing)
                        btnStatusBadge.iconTint = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                        btnStatusBadge.setTextColor(Color.parseColor("#2E7D32"))
                    }
                    ReviewStatus.NEEDS_WORK -> {
                        btnStatusBadge.setIconResource(R.drawable.ic_status_needs_work)
                        btnStatusBadge.iconTint = ColorStateList.valueOf(Color.parseColor("#F44336"))
                        btnStatusBadge.setTextColor(Color.parseColor("#C62828"))
                    }
                    ReviewStatus.UNCHECKED -> {
                        btnStatusBadge.setIconResource(R.drawable.ic_status_unchecked)
                        btnStatusBadge.iconTint = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
                        btnStatusBadge.setTextColor(Color.parseColor("#616161"))
                    }
                }

                // Show notes preview if present
                if (!evaluation?.notes.isNullOrBlank()) {
                    textNotesPreview.visibility = View.VISIBLE
                    textNotesPreview.text = "📝 Note: ${evaluation?.notes}"
                } else {
                    textNotesPreview.visibility = View.GONE
                }
            } else {
                btnStatusBadge.visibility = View.GONE
                textNotesPreview.visibility = View.GONE
            }

            btnStatusBadge.setOnClickListener {
                onStatusClick(sample, evaluation)
            }

            btnInfo.setOnClickListener {
                onInfoClick(sample)
            }

            val hasActivity = sample.getActivityForFramework(currentFramework) != null
            btnLaunch.isEnabled = hasActivity
            btnLaunch.alpha = if (hasActivity) 1.0f else 0.4f
            btnLaunch.text = if (hasActivity) "Launch Sample" else "No ${currentFramework.badge} Impl"

            btnLaunch.setOnClickListener {
                if (hasActivity) {
                    onSampleClick(sample)
                }
            }

            cardView.setOnClickListener {
                if (hasActivity) {
                    onSampleClick(sample)
                } else {
                    onInfoClick(sample)
                }
            }
        }
    }

    class SampleDiffCallback : DiffUtil.ItemCallback<SampleItem>() {
        override fun areItemsTheSame(oldItem: SampleItem, newItem: SampleItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SampleItem, newItem: SampleItem): Boolean =
            oldItem == newItem
    }
}
