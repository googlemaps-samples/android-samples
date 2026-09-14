#!/usr/bin/env python3
# Copyright 2026 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""
Automated Multimodal QA Evaluation Engine using Gemini.

Evaluates Google Maps Android sample runs by comparing current run artifacts
(stills, multi-state substeps, videos) against declared sample contracts
(purpose, success criteria, failure indicators) and known-good golden baselines.
Automatically flags defects and suspicious deviations for human review.
"""

import argparse
import base64
import json
import os
import re
import sys
import time
from pathlib import Path
from typing import Any, Dict, List, Optional
import requests

FALLBACK_MODELS = [
    "gemini-flash-latest",
    "gemini-3-flash-preview",
    "gemini-2.5-flash-lite",
    "gemini-pro-latest",
]


def get_api_key(root_dir: Path) -> Optional[str]:
    """Retrieves GEMINI_API_KEY from environment or secrets.properties."""
    env_key = os.getenv("GEMINI_API_KEY")
    if env_key:
        return env_key

    secrets_file = root_dir / "secrets.properties"
    if secrets_file.exists():
        with open(secrets_file, "r", encoding="utf-8") as f:
            match = re.search(r"^GEMINI_API_KEY=(.+)$", f.read(), re.MULTILINE)
            if match:
                return match.group(1).strip()
    return None


class GeminiEvalEngine:
    """Evaluates sample run artifacts using Gemini multimodal LLM-as-a-judge."""

    def __init__(
        self,
        api_key: str,
        model_name: str = "gemini-flash-latest",
        root_dir: Optional[Path] = None,
    ):
        self.api_key = api_key
        self.model_name = model_name
        self.root_dir = root_dir or Path(__file__).resolve().parent.parent

    @staticmethod
    def encode_image(image_path: Path) -> Optional[str]:
        """Encodes an image file to base64 string."""
        if not image_path.exists():
            return None
        with open(image_path, "rb") as f:
            return base64.b64encode(f.read()).decode("utf-8")

    def _call_gemini_with_retry(self, parts: List[Dict[str, Any]]) -> Optional[Dict[str, Any]]:
        """Calls Gemini API with exponential backoff and model fallback."""
        models_to_try = [self.model_name] + [m for m in FALLBACK_MODELS if m != self.model_name]

        payload = {
            "contents": [{"parts": parts}],
            "generationConfig": {
                "response_mime_type": "application/json",
                "temperature": 0.2,
            },
        }

        for model in models_to_try:
            url = f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={self.api_key}"
            for attempt in range(3):
                try:
                    res = requests.post(url, json=payload, timeout=45)
                    if res.status_code == 200:
                        body = res.json()
                        candidates = body.get("candidates", [])
                        if candidates:
                            text_content = candidates[0].get("content", {}).get("parts", [{}])[0].get("text", "{}")
                            # Strip markdown blocks if present
                            cleaned = text_content.strip()
                            if cleaned.startswith("```json"):
                                cleaned = cleaned[7:]
                            if cleaned.endswith("```"):
                                cleaned = cleaned[:-3]
                            return json.loads(cleaned.strip())
                    elif res.status_code in (429, 503):
                        wait_time = 2 * (attempt + 1)
                        time.sleep(wait_time)
                        continue
                    else:
                        break  # Other error, try next model
                except Exception:
                    time.sleep(1.5)
                    continue

        return None

    def evaluate_sample(
        self,
        sample: Dict[str, Any],
        run_dir: Path,
        baseline_dir: Optional[Path] = None,
    ) -> Dict[str, Any]:
        """
        Sends sample contract, current artifacts, and baseline artifacts
        to Gemini for multimodal evaluation.
        """
        title = sample.get("title", "Unknown Sample")
        purpose = sample.get("purpose", "")
        success_criteria = sample.get("successCriteria", "")
        failure_indicators = sample.get("failureIndicators", "")
        api_calls = sample.get("apiCalls", [])

        # Collect current images
        kotlin_img_path = run_dir / sample.get("kotlin_screenshot", "")
        java_img_path = run_dir / sample.get("java_screenshot", "")

        parts: List[Dict[str, Any]] = []

        prompt = f"""You are an expert QA visual verification engineer for the Google Maps Android SDK.
Evaluate this sample test run against its specification and golden baseline.

### Sample Specification
- **Title**: {title}
- **Category**: {sample.get('category', '')}
- **Purpose**: {purpose}
- **Success Criteria**: {success_criteria}
- **Failure Indicators**: {failure_indicators}
- **Key API Calls**: {', '.join(api_calls) if api_calls else 'N/A'}

### Inspection Tasks
1. **Functional Criteria**: Does the current sample state satisfy the documented purpose and success criteria?
2. **Visual Health**: Are vector map tiles loaded cleanly without authorization failures (e.g. grey blank surfaces), clipped UI controls, overlapping labels, or unhandled crash dialogs?
3. **Cross-Framework Parity**: Do the Kotlin and Java implementations show visual and structural parity?
4. **Baseline Comparison**: If a golden baseline image is provided, identify if differences represent an intentional enhancement or an unintended regression.

Return ONLY a valid JSON object matching this schema:
{{
  "verdict": "PASS" or "FLAG_FOR_HUMAN",
  "confidence": 0.0 to 1.0,
  "criteria_met": true or false,
  "parity_verified": true or false,
  "baseline_match": true or false,
  "defects": ["list of defects if any, else empty list"],
  "baseline_diff_notes": "description of difference vs baseline, or 'Matches baseline'",
  "reasoning": "concise explanation of visual findings",
  "suggested_human_action": "what a human reviewer should inspect if flagged, else null"
}}
"""
        parts.append({"text": prompt})

        # Add baseline image if available (only when baseline is a distinct run)
        if baseline_dir and baseline_dir.exists() and baseline_dir.resolve() != run_dir.resolve():
            baseline_k_path = baseline_dir / sample.get("kotlin_screenshot", "")
            if baseline_k_path.exists():
                b64_base = self.encode_image(baseline_k_path)
                if b64_base:
                    parts.append({"text": "--- [Image: Golden Baseline (Known Good)] ---"})
                    parts.append({"inline_data": {"mime_type": "image/png", "data": b64_base}})
        elif baseline_dir and baseline_dir.resolve() == run_dir.resolve():
            parts.append({"text": "--- Note: This run is established as the Golden Baseline reference. Check against specification and visual health."})

        # Add current Kotlin screenshot
        if kotlin_img_path.exists():
            b64_k = self.encode_image(kotlin_img_path)
            if b64_k:
                parts.append({"text": "--- [Image: Current Kotlin Implementation] ---"})
                parts.append({"inline_data": {"mime_type": "image/png", "data": b64_k}})

        # Add current Java screenshot
        if java_img_path.exists():
            b64_j = self.encode_image(java_img_path)
            if b64_j:
                parts.append({"text": "--- [Image: Current Java Implementation] ---"})
                parts.append({"inline_data": {"mime_type": "image/png", "data": b64_j}})

        # Add substep screenshots if available (up to 2 key interaction states)
        substeps = sample.get("substep_screenshots", [])
        for sub in substeps[:2]:
            sub_k = sub.get("kotlin")
            if sub_k:
                sub_k_path = run_dir / sub_k
                if sub_k_path.exists():
                    b64_sub = self.encode_image(sub_k_path)
                    if b64_sub:
                        parts.append({"text": f"--- [Interaction Substep: {sub.get('label', 'Step')}] ---"})
                        parts.append({"inline_data": {"mime_type": "image/png", "data": b64_sub}})

        result = self._call_gemini_with_retry(parts)
        if not result:
            return {
                "verdict": "FLAG_FOR_HUMAN",
                "confidence": 0.0,
                "criteria_met": False,
                "parity_verified": False,
                "baseline_match": False,
                "defects": ["Automated evaluation timed out or experienced transient service errors."],
                "baseline_diff_notes": "Service Unavailable",
                "reasoning": "Gemini API unavailable during evaluation pass.",
                "suggested_human_action": "Perform manual review in dashboard.",
            }
        return result

    def evaluate_run(
        self,
        run_dir: Path,
        baseline_dir: Optional[Path] = None,
        sample_filter: Optional[str] = None,
    ) -> Dict[str, Any]:
        """Runs Gemini evaluation across all samples in a run directory."""
        summary_file = run_dir / "run_summary.json"
        if not summary_file.exists():
            raise FileNotFoundError(f"Missing run_summary.json in {run_dir}")

        with open(summary_file, "r", encoding="utf-8") as f:
            run_data = json.load(f)

        results = run_data.get("results", [])
        print(f"\n===========================================================================")
        print(f"🤖 Starting Gemini AI Evaluation for Run: {run_dir.name}")
        if baseline_dir:
            print(f"⭐ Comparing against Golden Baseline: {baseline_dir.name}")
        print(f"===========================================================================\n")

        evaluated_count = 0
        flagged_count = 0

        for i, sample in enumerate(results, 1):
            short_title = sample.get("title", f"Sample {i}")
            sample_id = sample.get("id", "")
            if sample_filter and (sample_filter.lower() not in short_title.lower() and sample_filter.lower() not in sample_id.lower()):
                continue

            print(f"[{i:2d}/{len(results)}] Evaluating '{short_title}' with Gemini...", end="", flush=True)
            ai_eval = self.evaluate_sample(sample, run_dir, baseline_dir)
            sample["ai_evaluation"] = ai_eval

            verdict = ai_eval.get("verdict", "FLAG_FOR_HUMAN")
            confidence = ai_eval.get("confidence", 0.0)
            defects = ai_eval.get("defects", [])
            reasoning = ai_eval.get("reasoning", "")

            existing_notes = sample.get("operator_notes", "")
            has_manual_notes = bool(existing_notes and not existing_notes.startswith("🤖 **Gemini AI Flagged"))

            if verdict == "FLAG_FOR_HUMAN" or defects or confidence < 0.65:
                sample["status"] = "NEEDS_WORK"
                flagged_count += 1
                flag_note = f"🤖 **Gemini AI Flagged for Review** (Confidence: {int(confidence*100)}%):\n- {reasoning}"
                if defects:
                    flag_note += f"\n- **Defects Detected**: {', '.join(defects)}"
                if has_manual_notes:
                    sample["operator_notes"] = f"{existing_notes}\n\n{flag_note}"
                else:
                    sample["operator_notes"] = flag_note
                print(f" 🚩 FLAGGED (Confidence: {int(confidence*100)}%)")
                if defects:
                    print(f"     Defects: {defects}")
            else:
                if not has_manual_notes:
                    sample["status"] = "PASSING"
                    sample["operator_notes"] = ""
                print(f" 🟢 PASS (Confidence: {int(confidence*100)}%)")

            evaluated_count += 1
            time.sleep(0.4)

        # Update run stats across all results in the run
        passing_total = sum(1 for r in results if r.get("status") == "PASSING")
        needs_work_total = sum(1 for r in results if r.get("status") == "NEEDS_WORK")
        run_data["total_samples"] = len(results)
        run_data["passing"] = passing_total
        run_data["needs_work"] = needs_work_total
        run_data["pass_rate_pct"] = (
            round((passing_total / len(results)) * 100, 1)
            if results
            else 0.0
        )
        run_data["ai_evaluated"] = True
        run_data["ai_model"] = self.model_name
        run_data["baseline_run"] = baseline_dir.name if baseline_dir else None

        # Write updated run_summary.json
        with open(summary_file, "w", encoding="utf-8") as f:
            json.dump(run_data, f, indent=2)

        print(f"\n===========================================================================")
        print(f"Gemini Evaluation Complete!")
        print(f"Evaluated: {evaluated_count} | 🟢 Passing: {run_data['passing']} | 🚩 Flagged for Human: {flagged_count}")
        print(f"Pass Rate: {run_data['pass_rate_pct']}%")
        print(f"Updated: {summary_file}")
        print(f"===========================================================================\n")

        # Regenerate report
        report_generator = self.root_dir / "scripts" / "generate_report.py"
        if not report_generator.exists():
            report_generator = self.root_dir / "scripts" / "generate_html_report.py"
        if report_generator.exists():
            os.system(f"python3 {report_generator} -r {run_dir}")

        return run_data


def set_golden_baseline(run_dir: Path, root_dir: Path):
    """Sets a run as the permanent golden baseline."""
    golden_dir = root_dir / "eval_runs" / "golden"
    if golden_dir.is_symlink() or golden_dir.exists():
        if golden_dir.is_symlink():
            golden_dir.unlink()
        elif golden_dir.is_dir():
            import shutil
            shutil.rmtree(golden_dir)

    target_rel = os.path.relpath(run_dir, root_dir / "eval_runs")
    os.symlink(target_rel, golden_dir)
    print(f"⭐ Successfully designated '{run_dir.name}' as the Golden Baseline at {golden_dir}")


def main():
    parser = argparse.ArgumentParser(description="Automated Multimodal QA Evaluation Engine using Gemini")
    parser.add_argument("-r", "--run", help="Target run directory or ID (defaults to eval_runs/latest)")
    parser.add_argument("-b", "--baseline", help="Baseline run directory or ID to compare against (defaults to eval_runs/golden)")
    parser.add_argument("-s", "--sample", help="Target specific sample title or class name")
    parser.add_argument("--model", default="gemini-flash-latest", help="Gemini model name")
    parser.add_argument("--set-golden", help="Set the specified run ID or directory as the permanent golden baseline")

    args = parser.parse_args()
    root_dir = Path(__file__).resolve().parent.parent

    if args.set_golden:
        target_dir = (root_dir / "eval_runs" / args.set_golden) if not Path(args.set_golden).exists() else Path(args.set_golden)
        set_golden_baseline(target_dir.resolve(), root_dir)
        return

    api_key = get_api_key(root_dir)
    if not api_key:
        print("Error: GEMINI_API_KEY not found in environment or secrets.properties.")
        sys.exit(1)

    # Resolve target run
    if args.run:
        run_dir = Path(args.run).resolve() if Path(args.run).exists() else (root_dir / "eval_runs" / args.run).resolve()
    else:
        run_dir = (root_dir / "eval_runs" / "latest").resolve()

    if not run_dir.exists():
        print(f"Error: Target run directory {run_dir} does not exist.")
        sys.exit(1)

    # Resolve baseline run
    baseline_dir = None
    if args.baseline:
        baseline_dir = Path(args.baseline).resolve() if Path(args.baseline).exists() else (root_dir / "eval_runs" / args.baseline).resolve()
    else:
        golden_path = (root_dir / "eval_runs" / "golden").resolve()
        if golden_path.exists():
            baseline_dir = golden_path

    engine = GeminiEvalEngine(api_key=api_key, model_name=args.model, root_dir=root_dir)
    engine.evaluate_run(run_dir, baseline_dir=baseline_dir, sample_filter=args.sample)


if __name__ == "__main__":
    main()
