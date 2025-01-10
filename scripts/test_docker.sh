#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# Make the script executable
chmod +x "$0"

# Change to the root directory of the repository
cd ..

# Build the Docker container
echo -e "Building the Docker container..."
docker compose build

# Run the Docker container
echo -e "\n\nStarting the Docker container..."
docker compose up -d

# Run the tests
echo -e "\n\nRunning the tests..."
docker compose exec -T qualitag ./gradlew test

# Stop the Docker container
echo -e "\n\nStopping the Docker container..."
docker compose stop