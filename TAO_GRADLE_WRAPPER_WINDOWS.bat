@echo off
setlocal
cd /d "%~dp0"
set GRADLE_VERSION=8.9
set ZIP=%TEMP%\gradle-%GRADLE_VERSION%-bin.zip
set DIR=%TEMP%\gradle-%GRADLE_VERSION%

echo [1/3] Tai Gradle %GRADLE_VERSION%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
if errorlevel 1 goto :error

echo [2/3] Giai nen...
if exist "%DIR%" rmdir /s /q "%DIR%"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%ZIP%' -DestinationPath '%TEMP%' -Force"
if errorlevel 1 goto :error

echo [3/3] Tao Gradle Wrapper...
"%DIR%\bin\gradle.bat" wrapper --gradle-version %GRADLE_VERSION%
if errorlevel 1 goto :error

echo.
echo HOAN TAT. Bay gio mo Android Studio va Sync Project.
pause
exit /b 0
:error
echo.
echo CO LOI. Kiem tra Internet va thu lai.
pause
exit /b 1
