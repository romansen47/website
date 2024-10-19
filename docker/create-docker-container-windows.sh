#!/bin/bash

# Name of the image
IMAGE_NAME="chess"

# Find all container IDs that are using the image named "chess"
CONTAINER_IDS=$(docker.exe ps -aq --filter "name=$IMAGE_NAME")

# Stop and remove all containers that are using the "chess" image
if [ -n "$CONTAINER_IDS" ]; then
    echo "Stopping and removing all containers using the image $IMAGE_NAME..."
    docker.exe stop $CONTAINER_IDS
    docker.exe rm $CONTAINER_IDS
else
    echo "No containers found using the image $IMAGE_NAME."
fi

# Find the image ID for the image named "chess"
IMAGE_IDS=$(docker.exe images -q $IMAGE_NAME)

# Remove all images named "chess"
if [ -n "$IMAGE_IDS" ]; then
    echo "Removing all Docker images named $IMAGE_NAME..."
    docker.exe rmi $IMAGE_IDS
else
    echo "No Docker images found with the name $IMAGE_NAME."
fi

# Build the new image without using the cache
echo "Building the Docker image $IMAGE_NAME without cache..."
docker.exe build --no-cache -t $IMAGE_NAME .

# Start the new container
echo "Starting the new container with the image $IMAGE_NAME..."
docker.exe run -d -p 80:80 -p 443:443 --name $IMAGE_NAME $IMAGE_NAME

echo "The container was successfully created and started."

echo "Access through https://localhost/"
