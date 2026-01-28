#!/bin/bash

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

# script: verify_all.sh
# description: Builds and verifies all Android modules in the project.

set -e # Exit immediately if a command exits with a non-zero status.

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color


echo "Checking documentation versions sync..."
python3 scripts/update_docs_versions.py --check || {
  echo "FAILURE: Documentation versions are out of sync with Version Catalog."
  echo "Run 'python3 scripts/update_docs_versions.py' to fix."
  exit 1
}

echo "Starting project verification..."

# List of modules to check
# Note: snippets has submodules, checking root snippets project might be enough if it aggregates them,
# but we'll be explicit or use standard tasks.
MODULES=(
    ":ApiDemos:java-app"
    ":ApiDemos:kotlin-app"
    ":ApiDemos:common-ui"
    ":FireMarkers:app"
    ":WearOS:Wearable"
    ":snippets:app"
    ":snippets:app-ktx"
    ":snippets:app-utils-ktx"
    ":snippets:app-compose"
    ":snippets:app-places-ktx"
    ":snippets:app-utils"
    ":tutorials:kotlin:Polygons"
)

# Function to run verification for a module
verify_module() {
    local module=$1
    echo "------------------------------------------------"
    # Determine variant-specific tasks
    local assembleTask=":assembleDebug"
    local testTask=":testDebugUnitTest"
    local lintTask=":lintDebug"

    if [[ "$module" == ":snippets:app" ]]; then
        assembleTask=":assembleGmsDebug"
        testTask=":testGmsDebugUnitTest"
        lintTask=":lintGmsDebug"
    fi

    # Run assemble, lint, and test
    if ./gradlew "$module$assembleTask" "$module$testTask" "$module$lintTask"; then
         echo -e "${GREEN}SUCCESS: $module verified.${NC}"
    else
         echo -e "${RED}FAILURE: $module failed verification.${NC}"
         return 1
    fi
}

FAILED_MODULES=()

for module in "${MODULES[@]}"; do
    if ! verify_module "$module"; then
        FAILED_MODULES+=("$module")
    fi
done

echo "------------------------------------------------"
if [ ${#FAILED_MODULES[@]} -eq 0 ]; then
    echo -e "${GREEN}ALL MODULES PASSED VERIFICATION.${NC}"
    exit 0
else
    echo -e "${RED}THE FOLLOWING MODULES FAILED:${NC}"
    for failed in "${FAILED_MODULES[@]}"; do
        echo -e "${RED}- $failed${NC}"
    done
    exit 1
fi
