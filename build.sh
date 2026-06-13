#!/bin/bash
# sleep-blog build script (Linux / macOS)
# Usage: ./build.sh [frontend|backend|all]
#   frontend  - Build frontend only (Vue + Vite)
#   backend   - Build backend only (Maven, no frontend resources)
#   all       - Build frontend, copy to backend, build fat JAR, package release (default)
set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"

MODE="${1:-all}"

build_frontend() {
  echo "=== Building frontend ==="
  cd "$ROOT/frontend"
  npm install --prefer-offline
  npx vite build || true
  if [ ! -f dist/index.html ]; then
    echo "ERROR: vite build did not produce dist/index.html"
    exit 1
  fi
  cd "$ROOT"
}

build_backend() {
  echo "=== Building backend ==="
  cd "$ROOT/backend"
  mvn clean package -DskipTests
  cd "$ROOT"
}

copy_frontend_to_static() {
  echo "=== Copying frontend dist to backend static ==="
  STATIC_DIR="$ROOT/backend/src/main/resources/static"
  rm -rf "$STATIC_DIR"/*
  mkdir -p "$STATIC_DIR"
  cp -r "$ROOT/frontend/dist/"* "$STATIC_DIR/"
}

package_release() {
  echo "=== Packaging release ==="
  mkdir -p "$ROOT/release"
  cp "$ROOT/backend/target/sleep-blog-1.0.0.jar" "$ROOT/release/"
  cp "$ROOT/application-prod.yml" "$ROOT/release/"
  cp "$ROOT/run.sh" "$ROOT/release/"

  echo ""
  echo "=== Build complete ==="
  echo "Release: $ROOT/release"
  ls -lh "$ROOT/release/"
}

case "$MODE" in
  frontend)
    build_frontend
    ;;
  backend)
    build_backend
    ;;
  all)
    build_frontend
    copy_frontend_to_static
    build_backend
    package_release
    ;;
  *)
    echo "Usage: $0 [frontend|backend|all]"
    echo "  frontend  - Build frontend only (Vue + Vite)"
    echo "  backend   - Build backend only (Maven)"
    echo "  all       - Build frontend, copy to backend, build fat JAR, package release (default)"
    exit 1
    ;;
esac