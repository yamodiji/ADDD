@echo off
echo ================================
echo Smart Drawer APK Builder
echo ================================
echo.

echo Cleaning previous builds...
call gradlew clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo Building debug APK...
call gradlew assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: Debug build failed!
    pause
    exit /b 1
)

echo.
echo ================================
echo BUILD SUCCESSFUL!
echo ================================
echo.
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
echo.
echo Installation instructions:
echo 1. Transfer APK to your Android device
echo 2. Enable "Unknown Sources" in Settings
echo 3. Install the APK
echo 4. Grant overlay permissions when prompted
echo.
pause 