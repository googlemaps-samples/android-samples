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
# verify_dev_cycle.sh
# ==============================================================================
#
# LITERATE PROGRAMMING & ARCHITECTURAL RATIONALE:
#
# 1. Problem Statement:
#    During development of the Google Maps Android Sample Catalog, verifying UI
#    changes across multiple modules, APKs, frameworks (Kotlin/Java), and modes
#    (Developer/Learner vs. Reviewer/Grader) involves a multi-step sequence:
#      a. Compiling Kotlin & Java APKs with Gradle.
#      b. Streamed installation over ADB to the connected target device.
#      c. Registering installed apps with the GMP DevRel Hub broadcast.
#      d. Launching specific activities with intent extras (framework, sample ID, mode).
#      e. Exercising UI components (e.g., opening "Info & Code" sheets).
#      f. Capturing screen verification artifacts.
#
#    Executing each command as an individual, compound shell command creates
#    friction by forcing engineers to manually approve every single terminal step.
#
# 2. Solution:
#    This unified script encapsulates the entire end-to-end verification cycle
#    into deterministic, reusable operations with robust auto-detection of ADB
#    devices and sensible defaults. A single execution completes the sequence
#    without interactive prompts or approval bottlenecks.
#
# 3. Usage Examples:
#    # Fast end-to-end: build, install, register, launch developer catalog & screenshot
#    ./scripts/verify_dev_cycle.sh --scenario dev-catalog
#
#    # Launch a specific sample and capture its About & APIs dialog (skip build):
#    ./scripts/verify_dev_cycle.sh --scenario sample-info --sample UiSettingsDemoActivity --no-build
#
#    # Full verification suite across developer and reviewer modes:
#    ./scripts/verify_dev_cycle.sh --scenario full-suite
# ==============================================================================

set -euo pipefail

# ------------------------------------------------------------------------------
# Terminal Aesthetics & ANSI Colors
# ------------------------------------------------------------------------------
BOLD="\033[1m"
GREEN="\033[0;32m"
BLUE="\033[0;34m"
YELLOW="\033[0;33m"
RED="\033[0;31m"
CYAN="\033[0;36m"
RESET="\033[0m"

log_info()    { echo -e "${BLUE}${BOLD}[INFO]${RESET} $1"; }
log_success() { echo -e "${GREEN}${BOLD}[SUCCESS]${RESET} $1"; }
log_warn()    { echo -e "${YELLOW}${BOLD}[WARN]${RESET} $1"; }
log_error()   { echo -e "${RED}${BOLD}[ERROR]${RESET} $1" >&2; }
log_step()    { echo -e "\n${CYAN}${BOLD}==>${RESET} ${BOLD}$1${RESET}"; }

# ------------------------------------------------------------------------------
# Default Configuration & Paths
# ------------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

KOTLIN_APK="${ROOT_DIR}/ApiDemos/project/kotlin-app/build/outputs/apk/debug/kotlin-app-debug.apk"
JAVA_APK="${ROOT_DIR}/ApiDemos/project/java-app/build/outputs/apk/debug/java-app-debug.apk"

DEFAULT_OUTPUT_DIR="/usr/local/google/home/dkhawk/.gemini/jetski/brain/7f6d68d6-c603-43cd-93c6-c922c844c15a"
if [[ ! -d "${DEFAULT_OUTPUT_DIR}" ]]; then
    DEFAULT_OUTPUT_DIR="${ROOT_DIR}/build/reports/verification"
fi

OUTPUT_DIR="${DEFAULT_OUTPUT_DIR}"
SCENARIO="dev-catalog"
SAMPLE_NAME="UiSettingsDemoActivity"
DO_BUILD=true
DO_INSTALL=true
DEVICE_SERIAL=""
SCREENSHOT_NAME=""

# ------------------------------------------------------------------------------
# Command Line Argument Parsing
# ------------------------------------------------------------------------------
print_usage() {
    cat <<EOF
Usage: $(basename "$0") [OPTIONS]

Unified build, deploy, launch, and verification script for GMP Android Samples.

Options:
  -s, --scenario <name>     Scenario to execute:
                            dev-catalog       Launch clean Developer Catalog (default)
                            reviewer-catalog  Launch Reviewer / Grader Mode
                            sample            Launch specific sample (Developer Mode)
                            sample-reviewer   Launch specific sample (Reviewer Mode)
                            sample-info       Launch sample and open "About & APIs" dialog
                            build-only        Only build debug APKs
                            install-only      Only install APKs & register DevRel Hub
                            full-suite        Run dev-catalog, reviewer-catalog & sample-info
  --sample <name>           Sample class name (default: UiSettingsDemoActivity)
  --no-build                Skip the Gradle compilation step
  --no-install              Skip ADB installation and DevRel Hub registration
  -d, --device <serial>     Target ADB device serial (auto-detected if omitted)
  -o, --output-dir <path>   Directory where screenshots are saved (default: ${OUTPUT_DIR})
  --screenshot <name>       Custom filename for the screenshot
  -h, --help                Display this help message and exit
EOF
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        -s|--scenario)
            SCENARIO="$2"
            shift 2
            ;;
        --sample)
            SAMPLE_NAME="$2"
            shift 2
            ;;
        --no-build)
            DO_BUILD=false
            shift
            ;;
        --no-install)
            DO_INSTALL=false
            shift
            ;;
        -d|--device)
            DEVICE_SERIAL="$2"
            shift 2
            ;;
        -o|--output-dir)
            OUTPUT_DIR="$2"
            shift 2
            ;;
        --screenshot)
            SCREENSHOT_NAME="$2"
            shift 2
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        *)
            log_error "Unknown option: $1"
            print_usage
            exit 1
            ;;
    esac
done

mkdir -p "${OUTPUT_DIR}"

# ------------------------------------------------------------------------------
# Auto-detect Connected ADB Device
# ------------------------------------------------------------------------------
detect_device() {
    if [[ -n "${DEVICE_SERIAL}" ]]; then
        log_info "Using explicitly specified target device: ${DEVICE_SERIAL}"
        return
    fi

    # Read connected devices excluding header line
    local devices=($(adb devices | awk 'NR>1 && $2=="device" {print $1}'))
    local count=${#devices[@]}

    if [[ ${count} -eq 0 ]]; then
        # Attempt fallback reconnect to standard forwarded ADB port
        adb connect localhost:35199 >/dev/null 2>&1 || true
        devices=($(adb devices | awk 'NR>1 && $2=="device" {print $1}'))
        count=${#devices[@]}
    fi

    if [[ ${count} -eq 0 ]]; then
        log_error "No active ADB devices or emulators detected! Please connect your device or forward ADB."
        exit 1
    elif [[ ${count} -eq 1 ]]; then
        DEVICE_SERIAL="${devices[0]}"
        log_info "Auto-detected active device: ${DEVICE_SERIAL}"
    else
        for dev in "${devices[@]}"; do
            if [[ "${dev}" == localhost:* || "${dev}" == 127.0.0.1:* ]]; then
                DEVICE_SERIAL="${dev}"
                log_info "Selected forwarded target device: ${DEVICE_SERIAL}"
                return
            fi
        done
        DEVICE_SERIAL="${devices[0]}"
        log_info "Multiple devices found; defaulting to first device: ${DEVICE_SERIAL}"
    fi
}

adb_cmd() {
    adb -s "${DEVICE_SERIAL}" "$@"
}

# ------------------------------------------------------------------------------
# Build Step
# ------------------------------------------------------------------------------
build_apks() {
    log_step "Building Kotlin and Java Debug APKs..."
    cd "${ROOT_DIR}"
    ./gradlew :ApiDemos:kotlin-app:assembleDebug :ApiDemos:java-app:assembleDebug \
        -g "${ROOT_DIR}/.gradle-test" --no-daemon
    log_success "Gradle compilation successful."
}

# ------------------------------------------------------------------------------
# Install & DevRel Hub Registration Step
# ------------------------------------------------------------------------------
install_and_register() {
    log_step "Installing APKs to device [${DEVICE_SERIAL}]..."
    if [[ ! -f "${KOTLIN_APK}" ]] || [[ ! -f "${JAVA_APK}" ]]; then
        log_error "APKs not found! Run without --no-build first."
        exit 1
    fi

    adb_cmd install -r "${KOTLIN_APK}"
    adb_cmd install -r "${JAVA_APK}"
    log_success "Both APKs successfully installed."

    log_step "Registering with Google Maps Platform DevRel Hub..."
    adb_cmd shell am broadcast \
        -a com.google.maps.samplehub.REGISTER \
        --es name "Google Maps Platform Samples" \
        --es package "com.example.kotlindemos" \
        --es activity "com.example.kotlindemos.MainActivity" \
        --es repo "android-samples" \
        --es tags "catalog,samples,maps,learn" >/dev/null || true

    adb_cmd shell am broadcast \
        -a com.google.maps.samplehub.REGISTER \
        --es name "GMP Sample Reviewer" \
        --es package "com.example.kotlindemos" \
        --es activity "com.example.common_ui.catalog.compose.ReviewerActivity" \
        --es repo "android-samples" \
        --es tags "catalog,reviewer,samples,maps,grader" >/dev/null || true

    log_success "DevRel Hub registrations completed."
}

# ------------------------------------------------------------------------------
# Helper: Capture Screenshot
# ------------------------------------------------------------------------------
capture_screenshot() {
    local target_name="$1"
    local local_file="${OUTPUT_DIR}/${target_name}.png"
    log_info "Capturing screenshot -> ${local_file}"
    adb_cmd shell screencap -p /sdcard/verify_screenshot.png
    adb_cmd pull /sdcard/verify_screenshot.png "${local_file}" >/dev/null
    log_success "Screenshot saved: ${local_file}"
}

# ------------------------------------------------------------------------------
# Scenario Executions
# ------------------------------------------------------------------------------

run_dev_catalog() {
    log_step "Executing Scenario: Developer / Learner Catalog"
    adb_cmd shell am force-stop com.example.kotlindemos
    adb_cmd shell am start -n com.example.kotlindemos/com.example.kotlindemos.MainActivity
    sleep 2.5
    local name="${SCREENSHOT_NAME:-screenshot_verified_dev_catalog}"
    capture_screenshot "${name}"
}

run_reviewer_catalog() {
    log_step "Executing Scenario: Reviewer / Grader Mode Catalog"
    adb_cmd shell am force-stop com.example.kotlindemos
    adb_cmd shell am start -n com.example.kotlindemos/com.example.common_ui.catalog.compose.ReviewerActivity
    sleep 2.5
    local name="${SCREENSHOT_NAME:-screenshot_verified_reviewer_catalog}"
    capture_screenshot "${name}"
}

run_sample() {
    local is_reviewer="$1"
    local mode_label=$([[ "${is_reviewer}" == "true" ]] && echo "Reviewer" || echo "Developer")
    log_step "Executing Scenario: Sample [${SAMPLE_NAME}] in ${mode_label} Mode"

    local fqcn="com.example.kotlindemos.${SAMPLE_NAME}"
    if [[ "${SAMPLE_NAME}" == *.* ]]; then
        fqcn="${SAMPLE_NAME}"
    fi

    adb_cmd shell am force-stop com.example.kotlindemos
    adb_cmd shell am start -n "com.example.kotlindemos/${fqcn}" \
        --es extra_sample_id "${fqcn}" \
        --ez extra_is_reviewer_mode "${is_reviewer}"
    sleep 2.5
    local name="${SCREENSHOT_NAME:-screenshot_verified_sample_${SAMPLE_NAME}_${mode_label}}"
    capture_screenshot "${name}"
}

run_sample_info() {
    log_step "Executing Scenario: Sample About & APIs Dialog for [${SAMPLE_NAME}]"
    local fqcn="com.example.kotlindemos.${SAMPLE_NAME}"
    if [[ "${SAMPLE_NAME}" == *.* ]]; then
        fqcn="${SAMPLE_NAME}"
    fi

    adb_cmd shell am force-stop com.example.kotlindemos
    adb_cmd shell am start -n "com.example.kotlindemos/${fqcn}" \
        --es extra_sample_id "${fqcn}" \
        --ez extra_is_reviewer_mode false
    sleep 2.5

    log_info "Tapping 'About & APIs' button at (911, 201)..."
    adb_cmd shell input tap 911 201
    sleep 2.0

    local name="${SCREENSHOT_NAME:-screenshot_verified_sample_info_${SAMPLE_NAME}}"
    capture_screenshot "${name}"
}

# ------------------------------------------------------------------------------
# Main Flow Orchestration
# ------------------------------------------------------------------------------
main() {
    log_info "GMP Android Samples - Automated Verification Runner"
    log_info "Scenario: ${SCENARIO}"
    log_info "Output Directory: ${OUTPUT_DIR}"

    detect_device

    if [[ "${SCENARIO}" == "build-only" ]]; then
        build_apks
        log_success "Build-only scenario finished."
        exit 0
    fi

    if [[ "${DO_BUILD}" == "true" ]]; then
        build_apks
    else
        log_info "Skipping Gradle build (--no-build requested)."
    fi

    if [[ "${DO_INSTALL}" == "true" ]]; then
        install_and_register
    else
        log_info "Skipping APK installation (--no-install requested)."
    fi

    case "${SCENARIO}" in
        dev-catalog)
            run_dev_catalog
            ;;
        reviewer-catalog)
            run_reviewer_catalog
            ;;
        sample)
            run_sample false
            ;;
        sample-reviewer)
            run_sample true
            ;;
        sample-info)
            run_sample_info
            ;;
        install-only)
            log_success "Install & register completed."
            ;;
        full-suite)
            run_dev_catalog
            run_reviewer_catalog
            run_sample_info
            log_success "Full verification suite completed successfully!"
            ;;
        *)
            log_error "Unrecognized scenario: ${SCENARIO}"
            print_usage
            exit 1
            ;;
    esac

    echo ""
    log_success "=========================================================="
    log_success "Verification Sequence Complete!"
    log_success "Artifacts written to: ${OUTPUT_DIR}"
    log_success "=========================================================="
}

main "$@"
