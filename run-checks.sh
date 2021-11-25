#!/usr/bin/env bash
set -euxo pipefail

cd web
yarn lint
yarn test

cd ../server
./gradlew check
