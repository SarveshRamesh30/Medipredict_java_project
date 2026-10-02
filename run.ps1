Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "   Launching MediPredict Desktop Application...     " -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    & .\compile.ps1
}

$cp = "bin;lib/flatlaf-3.5.4.jar;lib/sqlite-jdbc-3.47.1.0.jar;lib/mysql-connector-j-9.2.0.jar;resources"
java -cp $cp com.medipredict.Main
