# Build and run the game via Maven (includes all dependencies: VorbisSPI, Gson, OkHttp, etc.)
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

Write-Host "Building and running game..." -ForegroundColor Cyan
mvn compile exec:java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
