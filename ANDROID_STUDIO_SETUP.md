# Android Studio Setup Guide for Smart Drawer

## 🚨 Fixing Gradle Distribution Error

### Problem
```
Could not install Gradle distribution from 'https://services.gradle.org/distributions/gradle-8.4-bin.zip'.
Reason: java.lang.RuntimeException: Could not create parent directory for lock file
```

### Root Cause
The issue is with spaces in the Gradle cache path: `C:\Users\Ganesha\.gradle\wrapper\dists \gradle-8.4-bin\`

---

## 🔧 Solution 1: Change Gradle Home Directory (RECOMMENDED)

### Step 1: Open Android Studio Settings
1. **File** → **Settings** (or **Android Studio** → **Preferences** on Mac)
2. **Build, Execution, Deployment** → **Build Tools** → **Gradle**

### Step 2: Set Custom Gradle Home
1. **Gradle Home**: Set to `C:\gradle` (or any path WITHOUT spaces)
2. **Gradle user home**: Set to `C:\gradle-cache`
3. Click **Apply** and **OK**

### Step 3: Create Directories
```batch
mkdir C:\gradle
mkdir C:\gradle-cache
```

---

## 🔧 Solution 2: Use Project Gradle Wrapper

### Step 1: In Android Studio Settings
1. **File** → **Settings**
2. **Build, Execution, Deployment** → **Build Tools** → **Gradle**
3. Select **"Use Gradle from: gradle-wrapper.properties file"**
4. **Gradle JDK**: Select **"Project Structure SDK"** or **JDK 17**

### Step 2: Clean and Rebuild
1. **Build** → **Clean Project**
2. **Build** → **Rebuild Project**

---

## 🔧 Solution 3: Clear Gradle Cache

### Windows PowerShell (Run as Administrator)
```powershell
# Delete problematic Gradle cache
Remove-Item -Recurse -Force "$env:USERPROFILE\.gradle\caches"
Remove-Item -Recurse -Force "$env:USERPROFILE\.gradle\wrapper"

# Create clean directories
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.gradle\caches"
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.gradle\wrapper"
```

### Alternative: Manual Cleanup
1. Navigate to `C:\Users\Ganesha\.gradle\`
2. Delete `caches` and `wrapper` folders
3. Restart Android Studio

---

## 🔧 Solution 4: Fix Path with Spaces

### Method A: Use Short Path Names
```batch
# In Command Prompt, run:
dir /x C:\Users\
# This shows short names like GANESH~1 instead of "Ganesha"
```

### Method B: Set Environment Variable
1. **System Properties** → **Environment Variables**
2. Add new **System Variable**:
   - **Name**: `GRADLE_USER_HOME`
   - **Value**: `C:\gradle-cache`

---

## 📱 Android Studio Project Setup

### Step 1: Import Project
1. **File** → **Open**
2. Navigate to your `ADDD` folder
3. Select the **root folder** (not the app folder)
4. Click **OK**

### Step 2: Configure SDK
1. **File** → **Project Structure**
2. **Project Settings** → **Project**
3. **Project SDK**: Select **Android API 34**
4. **Project language level**: Select **8 - Lambdas, type annotations etc.**

### Step 3: Sync Project
1. Click **"Sync Now"** when prompted
2. Or **File** → **Sync Project with Gradle Files**

---

## 🏃‍♂️ Running the App

### Step 1: Connect Device
1. Enable **Developer Options** on your Android device
2. Enable **USB Debugging**
3. Connect via USB
4. Allow USB debugging when prompted

### Step 2: Select Device
1. Click the device dropdown in Android Studio toolbar
2. Select your connected device

### Step 3: Run App
1. Click the **Green Play Button** ▶️
2. Or **Run** → **Run 'app'**
3. Or use keyboard shortcut **Shift + F10**

---

## 🐛 Troubleshooting

### If "Sync Failed"
```bash
# In Android Studio Terminal:
./gradlew clean
./gradlew build
```

### If "Device Not Detected"
1. **Tools** → **SDK Manager**
2. **SDK Tools** tab
3. Install **Google USB Driver**
4. Restart Android Studio

### If "Permission Denied" on gradlew
```bash
# In Android Studio Terminal (Windows):
git update-index --chmod=+x gradlew
# Or simply:
chmod +x gradlew
```

### If Build Fails
1. **File** → **Invalidate Caches and Restart**
2. Select **"Invalidate and Restart"**

---

## ⚡ Quick Commands

### Android Studio Terminal Commands
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Uninstall from device
./gradlew uninstallDebug

# Run tests
./gradlew test

# Check for lint issues
./gradlew lint
```

---

## 📋 Project Configuration Verification

### Verify build.gradle (Module: app)
```kotlin
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.smartdrawer.app"
        minSdk 23
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
}
```

### Verify gradle.properties
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
android.useAndroidX=true
kotlin.code.style=official
```

---

## 🎯 Expected Result

After following these steps:
1. ✅ Android Studio opens the project without errors
2. ✅ Gradle sync completes successfully  
3. ✅ You can run the app on your connected device
4. ✅ App installs and runs with USB debugging
5. ✅ You can see logs in Android Studio's Logcat

---

## 📞 Alternative: Command Line Testing

If Android Studio still has issues:

```batch
# Build and install directly via command line
cd F:\ADDD
gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk

# Run the app
adb shell am start -n com.smartdrawer.app.debug/com.smartdrawer.app.MainActivity
```

---

**Note**: The USB debugging method is perfect for testing as it provides real-time logs and debugging capabilities! 