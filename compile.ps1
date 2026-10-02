Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "   Compiling MediPredict Healthcare Application...  " -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
$cp = "lib/flatlaf-3.5.4.jar;lib/sqlite-jdbc-3.47.1.0.jar;lib/mysql-connector-j-9.2.0.jar"

javac -encoding UTF-8 -cp $cp -d bin $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] MediPredict compiled successfully into bin/" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Compilation failed with exit code $LASTEXITCODE" -ForegroundColor Red
}
