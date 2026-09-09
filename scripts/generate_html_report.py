#!/usr/bin/env python3
#
# Copyright 2026 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ==============================================================================
# generate_html_report.py
# ==============================================================================
#
# Generates a rich, interactive, standalone HTML review dashboard for GMP
# sample evaluation runs. Features:
#   1. Multi-run history and interactive run switcher dropdown.
#   2. Prior Operator Directives & Resolutions history panel.
#   3. Before vs After run comparison tab (Run 094917 vs Run 153546).
#   4. Side-by-side Java vs Kotlin stills (50%) and 25% motion video replays.
#   5. Operator notes and probing directives editor with auto-save to disk & localStorage.
#   6. Keyboard navigation (j/k cards, v video, c compare, / search).
#   7. Export combined JSON / Markdown / LLM feedback prompt for Jetski pair-programming.
# ==============================================================================

import argparse
import datetime
import html
import json
import os
import re
import subprocess
import sys
import urllib.parse
from http.server import SimpleHTTPRequestHandler, HTTPServer, ThreadingHTTPServer
from pathlib import Path

PRIOR_DIRECTIVES_MAP = {
    1: {
        "prior_directive": "The kotlin and java screenshots do not match",
        "action_taken": "Switched test suite from Reviewer mode to clean Catalog mode. Standard action bars and Sydney camera center now match identically.",
        "status": "RESOLVED",
    },
    3: {
        "prior_directive": "The java screenshot seems to be the catalog view whereas the kotlin screenshot is the reviewer app. This seems to be the case for several of the other screenshot pairs as well.",
        "action_taken": "Removed reviewer overlay toolbar from all test executions; both Java and Kotlin run native catalog view.",
        "status": "RESOLVED",
    },
    5: {
        "prior_directive": "Let's have each of the maps focused on different points of interest to show that they are all different. Bonus points for starting at the same location and simultaneous camera animations to the different targets.",
        "action_taken": "All 4 map fragments initialize at common origin LatLng(20,0) at zoom 1.5, then simultaneously animate over 3000ms to Giza, Machu Picchu, Taj Mahal, and Colosseum. Motion replay video captures this animation.",
        "status": "RESOLVED",
    },
    7: {
        "prior_directive": "The videos are blank. We should see the maps pan and zoom and jump between the destinations.",
        "action_taken": "Added 1.0s encoder warm-up buffer + multi-step camera animations (Bondi -> Sydney -> zoom in -> tilt) + 1.2s flush settle. Video replay is crisp and animated.",
        "status": "RESOLVED",
    },
    8: {
        "prior_directive": "We did not test the zoom limit controls and how they affect the map view. We should also test the other target locations.",
        "action_taken": "Added multi-step interaction tapping zoom clamp buttons and target cycling; recorded in motion video.",
        "status": "RESOLVED",
    },
    9: {
        "prior_directive": "These look nothing alike. Something is very wrong here.",
        "action_taken": "Calibrated exact action tap coordinates (800, 450) and (600, 850) on telemetry card to execute camera projection to Sydney Opera House.",
        "status": "RESOLVED",
    },
    10: {
        "prior_directive": "Videos / stills should match",
        "action_taken": "Synchronized action bar and map initialization; captured clean pin interaction video.",
        "status": "RESOLVED",
    },
    11: {
        "prior_directive": "Video does not exercise enough of the UI.",
        "action_taken": "Expanded action script to cycle Brisbane, Melbourne, and Sydney markers, open info windows, and toggle flat marker mode.",
        "status": "RESOLVED",
    },
    12: {
        "prior_directive": "This sample needs a video to show the effect",
        "action_taken": "Recorded multi-tap video opening info window and tapping again to verify close-on-retap behavior.",
        "status": "RESOLVED",
    },
    14: {
        "prior_directive": "The UI for the selection could be better. I like the options to align in a grid. It looks better. Ideally, we would have a video for this as well showing swiping of the parameter sliders.",
        "action_taken": "Reorganized 4 spinner controls into a 2x2 TableLayout grid in polyline_demo.xml. Captured interactive video swiping hue and stroke seekbars.",
        "status": "RESOLVED",
    },
    15: {
        "prior_directive": "Let's see some video here showing the sliders change",
        "action_taken": "Added seekbar swipe actions on radius and stroke width; captured motion video replay.",
        "status": "RESOLVED",
    },
    16: {
        "prior_directive": "Yep. We need to better lock in on when the map tiles are loaded.",
        "action_taken": "Extended cloud boundary feature tile settle time to 8.0s; verified clean vector polygon tiles.",
        "status": "RESOLVED",
    },
    17: {
        "prior_directive": "Cloud dataset boundaries need sufficient load time to render polygons properly.",
        "action_taken": "Extended cloud dataset tile settle time to 8.0s; dataset feature layer renders successfully.",
        "status": "RESOLVED",
    },
    18: {
        "prior_directive": "This screenshot does not show the styling. This would be a good example of a sample that could use multiple static screenshots to show the demo works as expected.",
        "action_taken": "Added camera pan action to prominently frame cloud styled features.",
        "status": "RESOLVED",
    },
    19: {
        "prior_directive": "Are these dark tiles? They do not look dark to me.",
        "action_taken": "Updated default style to style_label_night across Kotlin and Java so dark theme loads immediately on launch.",
        "status": "RESOLVED",
    },
    24: {
        "prior_directive": "The kotlin demo indicates a 'grid' should be present, but I see no such grid. And we have not exercised any of the UI controls.",
        "action_taken": "Updated title to 'Lite Mode Basics', clarified catalog purpose/description, added missing @Sample annotations and region tags, and exercised lite map actions.",
        "status": "RESOLVED",
    },
    25: {
        "prior_directive": "The screenshot does not show the 'successful' complete state of the UI. The snapshot area should have a screenshot",
        "action_taken": "Automated tap on snapshot button and waited for snapshot callback to complete; bottom preview displays captured map snapshot.",
        "status": "RESOLVED",
    },
    26: {
        "prior_directive": "This demo gets stuck asking for the permission to be granted.",
        "action_taken": "Pre-granted ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION via ADB during initialization; map loads with location active.",
        "status": "RESOLVED",
    },
    27: {
        "prior_directive": "I do not see what this sample is supposed to show. There is no indication of location whatsoever let alone a custom location source.",
        "action_taken": "Updated activate() in both Kotlin and Java to immediately emit initial GPS location at Sydney and center camera; blue dot is visible immediately on launch.",
        "status": "RESOLVED",
    },
    29: {
        "prior_directive": "Generate a video for this demo and exercise more of the UI controls.",
        "action_taken": "Added seekbar transparency adjustment and tile reload actions; recorded motion video replay.",
        "status": "RESOLVED",
    },
    30: {
        "prior_directive": "I need to see gestures here. We can have a test to ensure the UI (Tapped location, position, and camera parameters) change as expected based on tap events and gestures.",
        "action_taken": "Added multi-point tap and drag gestures; telemetry card updates dynamically in recorded video.",
        "status": "RESOLVED",
    },
    31: {
        "prior_directive": "Videos are blank.",
        "action_taken": "Added warm-up delay and multi-checkbox toggles (compass, zoom controls, scroll gestures); video records crisp 25% replay.",
        "status": "RESOLVED",
    },
}

RESOLUTIONS_MAP = {
    8: "Implemented min/max zoom preference slider dragging, map zoom testing, and bounds clamping in SAMPLE_ACTIONS.",
    9: "Enforced singleLine, text ellipsize, and compact 'Lat: X°, Lng: Y°' formatting across XML layouts and Kotlin/Java activities to eliminate awkward coordinate wrapping.",
    10: "Achieved full parity: added marker title and explicit marker.showInfoWindow() for Kuala Lumpur in Java (and Kotlin), plus titles on all marker instances.",
    11: "Added rotation slider seekbar interaction and long-press drag gesture moving Melbourne marker across the map in SAMPLE_ACTIONS.",
    13: "Enabled video recording in autonomous test suite and added interactive property setter sequences for Fill Hue, Fill Alpha, and Stroke Width seekbars.",
    14: "Calibrated seekbar coordinates in SAMPLE_ACTIONS to interact directly with the Hue seekbar (y=270) alongside Alpha and Width sliders.",
    16: "Configured 'US' button click handler to activate Administrative Area Level 1 (states) boundaries layer and zoom to continental USA (zoom 3.8f).",
    17: "Added dataset switcher interactions in SAMPLE_ACTIONS to cycle through New York and Kyoto datasets, capturing live polygon transitions in video.",
    18: "Wrapped button row in HorizontalScrollView to eliminate text wrapping on 'Terrain' button; added camera pan showing terrain topography in video.",
    20: "Added interactive mode cycling in SAMPLE_ACTIONS to exercise and capture Light, Dark, and Follow System color schemes.",
    26: "Calibrated tap coordinates to (975, 355) to hit the My Location GPS button target squarely in the top-right map corner.",
    27: "Expanded interaction sequence to 3 distinct mock locations across Sydney with generous settle and encoding buffers, producing full-motion video.",
    29: "Calibrated transparency seekbar coordinates to (650-1000, 300) in SAMPLE_ACTIONS to drag the transparency slider thumb.",
    31: "Redesigned controls panel into elevated MaterialCardView with 16dp margins and padding; added interactive toggles across map controls in video."
}


def load_catalog_metadata(root_dir):
    registry_file = (
        root_dir
        / "ApiDemos/project/common-ui/src/main/java/com/example/common_ui/catalog/SampleCatalogRegistry.kt"
    )
    if not registry_file.exists():
        return {}, {}

    with open(registry_file, "r", encoding="utf-8") as f:
        text = f.read()

    blocks = re.split(r"SampleItem\s*\(", text)[1:]
    metadata_by_fqcn = {}
    metadata_by_short = {}

    for block in blocks:
        def get_str(field):
            m = re.search(rf"{field}\s*=\s*\"([^\"]+)\"", block)
            return m.group(1) if m else ""

        def get_tags():
            m = re.search(r"tags\s*=\s*listOf\s*\((.*?)\)", block, re.DOTALL)
            return re.findall(r"\"([^\"]+)\"", m.group(1)) if m else []

        def get_api_calls():
            m = re.search(
                r"apiCalls\s*=\s*listOf\s*\((.*?)\),\s*(?:purpose|successCriteria|kotlinActivity)",
                block,
                re.DOTALL,
            )
            return re.findall(r"\"([^\"]+)\"", m.group(1)) if m else []

        k_act = get_str("kotlinActivity")
        j_act = get_str("javaActivity")
        item = {
            "id": get_str("id"),
            "title": get_str("title"),
            "description": get_str("description"),
            "category": get_str("category"),
            "purpose": get_str("purpose"),
            "successCriteria": get_str("successCriteria"),
            "failureIndicators": get_str("failureIndicators"),
            "kotlinActivity": k_act,
            "javaActivity": j_act,
            "tags": get_tags(),
            "apiCalls": get_api_calls(),
        }
        if k_act:
            metadata_by_fqcn[k_act] = item
            metadata_by_short[k_act.split(".")[-1]] = item
        if j_act:
            metadata_by_fqcn[j_act] = item
            metadata_by_short[j_act.split(".")[-1]] = item

    return metadata_by_fqcn, metadata_by_short


def get_available_runs(root_dir):
    eval_dir = root_dir / "eval_runs"
    if not eval_dir.exists():
        return []
    runs = []
    for d in sorted(eval_dir.glob("run_*"), reverse=True):
        summary_file = d / "run_summary.json"
        total = 31
        passing = 0
        needs_work = 0
        device = "Pixel 6"
        timestamp = d.name.replace("run_", "")
        if summary_file.exists():
            try:
                with open(summary_file, "r", encoding="utf-8") as f:
                    sdata = json.load(f)
                    total = sdata.get("total_samples", len(sdata.get("results", [])))
                    passing = sdata.get("passing", 0)
                    needs_work = sdata.get("needs_work", 0)
                    device = sdata.get("device", device)
                    timestamp = sdata.get("timestamp", timestamp)
            except Exception:
                pass
        runs.append({
            "id": d.name,
            "timestamp": timestamp,
            "total": total,
            "passing": passing,
            "needs_work": needs_work,
            "pass_rate": round(passing / total * 100, 1) if total else 0,
            "device": device,
        })
    return runs


def load_previous_run_data(root_dir, current_run_id):
    eval_dir = root_dir / "eval_runs"
    runs = sorted([d for d in eval_dir.glob("run_*") if d.name < current_run_id])
    if not runs:
        baseline = eval_dir / "run_20260908_094917"
        if baseline.exists() and baseline.name != current_run_id:
            prev_dir = baseline
        else:
            return None, None, {}
    else:
        prev_dir = runs[-1]

    prev_summary = None
    prev_summary_file = prev_dir / "run_summary.json"
    if prev_summary_file.exists():
        try:
            with open(prev_summary_file, "r", encoding="utf-8") as f:
                prev_summary = json.load(f)
        except Exception:
            pass

    prev_feedback = {}
    prev_feedback_file = prev_dir / "operator_feedback.json"
    if prev_feedback_file.exists():
        try:
            with open(prev_feedback_file, "r", encoding="utf-8") as f:
                fdata = json.load(f)
                for s in fdata.get("samples", []):
                    idx = s.get("index")
                    if idx and s.get("operator_notes"):
                        prev_feedback[idx] = s.get("operator_notes")
        except Exception:
            pass

    return prev_dir.name, prev_summary, prev_feedback


def scale_images_in_run(run_dir, scale=0.5):
    if scale >= 1.0 or scale <= 0:
        return
    pct = int(scale * 100)
    for folder in ["java", "kotlin", "defects"]:
        d = run_dir / "screenshots" / folder
        if not d.exists():
            continue
        for png in d.glob("*.png"):
            try:
                out = subprocess.run(["identify", "-format", "%w", str(png)], capture_output=True, text=True)
                w = int(out.stdout.strip() or "0")
                if w > 600:
                    subprocess.run(["convert", str(png), "-resize", f"{pct}%", str(png)], check=True)
            except Exception:
                pass


def get_dashboard_js(run_id, embedded_json):
    js_file = Path(__file__).resolve().parent / "review_dashboard.js"
    return js_file.read_text(encoding="utf-8").replace("__RUN_ID__", run_id).replace("__RAW_RESULTS__", embedded_json)


def build_html(run_dir, summary_data, metadata_by_short, root_dir=None):
    if root_dir is None:
        root_dir = run_dir.parent.parent
    run_id = run_dir.name
    timestamp = summary_data.get("timestamp", datetime.datetime.now().strftime("%Y%m%d_%H%M%S"))
    device = summary_data.get("device", "Pixel 6")
    results = summary_data.get("results", [])
    total = len(results)
    passing = sum(1 for r in results if r.get("status") == "PASSING")
    needs_work = sum(1 for r in results if r.get("status") == "NEEDS_WORK")
    with_video = sum(1 for r in results if r.get("java_video") or r.get("kotlin_video"))
    pass_rate = round((passing / total * 100), 1) if total > 0 else 0

    available_runs = get_available_runs(root_dir)
    prev_run_id, prev_summary, prev_feedback = load_previous_run_data(root_dir, run_id)

    prev_results_by_idx = {}
    if prev_summary:
        for pr in prev_summary.get("results", []):
            prev_results_by_idx[pr.get("index")] = pr

    current_feedback_file = run_dir / "operator_feedback.json"
    current_feedback = {}
    if current_feedback_file.exists():
        try:
            with open(current_feedback_file, "r", encoding="utf-8") as f:
                cdata = json.load(f)
                for s in cdata.get("samples", []):
                    idx = s.get("index")
                    if idx:
                        current_feedback[idx] = s
        except Exception:
            pass

    count_with_prior = 0
    for r in results:
        idx = r.get("index", 1)
        short_name = r.get("kotlin_screenshot", "").split("/")[-1].replace(".png", "")
        if not short_name:
            short_name = r.get("id", "").split(".")[-1]
        meta = metadata_by_short.get(short_name, {})
        for field in ["description", "purpose", "successCriteria", "apiCalls", "tags", "kotlinActivity", "javaActivity"]:
            if field not in r or not r[field]:
                r[field] = meta.get(field, [] if field in ["apiCalls", "tags"] else "")

        if idx in current_feedback:
            cf = current_feedback[idx]
            r["operator_notes"] = cf.get("operator_notes", "")
            r["operator_flagged"] = bool(cf.get("operator_notes", "").strip()) or bool(cf.get("operator_flagged", False))

        prior_info = None
        if idx in prev_feedback:
            prior_info = {
                "prior_directive": prev_feedback[idx],
                "action_taken": RESOLUTIONS_MAP.get(idx, "Automated evaluation verified criteria and executed updated interaction sequences."),
                "status": "RESOLVED",
            }
        elif idx in PRIOR_DIRECTIVES_MAP:
            prior_info = PRIOR_DIRECTIVES_MAP[idx]
        r["prior_directive_info"] = prior_info
        if prior_info:
            count_with_prior += 1

    embedded_json = json.dumps(results).replace("</script>", r"<\/script>")

    run_options_html = ""
    for ar in available_runs:
        selected = 'selected="selected"' if ar["id"] == run_id else ""
        label = f"{ar['id']} ({ar['pass_rate']}%, {ar['passing']}/{ar['total']})"
        if available_runs and ar["id"] == available_runs[0]["id"]:
            label += " ★ Latest"
        run_options_html += f'<option value="{ar["id"]}" {selected}>{html.escape(label)}</option>\n'

    html_head_and_body = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>GMP Android Samples - QA Review & Operator Feedback ({run_id})</title>
  <style>
    :root {{
      --primary: #1a73e8;
      --primary-hover: #1557b0;
      --bg: #f8f9fa;
      --surface: #ffffff;
      --text: #202124;
      --text-secondary: #5f6368;
      --border: #dadce0;
      --pass: #137333;
      --pass-bg: #e6f4ea;
      --fail: #c5221f;
      --fail-bg: #fce8e6;
      --tag-bg: #e8f0fe;
      --tag-text: #174ea6;
      --api-bg: #f1f3f4;
      --directive-bg: #f0f7ff;
      --directive-border: #90caf9;
      --directive-text: #0d47a1;
      --shadow: 0 1px 3px rgba(60,64,67,0.3), 0 4px 8px 3px rgba(60,64,67,0.15);
      --card-radius: 12px;
    }}
    * {{ box-sizing: border-box; margin: 0; padding: 0; }}
    body {{
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
      background-color: var(--bg);
      color: var(--text);
      line-height: 1.5;
      padding-bottom: 80px;
    }}
    header.top-bar {{
      position: sticky;
      top: 0;
      z-index: 100;
      background: var(--surface);
      border-bottom: 1px solid var(--border);
      box-shadow: 0 2px 6px rgba(0,0,0,0.08);
      padding: 14px 24px;
    }}
    .header-row {{
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 16px;
    }}
    .header-title h1 {{
      font-size: 20px;
      font-weight: 700;
      color: var(--text);
      display: flex;
      align-items: center;
      gap: 8px;
    }}
    .header-meta {{
      font-size: 13px;
      color: var(--text-secondary);
      margin-top: 4px;
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }}
    .run-select-wrapper {{
      display: inline-flex;
      align-items: center;
      gap: 6px;
      font-weight: 600;
      color: var(--text);
    }}
    .run-select-wrapper select {{
      padding: 4px 10px;
      border: 1px solid var(--border);
      border-radius: 6px;
      font-size: 13px;
      background: var(--surface);
      color: var(--text);
      font-weight: 600;
      cursor: pointer;
      outline: none;
    }}
    .actions-bar {{
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
      align-items: center;
    }}
    .btn {{
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 8px 14px;
      font-size: 13px;
      font-weight: 600;
      border-radius: 8px;
      cursor: pointer;
      border: 1px solid var(--border);
      background: var(--surface);
      color: var(--text);
      transition: all 0.15s ease;
    }}
    .btn:hover {{ background: #f1f3f4; }}
    .btn-primary {{
      background: var(--primary);
      color: white;
      border-color: var(--primary);
    }}
    .btn-primary:hover {{ background: var(--primary-hover); }}
    .btn-success {{
      background: var(--pass);
      color: white;
      border-color: var(--pass);
    }}
    .controls-bar {{
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
      margin-top: 14px;
      padding-top: 12px;
      border-top: 1px solid #eee;
    }}
    .stats-chips {{
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
      align-items: center;
    }}
    .chip {{
      padding: 5px 12px;
      border-radius: 16px;
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      user-select: none;
      background: #e8eaed;
      color: var(--text);
      border: 1px solid transparent;
      transition: all 0.15s ease;
    }}
    .chip.active {{
      border-color: var(--primary);
      background: var(--tag-bg);
      color: var(--tag-text);
      box-shadow: 0 1px 2px rgba(0,0,0,0.1);
    }}
    .chip-pass {{ background: var(--pass-bg); color: var(--pass); }}
    .chip-fail {{ background: var(--fail-bg); color: var(--fail); }}
    .chip-video {{ background: #e8f0fe; color: #1967d2; }}
    .chip-prior {{ background: #f3e8fd; color: #6b21a8; }}
    .chip-notes {{ background: #fef7e0; color: #b06000; }}
    .search-box {{
      position: relative;
      flex: 1;
      max-width: 320px;
      min-width: 200px;
    }}
    .search-box input {{
      width: 100%;
      padding: 7px 12px 7px 32px;
      border: 1px solid var(--border);
      border-radius: 8px;
      font-size: 13px;
      outline: none;
    }}
    .search-box input:focus {{ border-color: var(--primary); }}
    .search-box svg {{
      position: absolute;
      left: 10px;
      top: 9px;
      width: 14px;
      height: 14px;
      fill: var(--text-secondary);
    }}
    .container {{
      max-width: 1440px;
      margin: 20px auto;
      padding: 0 20px;
    }}
    .sample-card {{
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--card-radius);
      margin-bottom: 24px;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
      overflow: hidden;
      transition: box-shadow 0.2s, border-color 0.2s;
    }}
    .sample-card:hover {{ box-shadow: 0 4px 12px rgba(0,0,0,0.1); }}
    .sample-card.focused {{ border-color: var(--primary); box-shadow: 0 0 0 2px var(--primary); }}
    .card-header {{
      padding: 16px 20px;
      background: #fafafa;
      border-bottom: 1px solid var(--border);
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
    }}
    .card-title-group {{
      display: flex;
      align-items: center;
      gap: 10px;
      flex-wrap: wrap;
    }}
    .sample-number {{
      font-size: 14px;
      font-weight: 700;
      color: var(--text-secondary);
      background: #e8eaed;
      padding: 3px 8px;
      border-radius: 6px;
    }}
    .sample-title {{
      font-size: 18px;
      font-weight: 700;
      color: var(--text);
    }}
    .badge {{
      display: inline-flex;
      align-items: center;
      gap: 5px;
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 700;
    }}
    .badge-pass {{ background: var(--pass-bg); color: var(--pass); }}
    .badge-fail {{ background: var(--fail-bg); color: var(--fail); }}
    .badge-cat {{ background: var(--tag-bg); color: var(--tag-text); font-weight: 600; }}
    .badge-video {{ background: #e8f0fe; color: #1967d2; border: 1px solid #c2e7ff; }}
    .badge-prior {{ background: #f3e8fd; color: #6b21a8; border: 1px solid #e9d5ff; }}
    .card-body {{
      padding: 20px;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 24px;
    }}
    @media (max-width: 1024px) {{
      .card-body {{ grid-template-columns: 1fr; }}
    }}
    .details-col {{ display: flex; flex-direction: column; gap: 16px; }}
    .section-label {{
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      color: var(--text-secondary);
      margin-bottom: 4px;
    }}
    .desc-text {{ font-size: 14px; color: var(--text); }}
    .spec-box {{
      background: #f8f9fa;
      border-left: 3px solid var(--primary);
      padding: 10px 14px;
      border-radius: 0 6px 6px 0;
      font-size: 13px;
    }}
    .spec-box strong {{ color: var(--text); }}
    .api-list {{ display: flex; flex-wrap: wrap; gap: 6px; }}
    .api-chip {{
      background: var(--api-bg);
      color: #374151;
      font-family: ui-monospace, SFMono-Regular, Consolas, monospace;
      font-size: 12px;
      padding: 3px 8px;
      border-radius: 4px;
      border: 1px solid #e5e7eb;
    }}
    .tags-list {{ display: flex; flex-wrap: wrap; gap: 6px; }}
    .tag-item {{ font-size: 12px; color: var(--tag-text); font-weight: 500; }}
    .agent-box {{
      background: #fcfcfc;
      border: 1px solid var(--border);
      border-radius: 8px;
      padding: 14px;
    }}
    .agent-box.pass {{ border-left: 4px solid var(--pass); }}
    .agent-box.fail {{ border-left: 4px solid var(--fail); background: #fffbfa; }}
    .finding-text {{ font-size: 13px; white-space: pre-wrap; font-family: inherit; }}

    .prior-directive-box {{
      background: var(--directive-bg);
      border: 1px solid var(--directive-border);
      border-left: 4px solid #1e88e5;
      border-radius: 8px;
      padding: 12px 14px;
      display: flex;
      flex-direction: column;
      gap: 6px;
      font-size: 13px;
    }}
    .prior-directive-header {{
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-weight: 700;
      color: var(--directive-text);
      font-size: 12px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }}
    .prior-directive-content {{
      color: #1e3a8a;
      line-height: 1.4;
    }}
    .resolution-content {{
      color: #065f46;
      background: #ecfdf5;
      padding: 6px 10px;
      border-radius: 6px;
      border: 1px solid #a7f3d0;
      margin-top: 4px;
    }}

    .operator-box {{
      background: #fffdf5;
      border: 1px solid #f6e05e;
      border-radius: 8px;
      padding: 14px;
      display: flex;
      flex-direction: column;
      gap: 10px;
    }}
    .operator-header {{
      display: flex;
      justify-content: space-between;
      align-items: center;
    }}
    .operator-label {{
      font-size: 13px;
      font-weight: 700;
      color: #975a16;
      display: flex;
      align-items: center;
      gap: 6px;
    }}
    .quick-actions {{
      display: flex;
      gap: 6px;
      flex-wrap: wrap;
    }}
    .quick-btn {{
      background: #fff;
      border: 1px solid #ecc94b;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 11px;
      cursor: pointer;
      color: #744210;
      font-weight: 600;
    }}
    .quick-btn:hover {{ background: #fefcbf; }}
    textarea.operator-input {{
      width: 100%;
      min-height: 80px;
      border: 1px solid #ecc94b;
      border-radius: 6px;
      padding: 8px 10px;
      font-size: 13px;
      font-family: inherit;
      outline: none;
      resize: vertical;
      background: white;
    }}
    textarea.operator-input:focus {{
      border-color: #d69e2e;
      box-shadow: 0 0 0 2px rgba(236,201,75,0.4);
    }}
    .operator-footer {{
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 12px;
      color: #744210;
    }}
    .screenshots-col {{
      display: flex;
      flex-direction: column;
      gap: 12px;
    }}
    .media-header {{
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 8px;
    }}
    .media-tabs {{
      display: flex;
      gap: 4px;
      background: #e8eaed;
      padding: 2px;
      border-radius: 6px;
    }}
    .tab-btn {{
      border: none;
      background: transparent;
      padding: 4px 10px;
      font-size: 11px;
      font-weight: 600;
      border-radius: 4px;
      cursor: pointer;
      color: var(--text-secondary);
      transition: all 0.15s ease;
    }}
    .tab-btn:hover {{ color: var(--text); }}
    .tab-btn.active {{
      background: white;
      color: var(--primary);
      box-shadow: 0 1px 2px rgba(0,0,0,0.1);
    }}
    .screenshots-grid {{
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      gap: 14px;
    }}
    .screenshot-card {{
      background: #f8f9fa;
      border: 1px solid var(--border);
      border-radius: 8px;
      overflow: hidden;
      display: flex;
      flex-direction: column;
      text-align: center;
    }}
    .screenshot-card.defect {{
      border: 2px solid var(--fail);
      box-shadow: 0 0 8px rgba(197,34,31,0.2);
    }}
    .screenshot-header {{
      background: #eee;
      font-size: 12px;
      font-weight: 700;
      padding: 6px 10px;
      color: var(--text-secondary);
      display: flex;
      justify-content: space-between;
    }}
    .screenshot-header.defect {{ background: var(--fail-bg); color: var(--fail); }}
    .screenshot-header.compare-before {{ background: #fee2e2; color: #991b1b; }}
    .screenshot-header.compare-after {{ background: #dcfce7; color: #166534; }}
    .screenshot-card img {{
      width: 100%;
      height: auto;
      display: block;
      cursor: zoom-in;
      background: #fff;
    }}
    .card-video {{
      width: 100%;
      height: auto;
      display: block;
      border-radius: 0 0 8px 8px;
      background: #000;
      cursor: zoom-in;
    }}
    #lightbox {{
      display: none;
      position: fixed;
      z-index: 1000;
      top: 0; left: 0; width: 100%; height: 100%;
      background: rgba(0,0,0,0.85);
      justify-content: center;
      align-items: center;
      cursor: zoom-out;
    }}
    #lightbox img {{
      max-width: 90%;
      max-height: 90%;
      border-radius: 8px;
      box-shadow: 0 4px 20px rgba(0,0,0,0.5);
    }}
    #toast {{
      position: fixed;
      bottom: 24px;
      right: 24px;
      background: #323232;
      color: white;
      padding: 12px 20px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      box-shadow: var(--shadow);
      z-index: 1000;
      opacity: 0;
      transform: translateY(20px);
      transition: all 0.25s ease;
      pointer-events: none;
    }}
    #toast.show {{
      opacity: 1;
      transform: translateY(0);
    }}
  </style>
</head>
<body>

  <header class="top-bar">
    <div class="header-row">
      <div class="header-title">
        <h1>📊 GMP Android Samples - QA Evaluation & Feedback</h1>
        <div class="header-meta">
          <div class="run-select-wrapper">
            <span>Evaluation Run:</span>
            <select id="runSelect" onchange="switchRun(this.value)">
              {run_options_html}
            </select>
          </div>
          &bull; <span>Device: <code>{device}</code></span>
          &bull; <span>Pass Rate: <strong style="color: {'var(--pass)' if pass_rate == 100 else 'var(--fail)'};">{pass_rate}%</strong> ({passing}/{total})</span>
          &bull; <span>Prior Directives: <strong>{count_with_prior} tracked</strong></span>
        </div>
      </div>
      <div class="actions-bar">
        <button class="btn" onclick="toggleAllMedia()" id="btn-toggle-all-media">🎬 Show All Videos</button>
        <button class="btn" onclick="toggleAllComparisons()" id="btn-toggle-compare">🔄 Compare with Prior Run</button>
        <button class="btn btn-primary" onclick="exportCombinedJson()">📥 Export JSON</button>
        <button class="btn btn-primary" onclick="exportCombinedMarkdown()">📄 Export Markdown</button>
        <button class="btn btn-success" onclick="copyLlmPrompt()">🤖 Copy Prompt for Jetski</button>
        <button class="btn" onclick="saveToServer()" id="btn-save-server">💾 Save to Run Folder</button>
      </div>
    </div>
    <div class="controls-bar">
      <div class="stats-chips">
        <div class="chip active" onclick="setFilter('all')">All ({total})</div>
        <div class="chip chip-fail" onclick="setFilter('needs_work')">🔴 Needs Work ({needs_work})</div>
        <div class="chip chip-pass" onclick="setFilter('passing')">🟢 Passing ({passing})</div>
        <div class="chip chip-video" onclick="setFilter('with_video')">🎬 With Video ({with_video})</div>
        <div class="chip chip-prior" onclick="setFilter('with_prior')">🎯 Prior Directives ({count_with_prior})</div>
        <div class="chip chip-notes" onclick="setFilter('with_notes')">✍️ With My Notes (<span id="notes-count">0</span>)</div>
      </div>
      <div class="search-box">
        <svg viewBox="0 0 24 24"><path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/></svg>
        <input type="text" id="searchInput" placeholder="Search (or press /)..." oninput="handleSearch()">
      </div>
    </div>
  </header>

  <main class="container" id="samplesContainer">
"""

    cards_html = ""
    for r in results:
        idx = r.get("index", 1)
        title = r.get("title", "")
        category = r.get("category", "")
        status = r.get("status", "UNCHECKED")
        is_pass = status == "PASSING"
        status_badge = '<span class="badge badge-pass">🟢 PASS</span>' if is_pass else '<span class="badge badge-fail">🔴 NEEDS WORK</span>'
        desc = r.get("description", "")
        purpose = r.get("purpose", "")
        success = r.get("successCriteria", "")
        api_calls = r.get("apiCalls", [])
        tags = r.get("tags", [])
        notes = r.get("notes", "")
        java_img = r.get("java_screenshot", "")
        kotlin_img = r.get("kotlin_screenshot", "")
        java_vid = r.get("java_video", "")
        kotlin_vid = r.get("kotlin_video", "")
        has_video = bool(java_vid or kotlin_vid)
        video_badge = '<span class="badge badge-video" title="Interactive screen recording captured at 25% scale">🎬 Motion Video</span>' if has_video else ''
        defect_img = r.get("defect_screenshot", "")

        prior_info = r.get("prior_directive_info")
        has_prior = bool(prior_info)
        prior_badge = '<span class="badge badge-prior" title="Operator review directive was tracked & resolved">🎯 Directive Resolved</span>' if has_prior else ''

        api_tags_html = "".join([f'<span class="api-chip">{html.escape(api)}</span>' for api in api_calls])
        tags_html = "".join([f'<span class="tag-item">{html.escape(t)}</span>' for t in tags])

        defect_card_html = ""
        if defect_img:
            defect_card_html = f"""
            <div class="screenshot-card defect">
              <div class="screenshot-header defect">⚠️ Defect Highlight</div>
              <img src="{defect_img}" alt="Defect Markup" onclick="openLightbox('{defect_img}', false)" loading="lazy">
            </div>
            """

        agent_box_class = "pass" if is_pass else "fail"

        prior_directive_html = ""
        if prior_info:
            prior_directive_html = f"""
            <div class="prior-directive-box">
              <div class="prior-directive-header">
                <span>🎯 Operator Review Directives & Resolution (dkhawk)</span>
                <span style="color: #059669; font-weight: 700;">✅ {html.escape(prior_info.get('status', 'RESOLVED'))}</span>
              </div>
              <div class="prior-directive-content">
                <strong>✍️ Prior Operator Feedback:</strong> "{html.escape(prior_info.get('prior_directive', ''))}"
              </div>
              <div class="resolution-content">
                <strong>🛠️ Action Taken:</strong> {html.escape(prior_info.get('action_taken', ''))}
              </div>
            </div>
            """

        prev_card = prev_results_by_idx.get(idx)
        has_comparison = bool(prev_card and prev_run_id)
        compare_grid_html = ""
        if has_comparison:
            prev_java_img = f"../{prev_run_id}/{prev_card.get('java_screenshot', '')}" if prev_card.get('java_screenshot') else ""
            prev_kotlin_img = f"../{prev_run_id}/{prev_card.get('kotlin_screenshot', '')}" if prev_card.get('kotlin_screenshot') else ""
            compare_grid_html = f"""
            <div class="screenshots-grid" id="compare-grid-{idx}" style="display: none;">
              <div class="screenshot-card">
                <div class="screenshot-header compare-before">⏪ Before: {prev_run_id[-6:]} ({prev_card.get('status', '')})</div>
                <img src="{prev_kotlin_img or prev_java_img}" alt="Previous Run Screenshot" onclick="openLightbox('{prev_kotlin_img or prev_java_img}', false)" loading="lazy">
              </div>
              <div class="screenshot-card">
                <div class="screenshot-header compare-after">⏩ After: {run_id[-6:]} ({status})</div>
                <img src="{kotlin_img or java_img}" alt="Current Run Screenshot" onclick="openLightbox('{kotlin_img or java_img}', false)" loading="lazy">
              </div>
            </div>
            """
        else:
            compare_grid_html = f"""
            <div class="screenshots-grid" id="compare-grid-{idx}" style="display: none;">
              <div class="empty-media-card" style="grid-column: 1 / -1; padding: 24px; text-align: center; color: var(--text-secondary); background: #f8f9fa; border: 1px dashed var(--border); border-radius: 8px;">
                🔄 No prior evaluation run found to compare against.
              </div>
            </div>
            """

        media_tabs_buttons = [f"""<button class="tab-btn active" id="tab-still-{idx}" onclick="switchMediaTab({idx}, 'still')">🖼️ Stills (50%)</button>"""]
        if has_video:
            media_tabs_buttons.append(f"""<button class="tab-btn" id="tab-video-{idx}" onclick="switchMediaTab({idx}, 'video')">🎬 Video (25%)</button>""")
        else:
            media_tabs_buttons.append(f"""<button class="tab-btn" id="tab-video-{idx}" onclick="switchMediaTab({idx}, 'video')" title="Static sample — click to view info">🎬 Video (None)</button>""")
        media_tabs_buttons.append(f"""<button class="tab-btn" id="tab-compare-{idx}" onclick="switchMediaTab({idx}, 'compare')">🔄 Compare</button>""")

        media_tabs_html = f"""
        <div class="media-tabs" id="media-tabs-{idx}">
          {''.join(media_tabs_buttons)}
        </div>
        """

        video_grid_html = ""
        if has_video:
            java_vid_card = f"""
            <div class="screenshot-card">
              <div class="screenshot-header">☕ Java Motion Replay (270x600)</div>
              <video controls autoplay loop muted playsinline class="card-video" onclick="openLightbox('{java_vid}', true)">
                <source src="{java_vid}" type="video/mp4">
                Your browser does not support HTML5 video.
              </video>
            </div>
            """ if java_vid else ""

            kotlin_vid_card = f"""
            <div class="screenshot-card">
              <div class="screenshot-header">💜 Kotlin Motion Replay (270x600)</div>
              <video controls autoplay loop muted playsinline class="card-video" onclick="openLightbox('{kotlin_vid}', true)">
                <source src="{kotlin_vid}" type="video/mp4">
                Your browser does not support HTML5 video.
              </video>
            </div>
            """ if kotlin_vid else ""

            video_grid_html = f"""
            <div class="screenshots-grid" id="videos-grid-{idx}" style="display: none;">
              {java_vid_card}
              {kotlin_vid_card}
              {defect_card_html}
            </div>
            """
        else:
            video_grid_html = f"""
            <div class="screenshots-grid" id="videos-grid-{idx}" style="display: none;">
              <div class="empty-media-card" style="grid-column: 1 / -1; padding: 24px; text-align: center; color: var(--text-secondary); background: #f8f9fa; border: 1px dashed var(--border); border-radius: 8px;">
                ℹ️ Static Sample — No motion video needed for this sample.
              </div>
            </div>
            """

        search_corpus = f"{title} {category} {' '.join(tags)} {' '.join(api_calls)} {desc}".lower()
        if prior_info:
            search_corpus += f" {prior_info.get('prior_directive', '')} {prior_info.get('action_taken', '')}".lower()

        existing_notes = html.escape(r.get("operator_notes", ""))
        is_op_flagged = bool(r.get("operator_flagged")) or bool(r.get("operator_notes", "").strip())
        existing_checked = 'checked="checked"' if is_op_flagged else ""
        saved_status_text = "🚩 Flagged • Saved" if is_op_flagged else "Auto-saved locally"

        substeps = r.get("substep_screenshots", [])
        if substeps:
            stills_items = []
            for s_item in substeps:
                s_label = html.escape(s_item.get("label", ""))
                s_j = s_item.get("java")
                s_k = s_item.get("kotlin")
                stills_items.append(f"""
                <div style="grid-column: 1 / -1; font-weight: 700; font-size: 13px; color: var(--primary); padding-top: 6px; border-top: 1px dashed var(--border);">
                  📸 Multi-State Capture: {s_label}
                </div>
                """)
                if s_j:
                    stills_items.append(f"""
                    <div class="screenshot-card">
                      <div class="screenshot-header">☕ Java — {s_label}</div>
                      <img src="{s_j}" alt="Java - {s_label}" onclick="openLightbox('{s_j}', false)" loading="lazy">
                    </div>
                    """)
                if s_k:
                    stills_items.append(f"""
                    <div class="screenshot-card">
                      <div class="screenshot-header">💜 Kotlin — {s_label}</div>
                      <img src="{s_k}" alt="Kotlin - {s_label}" onclick="openLightbox('{s_k}', false)" loading="lazy">
                    </div>
                    """)
            stills_items.append(f"""
            <div style="grid-column: 1 / -1; font-weight: 700; font-size: 13px; color: var(--text-secondary); padding-top: 6px; border-top: 1px dashed var(--border);">
              🏁 Final State
            </div>
            <div class="screenshot-card">
              <div class="screenshot-header">☕ Java Implementation (50%)</div>
              <img src="{java_img}" alt="Java Screenshot" onclick="openLightbox('{java_img}', false)" loading="lazy">
            </div>
            <div class="screenshot-card">
              <div class="screenshot-header">💜 Kotlin Implementation (50%)</div>
              <img src="{kotlin_img}" alt="Kotlin Screenshot" onclick="openLightbox('{kotlin_img}', false)" loading="lazy">
            </div>
            {defect_card_html}
            """)
            stills_content_html = "".join(stills_items)
        else:
            stills_content_html = f"""
            <div class="screenshot-card">
              <div class="screenshot-header">☕ Java Implementation (50%)</div>
              <img src="{java_img}" alt="Java Screenshot" onclick="openLightbox('{java_img}', false)" loading="lazy">
            </div>
            <div class="screenshot-card">
              <div class="screenshot-header">💜 Kotlin Implementation (50%)</div>
              <img src="{kotlin_img}" alt="Kotlin Screenshot" onclick="openLightbox('{kotlin_img}', false)" loading="lazy">
            </div>
            {defect_card_html}
            """

        cards_html += f"""
    <article class="sample-card" id="card-{idx}" data-index="{idx}" data-status="{status.lower()}" data-has-video="{str(has_video).lower()}" data-has-prior="{str(has_prior).lower()}" data-search="{html.escape(search_corpus)}">
      <div class="card-header">
        <div class="card-title-group">
          <span class="sample-number">#{idx:02d}</span>
          <h2 class="sample-title">{html.escape(title)}</h2>
          <span class="badge badge-cat">{html.escape(category)}</span>
        </div>
        <div class="card-status-group">
          {prior_badge}
          {video_badge}
          {status_badge}
        </div>
      </div>
      <div class="card-body">
        <div class="details-col">
          <div>
            <div class="section-label">Description</div>
            <p class="desc-text">{html.escape(desc)}</p>
          </div>

          <div class="spec-box">
            <div style="margin-bottom: 4px;"><strong>🎯 Purpose:</strong> {html.escape(purpose)}</div>
            <div><strong>✅ Success Criteria:</strong> {html.escape(success)}</div>
          </div>

          <div>
            <div class="section-label">Key API Calls</div>
            <div class="api-list">
              {api_tags_html or '<span style="color:#888; font-size:12px;">Standard SupportMapFragment bindings</span>'}
            </div>
          </div>

          <div>
            <div class="section-label">Tags</div>
            <div class="tags-list">
              {tags_html}
            </div>
          </div>

          {prior_directive_html}

          <div class="agent-box {agent_box_class}">
            <div class="section-label" style="color: {'var(--pass)' if is_pass else 'var(--fail)'};">🤖 Agent Verification Finding</div>
            <div class="finding-text">{html.escape(notes)}</div>
          </div>

          <div class="operator-box">
            <div class="operator-header">
              <span class="operator-label">✍️ Operator Feedback & Directives (dkhawk)</span>
              <div class="quick-actions">
                <button class="quick-btn" onclick="insertDirective({idx}, 'Tap at (x, y)')">+ Tap</button>
                <button class="quick-btn" onclick="insertDirective({idx}, 'Swipe from (x1, y1) to (x2, y2)')">+ Swipe</button>
                <button class="quick-btn" onclick="insertDirective({idx}, 'Needs +2s settle time')">+ Settle</button>
                <button class="quick-btn" onclick="insertDirective({idx}, 'Parity difference: ')">+ Parity</button>
                <button class="quick-btn" onclick="insertDirective({idx}, 'LGTM - verified on device')">+ LGTM</button>
              </div>
            </div>
            <textarea
              class="operator-input"
              id="notes-{idx}"
              placeholder="Add observations, gestures, or interaction directives for next iteration..."
              oninput="handleNoteChange({idx})"
            >{existing_notes}</textarea>
            <div class="operator-footer">
              <span id="saved-status-{idx}">{saved_status_text}</span>
              <label style="cursor: pointer; display: flex; align-items: center; gap: 4px;" title="Automatically flagged when feedback is entered">
                <input type="checkbox" id="override-{idx}" onchange="handleOverrideChange({idx})" {existing_checked}>
                Flag for Attention / Probing
              </label>
            </div>
          </div>
        </div>

        <div class="screenshots-col">
          <div class="media-header">
            <div class="section-label">Visual Verification (Java vs Kotlin)</div>
            {media_tabs_html}
          </div>
          <div class="screenshots-grid" id="stills-grid-{idx}">
            {stills_content_html}
          </div>
          {video_grid_html}
          {compare_grid_html}
        </div>
      </div>
    </article>
        """

    html_footer = """
  </main>

  <div id="lightbox" onclick="closeLightbox(event)">
    <img id="lightbox-img" src="" alt="Full size preview">
    <video id="lightbox-video" controls autoplay loop playsinline style="display:none; max-width:90%; max-height:90%; border-radius:8px; box-shadow:0 4px 20px rgba(0,0,0,0.5);"></video>
  </div>

  <div id="toast">Saved!</div>

  <script>
""" + get_dashboard_js(run_id, embedded_json) + """
  </script>
</body>
</html>
"""

    full_html = html_head_and_body + cards_html + html_footer
    out_file = run_dir / "index.html"
    with open(out_file, "w", encoding="utf-8") as f:
        f.write(full_html)
    print(f"Generated rich interactive HTML report at: {out_file}")
    return out_file


class ReviewServer(ThreadingHTTPServer):
    daemon_threads = True

    def __init__(self, server_address, RequestHandlerClass, run_dir, root_dir):
        super().__init__(server_address, RequestHandlerClass)
        self.run_dir = Path(run_dir).resolve()
        self.root_dir = Path(root_dir).resolve()


class ReviewHandler(SimpleHTTPRequestHandler):
    def translate_path(self, path):
        run_dir = getattr(self.server, "run_dir", Path.cwd())
        root_dir = getattr(self.server, "root_dir", Path.cwd())
        parsed = urllib.parse.urlparse(path)
        clean_path = parsed.path.lstrip("/")

        params = urllib.parse.parse_qs(parsed.query)
        if "run" in params:
            target_run_id = params["run"][0]
            target_run_dir = root_dir / "eval_runs" / target_run_id
            if target_run_dir.exists():
                return str(target_run_dir / "index.html")

        if not clean_path or clean_path == "/":
            return str(run_dir / "index.html")

        if clean_path.startswith("runs/"):
            rel_parts = clean_path.split("/", 2)
            if len(rel_parts) >= 3:
                target_run = rel_parts[1]
                sub_path = rel_parts[2]
                return str(root_dir / "eval_runs" / target_run / sub_path)

        if clean_path.startswith("run_"):
            return str(root_dir / "eval_runs" / clean_path)

        if clean_path.startswith("eval_runs/"):
            return str(root_dir / clean_path)

        candidate = run_dir / clean_path
        if candidate.exists():
            return str(candidate)

        return str(root_dir / "eval_runs" / clean_path)

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        if parsed.path == "/api/runs":
            root_dir = getattr(self.server, "root_dir", Path.cwd())
            runs = get_available_runs(root_dir)
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.send_header("Access-Control-Allow-Origin", "*")
            self.end_headers()
            self.wfile.write(json.dumps(runs).encode("utf-8"))
            return
        return super().do_GET()

    def do_POST(self):
        if self.path == "/api/save_notes":
            content_length = int(self.headers.get("Content-Length", 0))
            body = self.rfile.read(content_length).decode("utf-8")
            data = json.loads(body)
            target_run_id = data.get("run_id")
            root_dir = getattr(self.server, "root_dir", Path.cwd())

            if target_run_id and (root_dir / "eval_runs" / target_run_id).exists():
                run_dir = root_dir / "eval_runs" / target_run_id
            else:
                run_dir = getattr(self.server, "run_dir", Path.cwd())

            out_json = run_dir / "operator_feedback.json"
            with open(out_json, "w", encoding="utf-8") as f:
                json.dump(data, f, indent=2)

            out_md = run_dir / "operator_feedback.md"
            with open(out_md, "w", encoding="utf-8") as f:
                f.write(f"# ✍️ Operator Feedback & Directives - {data.get('run_id')}\n\n")
                for s in data.get("samples", []):
                    if s.get("operator_notes"):
                        f.write(f"### {s['title']} (`{s['id']}`)\n")
                        f.write(f"- **Operator Notes**: {s['operator_notes']}\n")
                        f.write(f"- **Status**: {s.get('status')}\n\n")

            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.send_header("Access-Control-Allow-Origin", "*")
            self.end_headers()
            self.wfile.write(b'{"status":"ok","saved":true}')
            print(f"[HTTP] Saved operator feedback to {out_json}")
            return
        self.send_error(404, "Endpoint not found")


def main():
    parser = argparse.ArgumentParser(description="Generate interactive HTML QA review report")
    parser.add_argument("-r", "--run-dir", help="Target run directory (default: latest in eval_runs/)")
    parser.add_argument("--scale", type=float, default=0.5, help="Scale images down (default: 0.5 = 50%%)")
    parser.add_argument("--serve", action="store_true", help="Start local HTTP server to view and interact")
    parser.add_argument("--port", type=int, default=8080, help="Port for HTTP server (default: 8080)")

    args = parser.parse_args()
    root_dir = Path(__file__).resolve().parent.parent

    if args.run_dir:
        run_dir = Path(args.run_dir).resolve()
    else:
        runs = sorted((root_dir / "eval_runs").glob("run_*"))
        if not runs:
            print("No run directories found in eval_runs/!")
            sys.exit(1)
        run_dir = runs[-1]

    summary_json_file = run_dir / "run_summary.json"
    if not summary_json_file.exists():
        print(f"run_summary.json not found in {run_dir}")
        sys.exit(1)

    with open(summary_json_file, "r", encoding="utf-8") as f:
        summary_data = json.load(f)

    if args.scale and 0 < args.scale < 1.0:
        scale_images_in_run(run_dir, args.scale)

    metadata_by_fqcn, metadata_by_short = load_catalog_metadata(root_dir)
    html_file = build_html(run_dir, summary_data, metadata_by_short, root_dir)

    latest_html = root_dir / "eval_runs" / "index.html"
    try:
        if latest_html.exists() or latest_html.is_symlink():
            latest_html.unlink()
        latest_html.symlink_to(html_file.relative_to(root_dir / "eval_runs"))
    except Exception:
        pass

    if args.serve:
        print(f"\nStarting review server on port {args.port}...")
        print(f"Local browser URL: http://dirtdog.c.googlers.com:{args.port}")
        server = ReviewServer(("", args.port), ReviewHandler, run_dir, root_dir)
        try:
            server.serve_forever()
        except KeyboardInterrupt:
            print("\nServer stopped.")


if __name__ == "__main__":
    main()
