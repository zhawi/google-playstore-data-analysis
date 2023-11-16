#!/bin/bash

# Create Docker network
docker network create grafana-net

# Build Docker images
docker build -t grafanaserver ./grafana/Dockerfile
docker build -t postgresserver ./postgres/Dockerfile

# Run containers
docker run --name postgres-server-container --network grafana-net -p 5432:5432 -d postgresserver
docker run --name grafana-server-container --network grafana-net -p 3000:3000 -d grafanaserver

echo "Grafana and PostgreSQL containers are up and running."