#!/bin/bash

set -euo pipefail

docker run --rm \
  --name neo4j \
  -p 7474:7474 -p 7687:7687 \
  --env NEO4J_AUTH=neo4j/test \
  --volume=neo4j_data:/data \
  neo4j:latest
