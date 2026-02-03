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

# Parse arguments
RUN_CONNECTED_WEAR=false
RUN_CONNECTED_MOBILE=false

print_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Verifies all Android modules in the project by running assemble, test, and lint."
    echo ""
    echo "Options:"
    echo "  -h, --help           Show this help message and exit"
    echo "  --connected-wear     Run connected instrumentation tests for Wear OS modules"
    echo "  --connected-mobile   Run connected instrumentation tests for Mobile (Handheld) modules"
    echo "  --connected          Run ALL connected tests (Wear OS + Mobile) - Warning: Requires multiple emulators"
    echo ""
}

for arg in "$@"; do
    case $arg in
        -h|--help)
            print_usage
            exit 0
            ;;
        --connected-wear)
            RUN_CONNECTED_WEAR=true
            echo "Running with Connected Wear OS Tests..."
            ;;
        --connected-mobile)
            RUN_CONNECTED_MOBILE=true
            echo "Running with Connected Mobile Tests..."
            ;;
        --connected)
            RUN_CONNECTED_WEAR=true
            RUN_CONNECTED_MOBILE=true
            echo "Warning: Running ALL connected tests. This requires multiple simultaneous emulators (Wear + Handheld) or may fail."
            ;;
        *)
            echo "Unknown option: $arg"
            print_usage
            exit 1
            ;;
    esac
done

# Function to run verification for a module
verify_module() {
    local module=$1
    echo "------------------------------------------------"
    # Determine variant-specific tasks
    local assembleTask=":assembleDebug"
    local testTask=":testDebugUnitTest"
    local lintTask=":lintDebug"
    local connectedTask=""

    if [[ "$module" == ":snippets:app" ]]; then
        assembleTask=":assembleGmsDebug"
        testTask=":testGmsDebugUnitTest"
        lintTask=":lintGmsDebug"
    fi

    # Define connected test task if enabled
    if [ "$RUN_CONNECTED_WEAR" = true ] && [[ "$module" == ":WearOS:Wearable" ]]; then
         connectedTask=":connectedDebugAndroidTest"
    elif [ "$RUN_CONNECTED_MOBILE" = true ] && [[ "$module" != ":WearOS:Wearable" ]]; then
        if [[ "$module" == ":snippets:app" ]]; then
             connectedTask=":connectedGmsDebugAndroidTest"
        else
             connectedTask=":connectedDebugAndroidTest"
        fi
    fi
    # Note: If both flags are set, both types run (for their respective modules).

    # Build command
    local cmd="./gradlew $module$assembleTask $module$testTask $module$lintTask"
    
    if [ -n "$connectedTask" ]; then
        cmd="$cmd $module$connectedTask"
    fi

    # Run assemble, lint, and test (and connected if requested)
    echo "Running: $cmd"
    if $cmd; then
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
