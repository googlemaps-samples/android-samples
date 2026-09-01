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

package com.example.common_ui.catalog.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.common_ui.catalog.db.SampleEvaluationEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility that compiles evaluated sample reviews and notes into an "Airing of Grievances" markdown report
 * and saves it to the device's public or shared storage.
 */
object GrievanceReportExporter {

    fun generateAndSaveReport(
        context: Context,
        evaluations: List<SampleEvaluationEntity>
    ): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val fileDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
        val now = Date()
        val formattedDate = dateFormat.format(now)
        val fileSuffix = fileDateFormat.format(now)

        val passingCount = evaluations.count { it.status == "PASSING" }
        val needsWorkCount = evaluations.count { it.status == "NEEDS_WORK" }
        val uncheckedCount = evaluations.count { it.status == "UNCHECKED" }
        val totalWithNotes = evaluations.count { it.notes.isNotBlank() }

        val md = StringBuilder()
        md.append("# 📢 Google Maps Platform Samples - Airing of Grievances Report\n\n")
        md.append("> Generated on **$formattedDate**\n\n")

        md.append("## 📱 Device & Environment Metadata\n")
        md.append("- **Device Model**: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.PRODUCT})\n")
        md.append("- **Android Version**: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
        md.append("- **Build Fingerprint**: `${Build.FINGERPRINT}`\n")
        md.append("- **Package**: `${context.packageName}`\n\n")

        md.append("## 📊 Evaluation Summary\n")
        md.append("| Metric | Count |\n")
        md.append("|---|---|\n")
        md.append("| **Total Samples Evaluated** | ${evaluations.size} |\n")
        md.append("| 🟢 **Passing** | $passingCount |\n")
        md.append("| 🔴 **Needs Work / Broken** | $needsWorkCount |\n")
        md.append("| ⚪ **Unchecked** | $uncheckedCount |\n")
        md.append("| 📝 **Samples with Reviewer Notes** | $totalWithNotes |\n\n")

        val grievances = evaluations.filter { it.status == "NEEDS_WORK" || it.notes.isNotBlank() }
        if (grievances.isNotEmpty()) {
            md.append("## ⚠️ Airing of Grievances (Issues & Requested Improvements)\n\n")
            grievances.forEachIndexed { index, item ->
                val statusEmoji = if (item.status == "NEEDS_WORK") "🔴" else if (item.status == "PASSING") "🟢" else "⚪"
                md.append("### ${index + 1}. $statusEmoji ${item.sampleTitle}\n")
                md.append("- **Category**: ${item.category}\n")
                md.append("- **Status**: `${item.status}`\n")
                md.append("- **Activity**: `${item.activityName}`\n")
                md.append("- **Last Reviewed**: ${dateFormat.format(Date(item.lastUpdated))}\n")
                if (item.notes.isNotBlank()) {
                    md.append("- **Reviewer Notes & Grievances**:\n")
                    md.append("  > ${item.notes.replace("\n", "\n  > ")}\n")
                } else {
                    md.append("- **Reviewer Notes**: *Flagged as Needs Work without additional notes.*\n")
                }
                md.append("\n")
            }
        } else {
            md.append("## ⚠️ Airing of Grievances\n")
            md.append("*No issues or grievances recorded. All checked samples are passing! 🎉*\n\n")
        }

        md.append("## 📋 Complete Evaluation Log\n\n")
        md.append("| Status | Sample Title | Category | Activity | Notes |\n")
        md.append("|---|---|---|---|---|\n")
        evaluations.forEach { item ->
            val statusBadge = when (item.status) {
                "PASSING" -> "🟢 PASS"
                "NEEDS_WORK" -> "🔴 NEEDS WORK"
                else -> "⚪ UNCHECKED"
            }
            val notePreview = if (item.notes.isNotBlank()) item.notes.replace("|", "\\|").replace("\n", " ") else "-"
            md.append("| $statusBadge | ${item.sampleTitle} | ${item.category} | `${item.activityName.substringAfterLast('.')}` | $notePreview |\n")
        }
        md.append("\n---\n*Report generated by GMP Sample Reviewer Mode.*\n")

        val fileName = "airing_of_grievances_${fileSuffix}.md"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetDir = if (downloadsDir != null && downloadsDir.exists() && downloadsDir.canWrite()) {
            downloadsDir
        } else {
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        }

        val reportFile = File(targetDir, fileName)
        FileWriter(reportFile).use { writer ->
            writer.write(md.toString())
        }

        return reportFile
    }

    fun createShareIntent(context: Context, file: File): Intent {
        val uri: Uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        return Intent(Intent.ACTION_SEND).apply {
            type = "text/markdown"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "GMP Samples - Airing of Grievances (${file.name})")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
