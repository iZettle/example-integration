#!/usr/bin/env bash
set -euxo pipefail

(test -f server/.env && test -f web/.env) || (echo "Please populate .env files as described in README.md" && exit 1)

./create-certificate.sh

cd web && ./build-image.sh
cd ../server && ./build-image.sh

cd ../
docker compose up -d
