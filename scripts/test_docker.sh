#!/bin/bash

# Make the script executable
chmod +x "$0"

# Change to the root directory of the repository
cd ..

# Build the Docker container
docker compose build

# Run the Docker container
docker compose up -d

# Run the tests
docker compose exec -T qualitag ./gradlew test

# Stop the Docker container
docker compose stop