# Smart Drawer

A lightweight Android native app written in Kotlin that provides a smart app launcher with floating widget and swipe-up gesture functionality.

## Features

- **Floating Draggable Widget**: A persistent floating search widget that stays on top of all apps
- **Swipe-up App Drawer**: Full-screen overlay showing all installed apps with search functionality
- **Smart Search**: Real-time filtering of apps as you type
- **Settings Management**: Customizable preferences for widget behavior and appearance
- **Auto-start on Boot**: Optional automatic startup when device boots
- **Dark Mode Support**: Toggle between light and dark themes
- **Grid/List Layout**: Choose between grid or list view for apps
- **Minimal APK Size**: Optimized for size and performance (target < 2.5MB)

## Technical Specifications

- **Language**: Kotlin 100%
- **UI Framework**: View-based UI with ViewBinding (no Compose for size optimization)
- **Min SDK**: API 23 (Android 6.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVVM with LiveData and ViewModel
- **Dependencies**: Minimal - only essential AndroidX libraries

## Required Permissions

- `SYSTEM_ALERT_WINDOW`: For floating widget and overlay functionality
- `QUERY_ALL_PACKAGES`: To access list of installed apps (Android 11+)
- `FOREGROUND_SERVICE`: For persistent floating widget service
- `RECEIVE_BOOT_COMPLETED`: For auto-start functionality
- `VIBRATE`: For haptic feedback

## Building the Project

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 17
- Android SDK with API 34

### Local Build (Not Recommended - Use GitHub Actions Instead)

```bash
# Clone the repository
git clone https://github.com/yamodiji/ADDD.git
cd ADDD

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

### CI/CD Build (Recommended)

This project uses GitHub Actions for automated builds. Every push and pull request triggers:

- Lint checks
- Unit tests
- Debug and release APK builds
- Artifact uploads

**To build the APK:**
1. Push your code to the main branch
2. Go to Actions tab in GitHub
3. Wait for the build to complete
4. Download APK artifacts from the completed workflow

## Installation

1. Download the APK from the GitHub Actions artifacts
2. Enable "Install from unknown sources" in Android settings
3. Install the APK
4. Grant overlay permission when prompted
5. Configure settings as desired

## Usage

1. **Start the App**: Open Smart Drawer and grant required permissions
2. **Enable Widget**: Toggle the floating widget in settings or main screen
3. **Use Floating Widget**: Tap the search icon to open app drawer, settings icon for preferences
4. **Search Apps**: Type in the search bar to filter apps in real-time
5. **Launch Apps**: Tap any app icon to launch it
6. **Customize**: Access settings to enable/disable features and change appearance

## License

This project is open source and available under the MIT License.

---

**Smart Drawer** - Making app launching smarter and faster on Android.