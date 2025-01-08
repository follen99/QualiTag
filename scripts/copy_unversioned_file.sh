#!/bin/bash

# Make the script executable
chmod +x "$0"

# Change to the root directory of the repository
cd ..

# Ensure the "setup" directory exists
mkdir -p setup

# Get the list of unversioned files
unversioned_files=$(git ls-files --others --exclude-standard --ignored --directory)

# Copy each unversioned file to the "setup" directory
for file in $unversioned_files;
do
    # Skip files in the .github directory
    if [[ $file == .github* || $file == .idea* || $file == .vscode* || $file == qualitag/.gradle* || $file == qualitag/bin* || $file == qualitag/build* ]]; then
        continue
    fi

    # Create the directory structure in "setup" if necessary
    mkdir -p "setup/$(dirname "$file")"
    # Copy the file or directory
    cp -r "$file" "setup/$file"
done

echo "Unversioned files have been copied to the 'setup' directory."
# Create a zip file of the "setup" directory
zip -r setup.zip setup

echo "The 'setup' directory has been zipped into 'setup.zip'."

# Delete the "setup" directory
rm -rf setup

echo "The 'setup' directory has been deleted."