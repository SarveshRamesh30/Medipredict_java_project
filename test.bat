@echo off
echo ===================================================
echo   Running MediPredict Automated Integration Tests
echo ===================================================

if not exist bin (
    call compile.bat
)

java -cp "bin;lib/*;resources" com.medipredict.IntegrationTest
pause
