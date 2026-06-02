#!/bin/bash
# sample-blog ???? (Linux / macOS)
# ??: ./build.sh
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

echo "=== 1/4 ???? ==="
cd frontend
npm install --prefer-offline
npx vite build || true  # vite ??? chunk-size warning ????
if [ ! -f dist/index.html ]; then
  echo "vite build ?????? dist/index.html"
  exit 1
fi
cd ..

echo "=== 2/4 ????????????? ==="
STATIC_DIR="backend/src/main/resources/static"
rm -rf "$STATIC_DIR"/*
mkdir -p "$STATIC_DIR"
cp -r frontend/dist/* "$STATIC_DIR/"

echo "=== 3/4 ???? (fat JAR) ==="
cd backend
mvn clean package -DskipTests
cd ..

echo "=== 4/4 ?????? ==="
mkdir -p release
cp backend/target/sample-blog-1.0.0.jar release/
cp application-prod.yml release/
cp run.sh release/

echo ""
echo "=== ???? ==="
echo "????: $ROOT/release"
ls -lh release/
