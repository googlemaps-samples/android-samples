# Copyright 2024 Google LLC
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

duplicate_and_merge() {
    local folderOriginal="$1"
    local folderDuplicate="$2"
    # Replace '*' with 'all' in folderOriginal for branch name
    local branchName="duplicate-${folderOriginal//\*/all}"
    # Replace '/' with '-' to ensure branch name is valid
    branchName="${branchName//\//-}"

    # Create the destination directory if needed
    mkdir -p "$(dirname "$folderDuplicate")"

    # Review the defined names
    echo "Original folder: $folderOriginal"
    echo "Duplicate folder: $folderDuplicate"
    echo "Branch name: $branchName"

    # Create and switch to the new branch
    git checkout -b "$branchName"
	   
    # Move the original folder to the duplicate location
     git mv -f "$folderOriginal" "$folderDuplicate"

    # Commit the changes
    git commit -m "Duplicate $folderOriginal to $folderDuplicate"

    # Restore the original folder
    git checkout HEAD~1 -- "$folderOriginal"

    # Commit the restoration
    git commit -m "Restore duplicated $folderOriginal"

	

    # Switch back to the source branch
    git checkout -

    # Merge the new branch into the current branch
    git merge --no-ff "$branchName" -m "Merge branch $branchName"


    # Delete the temporary branch and reset to main
    git branch -D "$branchName"
	squash_last_commits $folderOriginal
	#git reset --hard origin/main
}

# Function to squash the last three commits and set a commit message
squash_last_commits() {
    local folderOriginal="$1"

    # Check if folderOriginal is provided
    if [ -z "$folderOriginal" ]; then
        echo "Error: folderOriginal parameter is required."
        return 1
    fi

    # Ensure the script is run from the root of the repository
    if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
        echo "Error: This script must be run from the root of a Git repository."
        return 1
    fi

    # Check if there are at least 3 commits to squash
    local commitCount=$(git rev-list --count HEAD)
    if [ "$commitCount" -lt 2 ]; then
        echo "Error: Not enough commits to squash."
        return 1
    fi

    # Reset to the state before the last three commits
    git reset --soft HEAD~1

    # Create a new commit with all changes
    git commit -m "feat: moved files in $folderOriginal keeping history"

    # Force push the changes to the remote repository (if necessary)
    # Uncomment the following line if you need to push the changes to a remote branch
    # git push --force
}

# Call the function with different folders
duplicate_and_merge "ApiDemos/kotlin/app/src/gms/java/com/example/" "ApiDemos/kotlin/app/src/main/java/com/example/"
#duplicate_and_merge "ApiDemos/kotlin/app/src/gms/res/layout/" "ApiDemos/kotlin/app/src/main/res/layout"

#duplicate_and_merge "ApiDemos/java/app/src/gms/java/com/example/" "ApiDemos/java/app/src/main/java/com/example/"
#duplicate_and_merge "ApiDemos/java/app/src/gms/res/layout/" "ApiDemos/java/app/src/main/res/layout"
