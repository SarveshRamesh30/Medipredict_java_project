@echo off
echo ===================================================
echo   Launching MediPredict Desktop Application...
echo ===================================================

if not exist bin (
    call compile.bat
)

java -cp "bin;lib/*;resources" com.medipredict.Main
pause
