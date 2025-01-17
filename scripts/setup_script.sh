#!/bin/bash

# Make the script executable
chmod +x "$0"

# Change to the root directory of the repository
cd ..

# Unzip setup.zip
unzip setup.zip
echo "Setup directory unzipped."

# Copy all content of the directory setup to the current directory
cp -r -f setup/* setup/.* .
echo -e "\nContents of setup directory copied to current directory."

# Delete the directory setup
rm -rf setup
echo -e "\nSetup directory deleted."