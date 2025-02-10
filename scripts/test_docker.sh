#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Make the script executable
chmod +x "$0"

# Change to the root directory of the repository
cd ..

# Function to stop the Docker container
function cleanup {
    echo -e "\n\nStopping the Docker container..."
    docker compose stop
}

# Ensure the cleanup function is called on script exit
trap cleanup EXIT

# Build the Docker container
echo -e "Building the Docker container..."
docker compose build

# Run the Docker container
echo -e "\n\nStarting the Docker container..."
docker compose up -d

# Run the tests
echo -e "\n\nRunning the Java tests..."
docker compose exec -T qualitag-java ./gradlew test

echo -e "\n\nRunning the Python tests..."
docker compose exec -T qualitag-python pytest -s
