# Build Windows app-image and fix launcher so double-click works.
# Run from project root: .\packaging\build-windows.ps1

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path $PSScriptRoot -Parent
if (-not (Test-Path "$projectRoot\pom.xml")) { $projectRoot = (Get-Location).Path }

Write-Host "Project root: $projectRoot"
Set-Location $projectRoot

# 1. Build fat JAR
Write-Host "`n[1/4] Building JAR..."
& mvn clean package -q
if ($LASTEXITCODE -ne 0) { throw "mvn package failed" }

# 2. Staging (only the fat JAR)
$staging = "$projectRoot\staging"
if (Test-Path $staging) { Remove-Item -Recurse -Force $staging }
New-Item -ItemType Directory $staging -Force | Out-Null
Copy-Item "$projectRoot\target\demonic-contractor-1.0-SNAPSHOT.jar" $staging\

# 3. jpackage (with --win-console so errors are visible)
$dist = "$projectRoot\dist"
if (Test-Path $dist) { Remove-Item -Recurse -Force $dist }
Write-Host "`n[2/4] Running jpackage..."
& "$env:JAVA_HOME\bin\jpackage.exe" `
  --input $staging `
  --main-jar demonic-contractor-1.0-SNAPSHOT.jar `
  --name "Demonic Contractor" `
  --app-version 1.0 `
  --type app-image `
  --dest $dist `
  --java-options "-Xmx512m" `
  --win-console `
  --jlink-options "--strip-debug --no-man-pages --no-header-files --compress zip-6"
if ($LASTEXITCODE -ne 0) { throw "jpackage failed" }

# 4. Fix JDK 17+ double-launch: add win.norestart to .cfg
$cfgPath = "$dist\Demonic Contractor\app\Demonic Contractor.cfg"
$cfg = Get-Content $cfgPath -Raw
if ($cfg -notmatch "win\.norestart") {
  $cfg = $cfg -replace "(\[Application\]\r?\napp\.mainjar=[^\r\n]+)", "`$1`r`nwin.norestart=true"
  Set-Content $cfgPath $cfg
  Write-Host "`n[3/4] Patched .cfg (win.norestart=true)"
} else {
  Write-Host "`n[3/4] .cfg already has win.norestart"
}

# 5. Copy run.bat so users can launch and see errors
$appDir = "$dist\Demonic Contractor"
Copy-Item "$PSScriptRoot\Run Demonic Contractor.bat" $appDir\
Write-Host "`n[4/4] Copied Run Demonic Contractor.bat"

Remove-Item -Recurse -Force $staging -ErrorAction SilentlyContinue
Write-Host "`nDone. Run: $appDir\Demonic Contractor.exe"
Write-Host "Or double-click: $appDir\Run Demonic Contractor.bat (shows errors if any)"
