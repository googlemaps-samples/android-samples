#!/usr/bin/env python3
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

"""
Interactive API Server for the Maps SDK Falsifiable Catalog Review Dashboard.
Serves MANUAL_VERIFY_CATALOG.html and provides REST endpoints to:
1. Build & run instrumented tests live via Gradle (`POST /api/run_test`)
2. Launch interactive samples on device via ADB (`POST /api/launch_sample`)
3. Save 1-to-5 star ratings & feedback directly to FALSIFIABLE_TASK_LIST.md (`POST /api/save_rating`)
"""
import argparse
import http.server
import json
import os
import re
import subprocess
import sys
import threading
import time

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RATINGS_FILE = os.path.join(PROJECT_ROOT, "test", "ratings_db.json")
TASK_LIST_PATH = os.path.join(PROJECT_ROOT, "FALSIFIABLE_TASK_LIST.md")

PORT = 8888
CUSTOM_JAVA_HOME = None

active_processes_lock = threading.Lock()
active_processes = set()
db_lock = threading.Lock()
task_list_lock = threading.Lock()

def run_tracked_subprocess(cmd, cwd=PROJECT_ROOT, env=None, timeout=180):
    proc = subprocess.Popen(cmd, cwd=cwd, env=env, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    with active_processes_lock:
        active_processes.add(proc)
    try:
        stdout, stderr = proc.communicate(timeout=timeout)
        return proc.returncode, stdout, stderr
    except subprocess.TimeoutExpired:
        try:
            proc.kill()
        except Exception:
            pass
        stdout, stderr = proc.communicate()
        raise
    finally:
        with active_processes_lock:
            active_processes.discard(proc)

# Ensure ratings db exists
if not os.path.exists(RATINGS_FILE):
    with open(RATINGS_FILE, "w", encoding="utf-8") as f:
        json.dump({"ratings": {}, "comments": {}, "progress": {"last_visited_id": None, "mode": "wizard"}}, f, indent=2)

class CatalogRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=PROJECT_ROOT, **kwargs)

    def do_POST(self):
        if self.path == "/api/run_test":
            self.handle_run_test()
        elif self.path == "/api/launch_sample":
            self.handle_launch_sample()
        elif self.path == "/api/save_rating":
            self.handle_save_rating()
        elif self.path == "/api/cancel":
            self.handle_cancel()
        else:
            self.send_error(404, "Unknown API endpoint")

    def do_GET(self):
        if self.path == "/api/get_ratings":
            self.handle_get_ratings()
        else:
            super().do_GET()

    def handle_get_ratings(self):
        try:
            with db_lock:
                with open(RATINGS_FILE, "r", encoding="utf-8") as f:
                    data = json.load(f)
            if "progress" not in data or not isinstance(data.get("progress"), dict):
                data["progress"] = {"last_visited_id": None, "mode": "wizard"}
            self.send_json_response(data)
        except Exception as e:
            self.send_json_response({"status": "error", "message": str(e)}, status=500)

    def handle_save_rating(self):
        try:
            content_length = int(self.headers.get("Content-Length", 0))
            body = self.rfile.read(content_length).decode("utf-8")
            payload = json.loads(body)
            cid = payload.get("id")
            rating = payload.get("rating")
            comment = payload.get("comment")

            if not cid:
                self.send_json_response({"status": "error", "message": "Missing capability ID 'id'"}, status=400)
                return

            with db_lock:
                with open(RATINGS_FILE, "r", encoding="utf-8") as f:
                    data = json.load(f)

                if "progress" not in data or not isinstance(data.get("progress"), dict):
                    data["progress"] = {"last_visited_id": None, "mode": "wizard"}

                data["progress"]["last_visited_id"] = cid

                if rating is not None:
                    data["ratings"][cid] = rating

                if "comment" in payload and comment is not None:
                    if str(comment).strip():
                        data["comments"][cid] = str(comment).strip()
                    elif cid in data["comments"]:
                        del data["comments"][cid]

                with open(RATINGS_FILE, "w", encoding="utf-8") as f:
                    json.dump(data, f, indent=2)

            # Discover next pending (unrated) capability ID
            next_id = None
            if os.path.exists(TASK_LIST_PATH):
                with task_list_lock:
                    with open(TASK_LIST_PATH, "r", encoding="utf-8") as f:
                        lines = f.readlines()
                all_cids = []
                for line in lines:
                    if line.strip().startswith("| `") and "` |" in line:
                        m = re.search(r'`([a-zA-Z0-9_]{8})`', line)
                        if m:
                            all_cids.append(m.group(1))
                if cid in all_cids:
                    idx = all_cids.index(cid)
                    for next_candidate in all_cids[idx+1:] + all_cids[:idx]:
                        if next_candidate not in data.get("ratings", {}):
                            next_id = next_candidate
                            break

            # Update markdown task list file asynchronously
            threading.Thread(target=update_markdown_task_list, args=(data,)).start()

            self.send_json_response({"status": "ok", "message": f"Saved rating for {cid}", "next_id": next_id})
        except Exception as e:
            self.send_json_response({"status": "error", "message": str(e)}, status=500)

    def handle_cancel(self):
        with active_processes_lock:
            procs = list(active_processes)
            for proc in procs:
                try:
                    proc.terminate()
                    proc.kill()
                except Exception:
                    pass
            active_processes.clear()
        try:
            subprocess.run(["./gradlew", "--stop"], cwd=PROJECT_ROOT, capture_output=True, timeout=10)
        except Exception:
            pass
        self.send_json_response({"status": "cancelled", "message": "🛑 Cancelled active execution and stopped Gradle daemon."})

    def handle_launch_sample(self):
        try:
            content_length = int(self.headers.get("Content-Length", 0))
            body = self.rfile.read(content_length).decode("utf-8")
            payload = json.loads(body)
            group = payload.get("group", "")
            title = payload.get("title", "")
            lang = payload.get("lang", "kotlin")
            pkg = "com.example.snippets.java" if lang == "java" else "com.example.snippets.kotlin"
            module = ":snippets:java-app" if lang == "java" else ":snippets:kotlin-app"

            if not title or title == "TODO":
                self.send_json_response({"status": "error", "message": "No valid snippet title specified for this capability."}, status=400)
                return

            print(f"Building and installing {lang.upper()} APK (`{module}:installDebug`)...")
            start_time = time.time()
            install_cmd = ["./gradlew", f"{module}:installDebug"]
            if CUSTOM_JAVA_HOME:
                install_cmd.insert(1, f"-Dorg.gradle.java.home={CUSTOM_JAVA_HOME}")
            custom_env = os.environ.copy()
            if CUSTOM_JAVA_HOME:
                custom_env["JAVA_HOME"] = CUSTOM_JAVA_HOME

            ret_code, stdout, stderr = run_tracked_subprocess(install_cmd, cwd=PROJECT_ROOT, env=custom_env, timeout=180)
            install_duration = round(time.time() - start_time, 1)
            install_out = (stdout or "") + "\n" + (stderr or "")

            if ret_code != 0:
                self.send_json_response({
                    "status": "error",
                    "message": f"❌ Rebuild & reinstall (`{module}:installDebug`) FAILED in {install_duration}s:\n\n=== Gradle Output ===\n{install_out[-3000:]}"
                })
                return

            group_clean = group.replace("'", "'\\''")
            title_clean = title.replace("'", "'\\''")
            remote_cmd = f"am start -S -n {pkg}/.MapActivity --es group_title '{group_clean}' --es snippet_title '{title_clean}'"
            cmd = ["adb", "shell", remote_cmd]
            print(f"Executing ADB launch command ({lang}): adb shell \"{remote_cmd}\"")
            adb_ret, adb_stdout, adb_stderr = run_tracked_subprocess(cmd, cwd=PROJECT_ROOT, env=custom_env, timeout=15)
            combined_adb_out = (adb_stdout or "") + "\n" + (adb_stderr or "")

            if adb_ret == 0 and "does not exist" not in combined_adb_out and "Error type" not in combined_adb_out:
                self.send_json_response({
                    "status": "success",
                    "message": f"✅ Rebuilt, installed ({install_duration}s), and launched {lang.upper()} sample '{title}' on attached device!\n\n=== ADB Launch Output ===\n{combined_adb_out.strip()}\n\n=== Gradle Install Output ===\n{install_out[-2000:].strip()}"
                })
            else:
                self.send_json_response({
                    "status": "error",
                    "message": f"❌ Rebuild succeeded ({install_duration}s), but ADB launch failed:\n{combined_adb_out}\n\n=== Gradle Install Output ===\n{install_out[-2000:].strip()}"
                })
        except subprocess.TimeoutExpired:
            self.send_json_response({
                "status": "error",
                "message": "❌ Rebuild or ADB launch timed out after 180 seconds.\nVerify device connection via 'adb devices'."
            })
        except Exception as e:
            self.send_json_response({"status": "error", "message": str(e)}, status=500)

    def handle_run_test(self):
        try:
            content_length = int(self.headers.get("Content-Length", 0))
            body = self.rfile.read(content_length).decode("utf-8")
            payload = json.loads(body)
            test_class = payload.get("test_class")
            test_method = payload.get("test_method")
            lang = payload.get("lang", "kotlin")
            module = ":snippets:java-app" if lang == "java" else ":snippets:kotlin-app"

            if not test_class or test_class == "TODO":
                self.send_json_response({
                    "status": "error",
                    "output": f"❌ Cannot run {lang.upper()} test: Test class is currently marked as TODO or not yet implemented.\n\nPlease check back once the falsifiable test suite is added."
                })
                return

            if test_method and test_method not in ["TODO", "ALL", "SUITE", ""]:
                class_arg = f"-Pandroid.testInstrumentationRunnerArguments.class={test_class}#{test_method}"
            else:
                class_arg = f"-Pandroid.testInstrumentationRunnerArguments.class={test_class}"

            cmd = [
                "./gradlew",
                f"{module}:connectedAndroidTest",
                class_arg
            ]
            if CUSTOM_JAVA_HOME:
                cmd.insert(1, f"-Dorg.gradle.java.home={CUSTOM_JAVA_HOME}")

            print(f"Running Gradle test command ({lang}): {' '.join(cmd)}")

            start_time = time.time()
            custom_env = os.environ.copy()
            if CUSTOM_JAVA_HOME:
                custom_env["JAVA_HOME"] = CUSTOM_JAVA_HOME
            ret_code, stdout, stderr = run_tracked_subprocess(cmd, cwd=PROJECT_ROOT, env=custom_env, timeout=180)
            duration = round(time.time() - start_time, 1)

            output_combined = (stdout or "") + "\n" + (stderr or "")

            if ret_code == 0:
                self.send_json_response({
                    "status": "passed",
                    "duration": duration,
                    "output": f"✅ TEST PASSED in {duration}s!\n\n=== Gradle Output ===\n{output_combined[-3000:]}"
                })
            else:
                self.send_json_response({
                    "status": "failed",
                    "duration": duration,
                    "output": f"❌ TEST FAILED in {duration}s (Exit code {ret_code})\n\n=== Gradle Output ===\n{output_combined[-4000:]}"
                })
        except subprocess.TimeoutExpired:
            self.send_json_response({
                "status": "failed",
                "duration": 180.0,
                "output": "❌ TEST TIMED OUT after 180 seconds.\nVerify that an Android emulator or physical device is connected via 'adb devices'."
            })
        except Exception as e:
            self.send_json_response({"status": "error", "output": f"Internal server error executing test: {str(e)}"}, status=500)

    def send_json_response(self, data, status=200):
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.end_headers()
        self.wfile.write(json.dumps(data).encode("utf-8"))

def update_markdown_task_list(db_data):
    try:
        if not os.path.exists(TASK_LIST_PATH):
            return
        with task_list_lock:
            with open(TASK_LIST_PATH, "r", encoding="utf-8") as f:
                lines = f.readlines()

            new_lines = []
            for line in lines:
                if line.strip().startswith("| `") and "` |" in line:
                    parts = [p.strip() for p in line.split("|")]
                    if len(parts) >= 8:
                        cid_match = re.search(r'`([a-zA-Z0-9_]{8})`', parts[1])
                        if cid_match:
                            cid = cid_match.group(1)
                            r_val = db_data.get("ratings", {}).get(cid)
                            c_val = db_data.get("comments", {}).get(cid)
                            if r_val is not None:
                                stars_str = "⭐" * int(r_val)
                                comment_suffix = ""
                                if c_val:
                                    c_clean = str(c_val).replace("\n", " ").replace("|", "╱").strip()
                                    c_trunc = (c_clean[:30] + "...") if len(c_clean) > 30 else c_clean
                                    comment_suffix = f" (*{c_trunc}*)"
                                parts[7] = f"[x] {stars_str} ({r_val}/5){comment_suffix}"
                            else:
                                parts[7] = "[ ] ___/5"
                            line = " | ".join(parts) + "\n"
                new_lines.append(line)

            with open(TASK_LIST_PATH, "w", encoding="utf-8") as f:
                f.writelines(new_lines)
    except Exception as e:
        print(f"Error updating markdown task list: {e}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Interactive API Server for Maps SDK Review Dashboard.")
    parser.add_argument("--port", type=int, default=8888, help="Port to bind the HTTP server to (default: 8888)")
    parser.add_argument("--java-home", "--jdk-home", dest="java_home", type=str, default="/usr/lib/jvm/java-21-openjdk-amd64", help="Path to JDK to use when executing Gradle build/test tasks")
    args = parser.parse_args()

    PORT = args.port
    CUSTOM_JAVA_HOME = args.java_home

    server_address = ("0.0.0.0", PORT)
    httpd = http.server.ThreadingHTTPServer(server_address, CatalogRequestHandler)
    print(f"🚀 Interactive Catalog API Server running on port {PORT} (JDK Home: {CUSTOM_JAVA_HOME})...")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping server.")
        httpd.server_close()
