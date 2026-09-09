#!/usr/bin/env python3
import os
import subprocess
import sys

def main():
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    script_path = os.path.join(project_root, "snippets", "scripts", "catalog_api.py")
    catalog_path = os.path.join(project_root, "snippets", "CATALOG.md")
    coverage_path = os.path.join(project_root, "snippets", "COVERAGE.md")

    # Clean up old catalog/coverage files if they exist to ensure fresh generation
    for path in [catalog_path, coverage_path]:
        if os.path.exists(path):
            os.remove(path)

    print("Running catalog_api.py script...")
    res = subprocess.run(
        [sys.executable, script_path],
        cwd=project_root,
        capture_output=True,
        text=True
    )
    
    if res.returncode != 0:
        print("Error: catalog_api.py failed with exit code:", res.returncode)
        print("Stdout:", res.stdout)
        print("Stderr:", res.stderr)
        sys.exit(1)

    print("Checking if CATALOG.md is generated and not empty...")
    if not os.path.exists(catalog_path):
        print("Error: CATALOG.md was not generated.")
        sys.exit(1)
    if os.path.getsize(catalog_path) == 0:
        print("Error: CATALOG.md is empty.")
        sys.exit(1)

    print("Checking if COVERAGE.md is generated and not empty...")
    if not os.path.exists(coverage_path):
        print("Error: COVERAGE.md was not generated.")
        sys.exit(1)
    if os.path.getsize(coverage_path) == 0:
        print("Error: COVERAGE.md is empty.")
        sys.exit(1)

    print("Verifying contents of CATALOG.md...")
    with open(catalog_path, "r", encoding="utf-8") as f:
        catalog_content = f.read()
    
    required_sections = [
        "## 📑 Snippet Concepts Index",
        "### Camera",
        "### Events",
        "### Map Initialization",
        "### Utility Library"
    ]
    for section in required_sections:
        if section not in catalog_content:
            print(f"Error: Missing section '{section}' in CATALOG.md")
            sys.exit(1)

    print("Verifying contents of COVERAGE.md...")
    with open(coverage_path, "r", encoding="utf-8") as f:
        coverage_content = f.read()

    required_coverage_elements = [
        "## Kotlin Snippets",
        "## Java Snippets",
        "### `GoogleMap`",
        "### `ClusterManager`",
        "### `GeoJsonLayer`",
        "### `KmlLayer`",
        "### `HeatmapTileProvider`",
        "### `GoogleMapKt`"
    ]
    for elem in required_coverage_elements:
        if elem not in coverage_content:
            print(f"Error: Missing class/section '{elem}' in COVERAGE.md")
            sys.exit(1)

    print("All checks passed successfully! Catalog automation is fully verified.")

if __name__ == "__main__":
    main()
