# sample-blog build script (Windows PowerShell)
# Usage: .\build.ps1 [frontend|backend|all]
#   frontend  - Build frontend only (Vue + Vite)
#   backend   - Build backend only (Maven, no frontend resources)
#   all       - Build frontend, copy to backend, build fat JAR, package release (default)

param([string]$Mode = "all")

$ErrorActionPreference = "Stop"
$root = $PSScriptRoot

function Find-MavenHome {
    # 1) %TEMP%\maven-install
    $mvn = Get-ChildItem "$env:TEMP\maven-install" -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($mvn) { return $mvn.FullName }
    # 2) ~/.m2/wrapper/dists (Maven wrapper cache)
    $dists = Get-ChildItem "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-*-bin" -Directory -ErrorAction SilentlyContinue
    if ($dists) {
        $latest = $dists | Sort-Object Name -Descending | Select-Object -First 1
        $inner = Get-ChildItem $latest.FullName -Directory | Select-Object -First 1
        if ($inner) { return $inner.FullName }
    }
    return $null
}

function Build-Frontend {
    Write-Host "=== Building frontend ===" -ForegroundColor Cyan
    Push-Location "$root\frontend"
    cmd /c "npm install --prefer-offline 2>&1"
    if ($LASTEXITCODE -ne 0) { throw "npm install failed" }
    cmd /c "npx vite build 2>&1"
    if (-not (Test-Path dist\index.html)) { throw "vite build did not produce dist/index.html" }
    Pop-Location
}

function Build-Backend {
    Write-Host "=== Building backend ===" -ForegroundColor Cyan
    $mavenHome = Find-MavenHome
    if (-not $mavenHome) {
        throw "Maven not found. Please install Maven or place it in `$env:TEMP\maven-install"
    }
    Write-Host "  Using Maven: $mavenHome" -ForegroundColor DarkGray
    $env:MAVEN_HOME = $mavenHome
    $env:PATH = "$mavenHome\bin;$env:PATH"
    Push-Location "$root\backend"
    cmd /c "mvn clean package -DskipTests 2>&1"
    if ($LASTEXITCODE -ne 0) { throw "mvn package failed" }
    Pop-Location
}

function Copy-FrontendToStatic {
    Write-Host "=== Copying frontend dist to backend static ===" -ForegroundColor Cyan
    $staticDir = "$root\backend\src\main\resources\static"
    if (Test-Path $staticDir) {
        Get-ChildItem -Path $staticDir -Exclude ".gitkeep" | Remove-Item -Recurse -Force
    }
    Copy-Item -Path "$root\frontend\dist\*" -Destination $staticDir -Recurse -Force
}

function Package-Release {
    Write-Host "=== Packaging release ===" -ForegroundColor Cyan
    $releaseDir = "$root\release"
    if (-not (Test-Path $releaseDir)) {
        New-Item -ItemType Directory -Path $releaseDir | Out-Null
    }
    Copy-Item -Path "$root\backend\target\sample-blog-1.0.0.jar" -Destination $releaseDir -Force
    Copy-Item -Path "$root\application-prod.yml" -Destination $releaseDir -Force
    Copy-Item -Path "$root\run.sh" -Destination $releaseDir -Force

    Write-Host ""
    Write-Host "=== Build complete ===" -ForegroundColor Green
    Write-Host "Release: $releaseDir"
    Get-ChildItem $releaseDir | ForEach-Object { Write-Host "  $($_.Name)" }
}

switch ($Mode.ToLower()) {
    "frontend" {
        Build-Frontend
    }
    "backend" {
        Build-Backend
    }
    "all" {
        Build-Frontend
        Copy-FrontendToStatic
        Build-Backend
        Package-Release
    }
    default {
        Write-Host "Usage: .\build.ps1 [frontend|backend|all]"
        Write-Host "  frontend  - Build frontend only (Vue + Vite)"
        Write-Host "  backend   - Build backend only (Maven)"
        Write-Host "  all       - Build frontend, copy to backend, build fat JAR, package release (default)"
        exit 1
    }
}
