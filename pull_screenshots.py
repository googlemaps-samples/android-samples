import subprocess
import os
import shutil

DEVICE_SCREENSHOT_DIR = "/sdcard/Pictures/screenshots/"
LOCAL_SCREENSHOT_DIR = "pulled_screenshots"

def pull_screenshots():
    if os.path.exists(LOCAL_SCREENSHOT_DIR):
        shutil.rmtree(LOCAL_SCREENSHOT_DIR)
    os.makedirs(LOCAL_SCREENSHOT_DIR)

    print(f"Pulling screenshots from {DEVICE_SCREENSHOT_DIR} to {LOCAL_SCREENSHOT_DIR}")
    command = ["adb", "pull", DEVICE_SCREENSHOT_DIR, LOCAL_SCREENSHOT_DIR]
    
    try:
        result = subprocess.run(command, capture_output=True, text=True, check=True)
        print("ADB Output:")
        print(result.stdout)
        if result.stderr:
            print("ADB Error (if any):")
            print(result.stderr)
        print("Screenshots pulled successfully.")
        
        # Optionally, clear screenshots from device after pulling
        # print(f"Clearing screenshots from device: {DEVICE_SCREENSHOT_DIR}")
        # subprocess.run(["adb", "shell", "rm -r", DEVICE_SCREENSHOT_DIR], check=True)

    except subprocess.CalledProcessError as e:
        print(f"Error pulling screenshots: {e}")
        print(f"Stdout: {e.stdout}")
        print(f"Stderr: {e.stderr}")
    except FileNotFoundError:
        print("Error: adb command not found. Please ensure Android SDK Platform-Tools are installed and in your PATH.")

if __name__ == "__main__":
    pull_screenshots()
