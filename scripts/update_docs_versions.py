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

import argparse
import re
import sys
from pathlib import Path

def parse_versions_toml(toml_path):
    versions = {}
    with open(toml_path, 'r') as f:
        for line in f:
            # Simple regex to find string versions: name = "version"
            match = re.match(r'^\s*(\w+)\s*=\s*"([^"]+)"', line)
            if match:
                versions[match.group(1)] = match.group(2)
    return versions

def update_file(file_path, versions, check_only=False):
    with open(file_path, 'r') as f:
        content = f.read()

    # Define replacements: (regex, replacement_template)
    # matching: implementation(libs.foo) // com.group:artifact:VERSION
    
    replacements = [
        (
            r'(compileOnly\(libs\.wearable\.compile\)\s*//\s*com\.google\.android\.wearable:wearable:)([\d\.]+)',
            f'\\g<1>{versions.get("wearable", "UNKNOWN")}'
        ),
        (
            r'(implementation\(libs\.wearable\.support\)\s*//\s*com\.google\.android\.support:wearable:)([\d\.]+)',
            f'\\g<1>{versions.get("wearable", "UNKNOWN")}'
        ),
        (
             r'(implementation\(libs\.play\.services\.maps\)\s*//\s*com\.google\.android\.gms:play-services-maps:)([\d\.]+)',
            f'\\g<1>{versions.get("playServicesMaps", "UNKNOWN")}'
        ),
        # Replacements for the "How-To" comment block
        (
            r'(//\s*wearable\s*=\s*")([\d\.]+)',
            f'\\g<1>{versions.get("wearable", "UNKNOWN")}'
        ),
        (
            r'(//\s*playServicesMaps\s*=\s*")([\d\.]+)',
            f'\\g<1>{versions.get("playServicesMaps", "UNKNOWN")}'
        ),
        (
            r'(//\s*wear\s*=\s*")([\d\.]+)',
            f'\\g<1>{versions.get("wear", "UNKNOWN")}'
        )
    ]

    new_content = content
    for pattern, replacement in replacements:
        new_content = re.sub(pattern, replacement, new_content)

    if new_content != content:
        if check_only:
            print(f"ERROR: {file_path} is out of sync with libs.versions.toml.")
            print("Run 'python3 scripts/update_docs_versions.py' to update it.")
            return False
        else:
            with open(file_path, 'w') as f:
                f.write(new_content)
            print(f"Updated {file_path}")
            return True
    
    if check_only:
        print(f"SUCCESS: {file_path} is in sync.")
    return True

def main():
    parser = argparse.ArgumentParser(description='Sync documentation versions with Version Catalog')
    parser.add_argument('--check', action='store_true', help='Check if files are up to date without modifying them')
    args = parser.parse_args()

    root_dir = Path(__file__).resolve().parent.parent
    toml_path = root_dir / 'gradle' / 'libs.versions.toml'
    target_file = root_dir / 'WearOS' / 'Wearable' / 'build.gradle.kts'

    if not toml_path.exists():
        print(f"Error: {toml_path} not found")
        sys.exit(1)

    versions = parse_versions_toml(toml_path)
    if 'wearable' not in versions or 'playServicesMaps' not in versions:
        print("Error: Could not find required versions in libs.versions.toml")
        sys.exit(1)

    success = update_file(target_file, versions, check_only=args.check)
    
    if args.check and not success:
        sys.exit(1)

if __name__ == '__main__':
    main()
