import subprocess
import os
import shutil
import google.generativeai as genai
from PIL import Image

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
        
        return True # Indicate success
    except subprocess.CalledProcessError as e:
        print(f"Error pulling screenshots: {e}")
        print(f"Stdout: {e.stdout}")
        print(f"Stderr: {e.stderr}")
        return False # Indicate failure
    except FileNotFoundError:
        print("Error: adb command not found. Please ensure Android SDK Platform-Tools are installed and in your PATH.")
        return False # Indicate failure

def analyze_screenshots_with_gemini():
    # Configure the Gemini API client
    GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
    if not GEMINI_API_KEY:
        print("Error: GEMINI_API_KEY environment variable not set.")
        return
    genai.configure(api_key=GEMINI_API_KEY)
    model = genai.GenerativeModel('gemini-pro-vision')

    pulled_files = [f for f in os.listdir(LOCAL_SCREENSHOT_DIR) if f.endswith(".png")]
    if not pulled_files:
        print("No screenshots found to analyze.")
        return

    for filename in pulled_files:
        image_path = os.path.join(LOCAL_SCREENSHOT_DIR, filename)
        try:
            img = Image.open(image_path)

            prompt = "Describe this Android screenshot. Does it appear to be a map of Adelaide?"
            
            print(f"\nAnalyzing {filename} with Gemini...")
            response = model.generate_content([prompt, img])
            
            print(f"Gemini's analysis for {filename}:")
            print(response.text)

            if "adelaide" in response.text.lower() and "map" in response.text.lower():
                print(f"Assertion PASSED for {filename}: Gemini confirmed it's a map of Adelaide.")
            else:
                print(f"Assertion FAILED for {filename}: Gemini did not confirm it's a map of Adelaide.")

        except Exception as e:
            print(f"Error analyzing {filename}: {e}")

if __name__ == "__main__":
    if pull_screenshots():
        analyze_screenshots_with_gemini()
