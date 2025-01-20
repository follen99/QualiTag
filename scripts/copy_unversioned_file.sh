#!/bin/bash

# Make the script executable
chmod +x "$0"

# Change to the root directory of the repository
cd ..

# Ensure the "setup" directory exists
mkdir -p setup
echo "Created the 'setup' directory."

# Get the list of unversioned files
unversioned_files=$(git ls-files --others --exclude-standard --ignored --directory)

# Copy each unversioned file to the "setup" directory
echo -e "\nCopying unversioned files to the 'setup' directory..."
for file in $unversioned_files;
do
    # Skip files in the .github directory and other specified patterns
    if [[ $file == .github* || \
            $file == .idea* || \
            $file == .vscode* || \
            $file == qualitag/.gradle* || \
            $file == qualitag/bin* || \
            $file == qualitag/build* || \
            $file == qualitag_python/__pycache__* || \
            $file == qualitag_python/.pytest_cache* || \
            $file == qualitag_python/.venv* || \
            $file == artifacts* || \
            $file == setup.zip* || \
            $file == *~ || \
            $file == *.swp || \
            $file == *.swo ]]; then
        continue
    fi

    if [ -d "$file" ]; then
        echo -e "\nRecursively copying directory $file to setup/$file"
        mkdir -p "setup/$(dirname "$file")"  # Ensure parent structure exists
        cp -r "$file" "setup/$(dirname "$file")/"
    elif [ -f "$file" ]; then
        echo -e "\nCopying file $file to setup/$file"
        mkdir -p "setup/$(dirname "$file")"  # Ensure parent structure exists
        cp "$file" "setup/$file"
    fi
done

echo -e  "\nUnversioned files have been copied to the 'setup' directory."

# Remove the existing setup.zip file if it exists
if [ -f setup.zip ]; then
    rm setup.zip
    echo -e  "\nExisting setup.zip file has been removed."
fi

# Create a zip file of the "setup" directory
zip -r setup.zip setup

echo -e  "\nThe 'setup' directory has been zipped into 'setup.zip'."

# Delete the "setup" directory
rm -rf setup

echo -e  "\nThe 'setup' directory has been deleted."