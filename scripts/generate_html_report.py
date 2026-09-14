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
Backward compatibility wrapper for generate_html_report.py.

Delegates directly to generate_report.py while preserving all module exports
and function signatures used across the QA suite.
"""

from generate_report import (
    PRIOR_DIRECTIVES_MAP,
    ReviewHandler,
    ReviewServer,
    build_html,
    generate_all_reports,
    generate_html_report,
    generate_markdown_summary,
    get_available_runs,
    load_catalog_metadata,
    load_previous_run_data,
    main,
)

if __name__ == "__main__":
    main()
