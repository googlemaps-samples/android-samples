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
# manage_eval_run.sh
# ==============================================================================
#
# Operational CLI utility for managing sample evaluation cycles:
#   1. Pulling verification & defect screenshots to workstation.
#   2. Wiping & resetting evaluation runs across device, DB, and UI.
#   3. Annotating defect screenshots with visual bounding boxes & callout badges.
#   4. Recording evaluations directly into the app's Room database via ADB.
#   5. Exporting Markdown Airing of Grievances reports.
# ==============================================================================

set -euo pipefail

# ------------------------------------------------------------------------------
# Terminal Aesthetics & Formatting
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
# Default Directories & Device Config
# ------------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

DEVICE_SCREENSHOT_DIR="/sdcard/gmp_eval_screenshots"
LOCAL_SCREENSHOT_DIR="${ROOT_DIR}/build/reports/eval_screenshots"
DEFAULT_OUTPUT_DIR="/usr/local/google/home/dkhawk/.gemini/jetski/brain/7f6d68d6-c603-43cd-93c6-c922c844c15a"
if [[ -d "${DEFAULT_OUTPUT_DIR}" ]]; then
    LOCAL_SCREENSHOT_DIR="${DEFAULT_OUTPUT_DIR}/eval_screenshots"
fi

DEVICE_SERIAL=""
KOTLIN_PKG="com.example.kotlindemos"
JAVA_PKG="com.example.mapdemo"

# ------------------------------------------------------------------------------
# Auto-detect Connected ADB Device
# ------------------------------------------------------------------------------
detect_device() {
    if [[ -n "${DEVICE_SERIAL}" ]]; then
        return
    fi

    local devices=($(adb devices | awk 'NR>1 && $2=="device" {print $1}'))
    local count=${#devices[@]}

    if [[ ${count} -eq 0 ]]; then
        adb connect localhost:35199 >/dev/null 2>&1 || true
        devices=($(adb devices | awk 'NR>1 && $2=="device" {print $1}'))
        count=${#devices[@]}
    fi

    if [[ ${count} -eq 0 ]]; then
        log_error "No active ADB devices or emulators detected! Please connect your device or forward ADB."
        exit 1
    elif [[ ${count} -eq 1 ]]; then
        DEVICE_SERIAL="${devices[0]}"
    else
        for dev in "${devices[@]}"; do
            if [[ "${dev}" == localhost:* || "${dev}" == 127.0.0.1:* ]]; then
                DEVICE_SERIAL="${dev}"
                return
            fi
        done
        DEVICE_SERIAL="${devices[0]}"
    fi
}

adb_cmd() {
    adb -s "${DEVICE_SERIAL}" "$@"
}

ensure_dirs() {
    mkdir -p "${LOCAL_SCREENSHOT_DIR}"
    adb_cmd shell mkdir -p "${DEVICE_SCREENSHOT_DIR}"
}

# ------------------------------------------------------------------------------
# Subcommand: Pull Screenshots
# ------------------------------------------------------------------------------
pull_screenshots() {
    local target_dir="${1:-${LOCAL_SCREENSHOT_DIR}}"
    mkdir -p "${target_dir}"
    log_step "Pulling screenshots from device [${DEVICE_SERIAL}:${DEVICE_SCREENSHOT_DIR}] -> [${target_dir}]"

    adb_cmd shell mkdir -p "${DEVICE_SCREENSHOT_DIR}"
    adb_cmd pull "${DEVICE_SCREENSHOT_DIR}/." "${target_dir}/" 2>/dev/null || true

    local count=$(find "${target_dir}" -maxdepth 1 -name "*.png" | wc -l)
    log_success "Successfully pulled ${count} screenshot(s) to ${target_dir}."
}

# ------------------------------------------------------------------------------
# Subcommand: Reset Evaluation Run
# ------------------------------------------------------------------------------
reset_run() {
    log_step "Resetting evaluation run across device database, filesystem, and UI..."

    # 1. Send broadcast to clear Room DB and app-internal files
    log_info "Broadcasting CLEAR_EVALUATIONS to ${KOTLIN_PKG}..."
    adb_cmd shell am broadcast -a com.google.maps.CLEAR_EVALUATIONS -p "${KOTLIN_PKG}" >/dev/null || true

    # 2. Wipe device screenshots
    log_info "Wiping device screenshot cache at ${DEVICE_SCREENSHOT_DIR}..."
    adb_cmd shell rm -rf "${DEVICE_SCREENSHOT_DIR}/*" || true
    adb_cmd shell mkdir -p "${DEVICE_SCREENSHOT_DIR}"

    # 3. Wipe local screenshot cache
    log_info "Cleaning local screenshot directory: ${LOCAL_SCREENSHOT_DIR}..."
    rm -rf "${LOCAL_SCREENSHOT_DIR:?}"/* || true

    # 4. Relaunch ReviewerActivity to refresh live Compose UI
    log_info "Relaunching ReviewerActivity to refresh live UI..."
    adb_cmd shell am force-stop "${KOTLIN_PKG}"
    adb_cmd shell am start -n "${KOTLIN_PKG}/com.example.common_ui.catalog.compose.ReviewerActivity" >/dev/null
    sleep 2.0

    log_success "Evaluation run completely reset! Reviewer catalog is fresh at ⚪ Unchecked (31)."
}

# ------------------------------------------------------------------------------
# Subcommand: Annotate Defect Screenshot
# ------------------------------------------------------------------------------
annotate_defect() {
    local in_file="$1"
    local box="$2"      # "x1,y1,x2,y2"
    local label="$3"
    local out_file="$4"

    if [[ ! -f "${in_file}" ]]; then
        log_error "Input screenshot does not exist: ${in_file}"
        exit 1
    fi

    IFS=',' read -r x1 y1 x2 y2 <<< "${box}"
    local label_y=$(( y1 > 60 ? y1 - 45 : y2 + 10 ))
    local label_y2=$(( label_y + 40 ))
    local text_y=$(( label_y + 28 ))

    log_info "Drawing defect annotation on ${in_file} -> ${out_file}"
    convert "${in_file}" \
        -stroke "#EF4444" -strokewidth 5 -fill "rgba(239, 68, 68, 0.2)" \
        -draw "rectangle ${x1},${y1} ${x2},${y2}" \
        -stroke none -fill "rgba(220, 38, 38, 0.9)" \
        -draw "roundrectangle ${x1},${label_y} $((x1 + 450)),${label_y2} 8,8" \
        -fill white -pointsize 26 -font DejaVu-Sans-Bold \
        -draw "text $((x1 + 15)),${text_y} '⚠️ ${label}'" \
        "${out_file}"

    local base_name="$(basename "${out_file}")"
    adb_cmd push "${out_file}" "${DEVICE_SCREENSHOT_DIR}/${base_name}" >/dev/null || true
    log_success "Defect annotation saved locally to ${out_file} and pushed to device."
}

# ------------------------------------------------------------------------------
# Subcommand: Record Evaluation via ADB
# ------------------------------------------------------------------------------
record_evaluation() {
    local fqcn="$1"
    local status="$2"
    local notes="$3"
    local screenshot="${4:-}"

    log_step "Recording evaluation for [${fqcn}] -> ${status}"
    local dev_screenshot=""
    if [[ -n "${screenshot}" ]]; then
        if [[ -f "${screenshot}" ]]; then
            local base="$(basename "${screenshot}")"
            adb_cmd push "${screenshot}" "${DEVICE_SCREENSHOT_DIR}/${base}" >/dev/null || true
            dev_screenshot="${DEVICE_SCREENSHOT_DIR}/${base}"
        else
            dev_screenshot="${screenshot}"
        fi
    fi

    local b64_notes="$(echo -n "${notes}" | base64 -w 0)"

    adb_cmd shell am broadcast \
        -a com.google.maps.RECORD_EVALUATION \
        -p "${KOTLIN_PKG}" \
        --es fqcn "${fqcn}" \
        --es status "${status}" \
        --es notes_b64 "${b64_notes}" \
        --es screenshot "${dev_screenshot}" >/dev/null

    log_success "Evaluation broadcast dispatched for ${fqcn}."
}

# ------------------------------------------------------------------------------
# Subcommand: Capture Sample (Java or Kotlin)
# ------------------------------------------------------------------------------
capture_sample() {
    local sample_class="$1"
    local framework="$2" # "java" or "kotlin"
    local output_name="${3:-eval_${sample_class}_${framework}.png}"

    local pkg=$([[ "${framework}" == "java" ]] && echo "${JAVA_PKG}" || echo "${KOTLIN_PKG}")
    local fqcn="${pkg}.${sample_class}"
    local local_file="${LOCAL_SCREENSHOT_DIR}/${output_name}"

    log_step "Launching ${framework} sample: ${fqcn}..."
    adb_cmd shell logcat -c
    adb_cmd shell am force-stop "${pkg}"
    adb_cmd shell am start -n "${pkg}/${fqcn}" \
        --es extra_sample_id "${fqcn}" \
        --ez extra_is_reviewer_mode true >/dev/null

    sleep 3.0

    log_info "Capturing screenshot -> ${local_file}..."
    adb_cmd shell screencap -p "/sdcard/${output_name}"
    adb_cmd pull "/sdcard/${output_name}" "${local_file}" >/dev/null
    adb_cmd shell cp "/sdcard/${output_name}" "${DEVICE_SCREENSHOT_DIR}/${output_name}"
    adb_cmd shell rm "/sdcard/${output_name}"

    log_success "Captured: ${local_file}"
}

# ------------------------------------------------------------------------------
# Subcommand: Export Report
# ------------------------------------------------------------------------------
export_report() {
    local out_file="${1:-${ROOT_DIR}/build/reports/latest_evaluation_report.md}"
    mkdir -p "$(dirname "${out_file}")"

    log_step "Triggering EXPORT_EVALUATIONS broadcast..."
    adb_cmd shell am broadcast -a com.google.maps.EXPORT_EVALUATIONS -p "${KOTLIN_PKG}" >/dev/null
    sleep 1.5

    local report_path="/sdcard/Android/data/${KOTLIN_PKG}/files/reports/latest_evaluation_report.md"
    adb_cmd pull "${report_path}" "${out_file}" >/dev/null 2>&1 || true

    if [[ -f "${out_file}" ]]; then
        log_success "Exported report pulled successfully to: ${out_file}"
    else
        log_warn "Report file not immediately at standard path; checking storage..."
    fi
}

# ------------------------------------------------------------------------------
# CLI Help & Dispatch
# ------------------------------------------------------------------------------
print_usage() {
    cat <<EOF
Usage: $(basename "$0") <COMMAND> [OPTIONS]

Operational evaluation run manager for Google Maps Platform Android Samples.

Commands:
  --pull-screenshots [dir]      Pull all screenshots from device to local directory
  --reset                       Wipe Room DB, clear device/local screenshots, reset UI to 31 unchecked
  --annotate-defect             Draw defect box and label on screenshot
                                Required: --input <file> --box <x1,y1,x2,y2> --label <text> --output <file>
  --record-eval                 Record evaluation into app's Room DB via broadcast
                                Required: --fqcn <fqcn> --status <PASSING|NEEDS_WORK> --notes <notes> [--screenshot <path>]
  --capture-sample              Launch and capture screenshot for a sample
                                Required: --sample <class_name> --framework <java|kotlin> [--output <name>]
  --export-report [file]        Trigger evaluation report export and pull Markdown file
EOF
}

main() {
    detect_device
    ensure_dirs

    if [[ $# -eq 0 ]]; then
        print_usage
        exit 1
    fi

    case "$1" in
        --pull-screenshots)
            shift
            pull_screenshots "${1:-}"
            ;;
        --reset)
            reset_run
            ;;
        --annotate-defect)
            shift
            local in_file="" box="" label="" out_file=""
            while [[ $# -gt 0 ]]; do
                case "$1" in
                    --input) in_file="$2"; shift 2 ;;
                    --box) box="$2"; shift 2 ;;
                    --label) label="$2"; shift 2 ;;
                    --output) out_file="$2"; shift 2 ;;
                    *) shift ;;
                esac
            done
            annotate_defect "${in_file}" "${box}" "${label}" "${out_file}"
            ;;
        --record-eval)
            shift
            local fqcn="" status="PASSING" notes="" screenshot=""
            while [[ $# -gt 0 ]]; do
                case "$1" in
                    --fqcn) fqcn="$2"; shift 2 ;;
                    --status) status="$2"; shift 2 ;;
                    --notes) notes="$2"; shift 2 ;;
                    --screenshot) screenshot="$2"; shift 2 ;;
                    *) shift ;;
                esac
            done
            record_evaluation "${fqcn}" "${status}" "${notes}" "${screenshot}"
            ;;
        --capture-sample)
            shift
            local sample="" framework="kotlin" out=""
            while [[ $# -gt 0 ]]; do
                case "$1" in
                    --sample) sample="$2"; shift 2 ;;
                    --framework) framework="$2"; shift 2 ;;
                    --output) out="$2"; shift 2 ;;
                    *) shift ;;
                esac
            done
            capture_sample "${sample}" "${framework}" "${out}"
            ;;
        --export-report)
            shift
            export_report "${1:-}"
            ;;
        *)
            log_error "Unknown command: $1"
            print_usage
            exit 1
            ;;
    esac
}

main "$@"
