#!/bin/bash

# Stop execution if any command fails
set -e

echo "Starting build and deployment process..."

#stopping and removing the containers
echo "Stopping and removing the containers..."
docker-compose down

# Pulling master changes
echo "Pulling master changes..."
git pull
# Building the project
echo "Building the project..."
./gradlew build

# Building and starting the Docker containers
echo "Building and starting Docker containers..."
docker-compose up --build -d

echo "Build and deployment completed successfully."