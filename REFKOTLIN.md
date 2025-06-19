# REFKOTLIN - Android Kotlin Development Reference Guide

## 🎯 Overview
This reference guide documents common issues, solutions, and best practices encountered during Android Kotlin app development, specifically learned from the Smart Drawer project. Use this to avoid repeating mistakes and accelerate development.

---

## 📋 Table of Contents
1. [Project Setup Issues](#project-setup-issues)
2. [Gradle Configuration Problems](#gradle-configuration-problems)
3. [GitHub Actions CI/CD Issues](#github-actions-cicd-issues)
4. [Android Lint Errors](#android-lint-errors)
5. [Resource Compilation Errors](#resource-compilation-errors)
6. [Best Practices](#best-practices)
7. [Project Structure Template](#project-structure-template)

---

## 🛠️ Project Setup Issues

### Issue 1: Deprecated GitHub Actions
**Problem**: Using outdated GitHub Actions versions
```yaml
# ❌ WRONG - Deprecated versions
uses: actions/upload-artifact@v3
uses: actions/cache@v3
```

**Solution**: Always use latest stable versions
```yaml
# ✅ CORRECT - Latest versions
uses: actions/upload-artifact@v4
uses: actions/cache@v4
```

### Issue 2: Missing Permissions for Release Creation
**Problem**: GitHub Actions can't create releases
```
Error 403: Resource not accessible by integration
```

**Solution**: Add proper permissions to workflow
```yaml
# At workflow level
permissions:
  contents: write
  packages: write

# At job level for release
release:
  permissions:
    contents: write
    packages: write
```

---

## ⚙️ Gradle Configuration Problems

### Issue 1: Repository Configuration Conflict
**Problem**: Build fails with repository preference error
```
Build was configured to prefer settings repositories over project repositories
```

**Solution**: Use repositories only in `settings.gradle`, remove from root `build.gradle`
```kotlin
// ✅ settings.gradle - CORRECT
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// ❌ build.gradle (root) - REMOVE THIS
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

### Issue 2: Boolean Property Parsing Error
**Problem**: Trailing spaces in gradle.properties
```
Cannot parse project property android.nonTransitiveRClass='true ' as boolean
```

**Solution**: Remove trailing spaces or remove optional properties
```properties
# ❌ WRONG - Has trailing space
android.nonTransitiveRClass=true 

# ✅ CORRECT - No trailing space
android.nonTransitiveRClass=true

# ✅ ALTERNATIVE - Remove if optional
# android.nonTransitiveRClass=true
```

---

## 🚀 GitHub Actions CI/CD Issues

### Issue 1: Workflow File Template
**Complete working workflow with all fixes applied:**

```yaml
name: Android CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

permissions:
  contents: write
  packages: write

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v4
      
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
          
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Clean project
      run: ./gradlew clean
      
    - name: Run lint
      run: ./gradlew lint
      
    - name: Run unit tests
      run: ./gradlew test
      
    - name: Build debug APK
      run: ./gradlew assembleDebug
      
    - name: Build release APK
      run: ./gradlew assembleRelease

  release:
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'
    permissions:
      contents: write
      packages: write
    
    steps:
    - name: Create Release
      uses: ncipollo/release-action@v1
      with:
        tag: v${{ steps.version.outputs.version }}
        artifacts: app/build/outputs/apk/release/app-release.apk
        token: ${{ secrets.GITHUB_TOKEN }}
```

---

## 🔍 Android Lint Errors

### Issue 1: API Level Compatibility
**Problem**: Using newer APIs without version checks
```kotlin
// ❌ WRONG - Requires API 26+
context.startForegroundService(serviceIntent)
```

**Solution**: Add proper API level checks
```kotlin
// ✅ CORRECT - Backward compatible
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    context.startForegroundService(serviceIntent)
} else {
    context.startService(serviceIntent)
}
```

### Issue 2: Deprecated Methods
**Problem**: Using deprecated `onBackPressed()`
```kotlin
// ❌ WRONG - Deprecated
override fun onBackPressed() {
    super.onBackPressed()
}
```

**Solution**: Use modern OnBackPressedDispatcher
```kotlin
// ✅ CORRECT - Modern approach
private fun setupBackPressedCallback() {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    })
}
```

### Issue 3: Tint Attribute Issues
**Problem**: Using `android:tint` in ImageViews
```xml
<!-- ❌ WRONG - Lint error UseAppTint -->
<ImageView
    android:tint="?attr/colorOnSurfaceVariant" />
```

**Solution**: Use `app:tint` for ImageViews
```xml
<!-- ✅ CORRECT - AppCompat compatible -->
<ImageView
    app:tint="?attr/colorOnSurfaceVariant" />
```

### Issue 4: Unused Parameter Warnings
**Problem**: Lint warns about unused parameters
```kotlin
// ❌ WRONG - 'app' parameter unused
onAppLongClick = { app ->
    // TODO: Show context menu
}
```

**Solution**: Use underscore for intentionally unused parameters
```kotlin
// ✅ CORRECT - Indicates intentionally unused
onAppLongClick = { _ ->
    // TODO: Show context menu
}
```

---

## 🖼️ Resource Compilation Errors

### Issue 1: Invalid PNG Files
**Problem**: AAPT fails to compile corrupted PNG files
```
ERROR: /path/to/ic_launcher.png: AAPT: error: file failed to compile
```

**Solution**: Use vector drawables and adaptive icons instead
```xml
<!-- mipmap-anydpi-v26/ic_launcher.xml -->
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

**Benefits of Vector Icons:**
- Smaller APK size
- Perfect scaling on all densities
- No PNG corruption issues
- Modern adaptive icon support

---

## 📝 Best Practices

### 1. Project Configuration
```kotlin
// app/build.gradle - Essential configurations
android {
    compileSdk 34
    
    defaultConfig {
        minSdk 23
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
    }
    
    buildFeatures {
        viewBinding true
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

### 2. Dependency Management
```kotlin
dependencies {
    // Use stable versions
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.10.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
}
```

### 3. Manifest Permissions
```xml
<!-- Essential permissions for overlay apps -->
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### 4. Service Implementation
```kotlin
// Proper foreground service implementation
class FloatingWidgetService : Service() {
    
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForeground(NOTIFICATION_ID, createNotification())
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Widget Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
```

---

## 🏗️ Project Structure Template

```
app/
├── src/main/
│   ├── java/com/yourpackage/app/
│   │   ├── adapter/          # RecyclerView adapters
│   │   ├── model/           # Data models
│   │   ├── overlay/         # Overlay implementations
│   │   ├── receiver/        # Broadcast receivers
│   │   ├── service/         # Background services
│   │   ├── utils/           # Utility classes
│   │   ├── viewmodel/       # ViewModels (MVVM)
│   │   ├── MainActivity.kt
│   │   └── SettingsActivity.kt
│   ├── res/
│   │   ├── drawable/        # Vector drawables only
│   │   ├── layout/          # XML layouts
│   │   ├── mipmap-anydpi-v26/ # Adaptive icons
│   │   ├── values/          # Strings, colors, themes
│   │   └── xml/             # Backup rules, etc.
│   └── AndroidManifest.xml
├── build.gradle             # App-level build config
└── proguard-rules.pro       # Proguard rules
```

---

## 🚨 Common Pitfalls to Avoid

1. **Never commit placeholder/empty PNG files** - Use vector drawables
2. **Always add API level checks** for newer Android features
3. **Use app: namespace** for AppCompat attributes in layouts
4. **Set proper GitHub Actions permissions** for releases
5. **Remove trailing spaces** from gradle.properties
6. **Use repositories only in settings.gradle** for modern Gradle
7. **Implement proper foreground service** with notification channels
8. **Add proper error handling** for overlay permissions
9. **Use ViewBinding instead of findViewById** for better performance
10. **Test on multiple Android versions** (min SDK to target SDK)

---

## 📊 Performance Optimization

### APK Size Optimization
```kotlin
// Proguard rules for smaller APK
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### Memory Management
```kotlin
// Proper lifecycle management
override fun onDestroy() {
    super.onDestroy()
    // Clean up resources
    binding = null
    serviceIntent = null
}
```

---

## 🔗 Useful Commands

```bash
# Clean and build
./gradlew clean assembleDebug

# Run lint checks
./gradlew lint

# Run tests
./gradlew test

# Build release APK
./gradlew assembleRelease

# Check for dependency updates
./gradlew dependencyUpdates
```

---

## 📚 Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Android Extensions](https://kotlinlang.org/docs/android-overview.html)
- [Material Design Guidelines](https://material.io/design)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

---

**Created**: Based on Smart Drawer Android project development experience  
**Last Updated**: 2024  
**Author**: Generated from real project issues and solutions 