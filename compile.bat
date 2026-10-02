@echo off
echo ===================================================
echo   Compiling MediPredict Healthcare Application...
echo ===================================================

if not exist bin mkdir bin

javac -encoding UTF-8 -cp "lib/*" -d bin src/com/medipredict/*.java src/com/medipredict/model/*.java src/com/medipredict/dao/*.java src/com/medipredict/service/*.java src/com/medipredict/database/*.java src/com/medipredict/prediction/*.java src/com/medipredict/util/*.java src/com/medipredict/ui/*.java src/com/medipredict/ui/components/*.java src/com/medipredict/ui/screens/*.java src/com/medipredict/ui/theme/*.java

if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] MediPredict compiled successfully into bin/
) else (
    echo [ERROR] Compilation failed!
)
pause
