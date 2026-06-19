# Phase 01: Project Setup & Gradle Configuration

Status: 🟩 Completed  
Dependencies: None

## Objective

Bootstrap a fully buildable Android project from scratch using Gradle Kotlin DSL, Version Catalog, Jetpack Compose, Material 3, and the `youtubedl-android` library. The project must compile and launch an empty Material 3 screen on a device/emulator.

## Requirements

### Functional
- [x] Create a standard single-module Android Gradle project structure.
- [x] Configure Version Catalog (`libs.versions.toml`) for all dependencies.
- [x] Add Jetpack Compose + Material 3 dependencies via BOM.
- [x] Add `youtubedl-android` (library + ffmpeg) dependencies from Maven Central.
- [x] Create `MainActivity` with a minimal Compose scaffold.
- [x] Create `MusicDownloaderApp` Application class that initializes `YoutubeDL`.

### Non-Functional
- [x] Min SDK: 26 (Android 8.0)
- [x] Target SDK: 35
- [x] Kotlin 2.0+
- [x] `extractNativeLibs = true` in manifest (required by youtubedl-android).
- [x] ABI filters: `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`.

## Implementation Steps

### 1. Create Root Gradle Files
- [x] `settings.gradle.kts` — Define project name, include `:app` module, configure repositories.
- [x] `build.gradle.kts` (root) — Declare plugins (Android Application, Kotlin Android, Compose Compiler) with `apply false`.
- [x] `gradle.properties` — Enable AndroidX, Compose, non-transitive R classes.

### 2. Create Version Catalog
- [x] `gradle/libs.versions.toml` — All versions, libraries, bundles, and plugins in one place.

Versions to define:
```
kotlin = "2.0.21"
agp = "8.7.3"
composeBom = "2024.12.01"
youtubedlAndroid = "0.18.1"
coil = "2.7.0"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.9.3"
coreKtx = "1.15.0"
junit = "4.13.2"
junitExt = "1.2.1"
espressoCore = "3.6.1"
```

### 3. Create App Module Build File
- [x] `app/build.gradle.kts` — Apply plugins, configure `android {}` block (compileSdk, minSdk, targetSdk, ABI filters, compose buildFeature), and declare all dependencies via `libs.*` accessors.

### 4. Create Android Manifest
- [x] `app/src/main/AndroidManifest.xml` — Declare permissions (`INTERNET`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `POST_NOTIFICATIONS`), application class, activity, and `extractNativeLibs="true"`.

### 5. Create Application Class
- [x] `app/src/main/java/com/musicdownloader/app/MusicDownloaderApp.kt` — Extends `Application`, initializes `YoutubeDL.getInstance().init(this)` and `FFmpeg.getInstance().init(this)` in `onCreate()`.

### 6. Create MainActivity
- [x] `app/src/main/java/com/musicdownloader/app/MainActivity.kt` — Extends `ComponentActivity`, sets content to a minimal Compose scaffold with Material 3 theme.

### 7. Create Theme
- [x] `app/src/main/java/com/musicdownloader/app/ui/theme/Color.kt` — Define custom color palette (dark + light).
- [x] `app/src/main/java/com/musicdownloader/app/ui/theme/Type.kt` — Define typography using Google Fonts (Inter/Outfit).
- [x] `app/src/main/java/com/musicdownloader/app/ui/theme/Theme.kt` — Create `MusicDownloaderTheme` composable with dynamic color support.

### 8. Create Resources
- [x] `app/src/main/res/values/strings.xml` — App name and basic strings.
- [x] `app/src/main/res/values/themes.xml` — Splash/launcher theme fallback.
- [x] `app/src/main/res/mipmap-*` — App launcher icons (use default or generate later).

### 9. Create Gradle Wrapper
- [x] `gradle/wrapper/gradle-wrapper.properties` — Pin Gradle version (`8.9`).
- [x] `gradlew` / `gradlew.bat` — Gradle wrapper scripts.

## Files to Create/Modify

| File | Purpose |
|------|---------|
| `settings.gradle.kts` | Project settings, module includes, repository config |
| `build.gradle.kts` | Root-level plugin declarations |
| `gradle.properties` | Gradle/AndroidX flags |
| `gradle/libs.versions.toml` | Centralized dependency management |
| `app/build.gradle.kts` | App module build config + dependencies |
| `app/src/main/AndroidManifest.xml` | Permissions, Application, Activity |
| `app/src/main/java/com/musicdownloader/app/MusicDownloaderApp.kt` | Application class |
| `app/src/main/java/com/musicdownloader/app/MainActivity.kt` | Entry Activity |
| `app/src/main/java/com/musicdownloader/app/ui/theme/Color.kt` | Color definitions |
| `app/src/main/java/com/musicdownloader/app/ui/theme/Type.kt` | Typography |
| `app/src/main/java/com/musicdownloader/app/ui/theme/Theme.kt` | Theme composable |
| `app/src/main/res/values/strings.xml` | String resources |
| `app/src/main/res/values/themes.xml` | XML theme fallback |

## Test Criteria (Phase 01 Verification)

After completing this phase, run the following checks:

### Test 1: Gradle Sync & Build
```bash
cd /home/skul9x/Desktop/Test_code/Tiktok-Downloader
./gradlew assembleDebug
```
**Expected:** BUILD SUCCESSFUL. APK generated at `app/build/outputs/apk/debug/app-debug.apk`.

### Test 2: Lint Check
```bash
./gradlew lintDebug
```
**Expected:** No critical errors. Warnings are acceptable.

### Test 3: Unit Test Scaffold
Create a minimal unit test to verify the project test infrastructure works.

**File:** `app/src/test/java/com/musicdownloader/app/SanityTest.kt`
```kotlin
package com.musicdownloader.app

import org.junit.Assert.assertTrue
import org.junit.Test

class SanityTest {
    @Test
    fun `project compiles and tests run`() {
        assertTrue("Sanity check passed", true)
    }
}
```

```bash
./gradlew testDebugUnitTest
```
**Expected:** 1 test passed, 0 failures.

### Test 4: Version Catalog Validation
Verify all dependency accessors resolve correctly by checking Gradle sync does not produce unresolved reference errors.

**File:** `app/src/test/java/com/musicdownloader/app/DependencyCheckTest.kt`
```kotlin
package com.musicdownloader.app

import org.junit.Assert.assertNotNull
import org.junit.Test

class DependencyCheckTest {
    @Test
    fun `youtubedl classes are on classpath`() {
        val clazz = Class.forName("com.yausername.youtubedl_android.YoutubeDL")
        assertNotNull("YoutubeDL class should be resolvable", clazz)
    }

    @Test
    fun `compose material3 classes are on classpath`() {
        val clazz = Class.forName("androidx.compose.material3.MaterialTheme")
        assertNotNull("Material3 MaterialTheme class should be resolvable", clazz)
    }
}
```

```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.DependencyCheckTest"
```
**Expected:** 2 tests passed, confirming both youtubedl-android and Material 3 are correctly linked.

### Test 5: APK Contains Native Libraries
```bash
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -E "\.so$" | head -20
```
**Expected:** Output shows `.so` files for `arm64-v8a`, `armeabi-v7a`, etc. (from youtubedl-android native binaries).

## Notes

- The `youtubedl-android` library bundles Python + yt-dlp as native libraries. The debug APK will be ~60-80MB. This is expected.
- When building a release, configure ABI splits to reduce per-device download size to ~20-25MB.
- Do NOT run `npm install` or any Node.js tooling. This is a pure Android/Gradle project.

---
Next Phase: [phase-02-download-engine.md](./phase-02-download-engine.md)
