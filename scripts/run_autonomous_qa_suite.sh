#!/usr/bin/env bash
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
# run_autonomous_qa_suite.sh
# ==============================================================================
#
# Single-operation runner for the unattended autonomous QA verification suite.
# Executes end-to-end evaluation of all 31 samples (Java + Kotlin) without requiring
# operator approvals, captures screenshots, marks up defect areas, and streams
# verdicts directly into the on-device Room database.
#
# All results and artifacts are stored in a dedicated hierarchical directory:
#   eval_runs/run_<timestamp>/
#
# Usage:
#   # Run entire catalog unattended:
#   ./scripts/run_autonomous_qa_suite.sh
#
#   # Run quick smoke test on first 3 samples:
#   ./scripts/run_autonomous_qa_suite.sh --limit 3
#
#   # Run specific sample:
#   ./scripts/run_autonomous_qa_suite.sh --sample BasicMapDemoActivity
# ==============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

PYTHON_BIN="/usr/bin/python3"
RUNNER_PY="${SCRIPT_DIR}/run_autonomous_qa_suite.py"

if [[ ! -x "${RUNNER_PY}" ]]; then
    chmod +x "${RUNNER_PY}"
fi

cd "${ROOT_DIR}"
exec "${PYTHON_BIN}" "${RUNNER_PY}" "$@"
