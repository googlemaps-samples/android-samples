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

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.common_ui.R
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.ReviewStatus
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.repository.SampleReviewRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SampleExpectationsBottomSheet : BottomSheetDialogFragment() {

    private var sample: SampleItem? = null
    private var currentFramework: Framework = Framework.KOTLIN_VIEWS
    private var onLaunchRequested: ((SampleItem, Framework) -> Unit)? = null

    companion object {
        private const val ARG_SAMPLE = "arg_sample"
        private const val ARG_FRAMEWORK = "arg_framework"

        fun newInstance(
            sample: SampleItem,
            framework: Framework,
            onLaunch: ((SampleItem, Framework) -> Unit)? = null
        ): SampleExpectationsBottomSheet {
            return SampleExpectationsBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SAMPLE, sample)
                    putSerializable(ARG_FRAMEWORK, framework)
                }
                this.onLaunchRequested = onLaunch
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sample = arguments?.getSerializable(ARG_SAMPLE) as? SampleItem
        currentFramework = (arguments?.getSerializable(ARG_FRAMEWORK) as? Framework) ?: Framework.KOTLIN_VIEWS
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_sample_expectations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val s = sample ?: return

        val sheetTitle: TextView = view.findViewById(R.id.sheet_title)
        val sheetComplexityChip: Chip = view.findViewById(R.id.sheet_complexity_chip)
        val sheetCategoryTags: TextView = view.findViewById(R.id.sheet_category_tags)
        val sheetHtmlContent: TextView = view.findViewById(R.id.sheet_html_content)
        val statusToggleGroup: MaterialButtonToggleGroup = view.findViewById(R.id.status_toggle_group)
        val btnPassing: MaterialButton = view.findViewById(R.id.btn_status_passing)
        val btnNeedsWork: MaterialButton = view.findViewById(R.id.btn_status_needs_work)
        val btnUnchecked: MaterialButton = view.findViewById(R.id.btn_status_unchecked)
        val editTextNotes: TextInputEditText = view.findViewById(R.id.edit_text_notes)
        val btnSaveEvaluation: MaterialButton = view.findViewById(R.id.btn_save_evaluation)
        val btnLaunch: MaterialButton = view.findViewById(R.id.btn_launch_from_sheet)
        val btnSwitchFramework: MaterialButton = view.findViewById(R.id.btn_switch_framework)

        sheetTitle.text = s.title
        sheetComplexityChip.text = "${s.complexity.badge} ${s.complexity.displayName}"
        sheetCategoryTags.text = "${s.category} • ${s.tags.joinToString(" ")}"

        // Render HTML formatted expectations
        sheetHtmlContent.text = Html.fromHtml(s.getFormattedHelpHtml(), Html.FROM_HTML_MODE_COMPACT)

        val repository = SampleReviewRepository.getInstance(requireContext())

        // Load existing review from Room DB
        lifecycleScope.launch {
            val eval = repository.getEvaluation(s.id)
            val status = ReviewStatus.fromString(eval?.status)
            when (status) {
                ReviewStatus.PASSING -> statusToggleGroup.check(R.id.btn_status_passing)
                ReviewStatus.NEEDS_WORK -> statusToggleGroup.check(R.id.btn_status_needs_work)
                ReviewStatus.UNCHECKED -> statusToggleGroup.check(R.id.btn_status_unchecked)
            }
            if (!eval?.notes.isNullOrBlank()) {
                editTextNotes.setText(eval?.notes)
            }
        }

        btnSaveEvaluation.setOnClickListener {
            val selectedStatus = when (statusToggleGroup.checkedButtonId) {
                R.id.btn_status_passing -> ReviewStatus.PASSING
                R.id.btn_status_needs_work -> ReviewStatus.NEEDS_WORK
                else -> ReviewStatus.UNCHECKED
            }
            val notes = editTextNotes.text?.toString()?.trim().orEmpty()

            repository.saveEvaluation(s.id, selectedStatus, notes, s)
            Toast.makeText(requireContext(), "Saved review for ${s.title}", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // Framework switcher button
        val altFramework = when (currentFramework) {
            Framework.KOTLIN_VIEWS -> if (s.javaActivity != null) Framework.JAVA_VIEWS else if (s.composeActivity != null) Framework.COMPOSE else null
            Framework.JAVA_VIEWS -> if (s.kotlinActivity != null) Framework.KOTLIN_VIEWS else if (s.composeActivity != null) Framework.COMPOSE else null
            Framework.COMPOSE -> if (s.kotlinActivity != null) Framework.KOTLIN_VIEWS else if (s.javaActivity != null) Framework.JAVA_VIEWS else null
        }

        if (altFramework != null) {
            btnSwitchFramework.visibility = View.VISIBLE
            btnSwitchFramework.text = "⇄ Switch to ${altFramework.displayName}"
            btnSwitchFramework.setOnClickListener {
                dismiss()
                onLaunchRequested?.invoke(s, altFramework)
            }
        } else {
            btnSwitchFramework.visibility = View.GONE
        }

        btnLaunch.setOnClickListener {
            dismiss()
            onLaunchRequested?.invoke(s, currentFramework)
        }
    }
}
