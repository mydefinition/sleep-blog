# sample-blog ???? (Windows PowerShell)
# ??: powershell -ExecutionPolicy Bypass -File .\build.ps1

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

Write-Host "=== 1/4 ???? ===" -ForegroundColor Cyan
Push-Location frontend
cmd /c "npm install --prefer-offline 2>&1"
if ($LASTEXITCODE -ne 0) { throw "npm install ??" }
cmd /c "npx vite build 2>&1"
if (-not (Test-Path dist\index.html)) { throw "vite build ?????? dist/index.html" }
Pop-Location

Write-Host "=== 2/4 ????????????? ===" -ForegroundColor Cyan
$staticDir = "$root\backend\src\main\resources\static"
if (Test-Path $staticDir) {
    Get-ChildItem -Path $staticDir -Exclude ".gitkeep" | Remove-Item -Recurse -Force
}
Copy-Item -Path "$root\frontend\dist\*" -Destination $staticDir -Recurse -Force

Write-Host "=== 3/4 ???? (fat JAR) ===" -ForegroundColor Cyan
Push-Location backend
$mvn = Get-ChildItem "$env:TEMP\maven-install" -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
if ($mvn) {
    $env:MAVEN_HOME = $mvn.FullName
    $env:PATH = "$($mvn.FullName)\bin;$env:PATH"
}
cmd /c "mvn clean package -DskipTests 2>&1"
if ($LASTEXITCODE -ne 0) { throw "mvn package ??" }
Pop-Location

Write-Host "=== 4/4 ?????? ===" -ForegroundColor Cyan
$releaseDir = "$root\release"
if (-not (Test-Path $releaseDir)) {
    New-Item -ItemType Directory -Path $releaseDir | Out-Null
}
Copy-Item -Path "$root\backend\target\sample-blog-1.0.0.jar" -Destination $releaseDir -Force
Copy-Item -Path "$root\application-prod.yml" -Destination $releaseDir -Force
Copy-Item -Path "$root\run.sh" -Destination $releaseDir -Force

Write-Host ""
Write-Host "=== ???? ===" -ForegroundColor Green
Write-Host "????: $releaseDir"
Get-ChildItem $releaseDir | ForEach-Object { Write-Host "  $($_.Name)" }
