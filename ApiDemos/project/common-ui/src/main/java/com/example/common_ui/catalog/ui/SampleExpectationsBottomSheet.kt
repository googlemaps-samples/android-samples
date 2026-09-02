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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.lifecycleScope
import com.example.common_ui.catalog.Framework
import com.example.common_ui.catalog.SampleItem
import com.example.common_ui.catalog.compose.SampleDetailContent
import com.example.common_ui.catalog.db.SampleEvaluationEntity
import com.example.common_ui.catalog.repository.SampleReviewRepository
import kotlinx.coroutines.launch

/**
 * On-device "Criteria & Purpose" dialog that displays the full "Info & Code" section,
 * including:
 * - Full Purpose & Acceptance Criteria
 * - Syntax-highlighted Source Code Snippet with Kotlin & Java tabs (CodeSnippetView)
 * - Reviewer evaluation controls, notes, and attached annotated screenshots
 * - Pinned bottom bar with framework switching, Save, and Save & Next
 */
class SampleExpectationsBottomSheet : AppCompatDialogFragment() {

    private var sample: SampleItem? = null
    private var currentFramework: Framework = Framework.KOTLIN_VIEWS
    private var onLaunchRequested: ((SampleItem, Framework) -> Unit)? = null

    companion object {
        private const val ARG_SAMPLE = "arg_sample"
        private const val ARG_FRAMEWORK = "arg_framework"

        @JvmStatic
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
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_Material3_DayNight_NoActionBar)
        sample = arguments?.getSerializable(ARG_SAMPLE) as? SampleItem
        currentFramework = (arguments?.getSerializable(ARG_FRAMEWORK) as? Framework) ?: Framework.KOTLIN_VIEWS
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val s = sample
                    if (s != null) {
                        val repository = remember { SampleReviewRepository.getInstance(context) }
                        var evaluation by remember { mutableStateOf<SampleEvaluationEntity?>(null) }
                        val targetFqcn = s.getTargetFqcn(currentFramework)

                        LaunchedEffect(s.id) {
                            evaluation = repository.getEvaluation(targetFqcn) ?: repository.getEvaluation(s.id)
                        }

                        SampleDetailContent(
                            sample = s,
                            targetFqcn = targetFqcn,
                            framework = currentFramework,
                            isReviewerMode = true,
                            existingEvaluation = evaluation,
                            onDismiss = { dismiss() },
                            onSaveEvaluation = { status, notes ->
                                repository.saveEvaluation(targetFqcn, status, notes, s) {
                                    Toast.makeText(context, "Saved review for ${s.title}", Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }
                            },
                            onSaveAndNext = { status, notes ->
                                repository.saveEvaluation(targetFqcn, status, notes, s) {
                                    dismiss()
                                    lifecycleScope.launch {
                                        val nextSample = repository.getNextUncheckedSample(s.id, currentFramework)
                                        val act = activity
                                        if (act != null) {
                                            if (nextSample != null) {
                                                SampleReviewRepository.launchSample(act, nextSample, currentFramework)
                                            } else {
                                                Toast.makeText(act, "🎉 All ${currentFramework.displayName} samples reviewed!", Toast.LENGTH_LONG).show()
                                                act.finish()
                                            }
                                        }
                                    }
                                }
                            },
                            onLaunch = { fw ->
                                dismiss()
                                onLaunchRequested?.invoke(s, fw)
                            }
                        )
                    }
                }
            }
        }
    }
}
