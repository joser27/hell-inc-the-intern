# Compile and run the demo 2D game
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

# Compile
if (-not (Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
Write-Host "Compiling..." -ForegroundColor Cyan
javac -d out -sourcepath src src\Main.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# Run (res must be on classpath for images/fonts)
Write-Host "Running game..." -ForegroundColor Green
java -cp "out;res" Main
