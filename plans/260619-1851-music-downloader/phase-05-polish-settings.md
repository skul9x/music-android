# Phase 05: Polish, Settings & Update Engine

Status: ✅ Completed  
Dependencies: Phase 04 (Foreground Service)

## Objective

Add the yt-dlp engine update feature, a settings screen for configuring download preferences, clipboard auto-detection, download history, and final UI polish (animations, transitions, edge cases). This phase brings the app to MVP-complete quality.

## Requirements

### Functional
- [ ] "Update Engine" button that calls `YoutubeDL.getInstance().updateYoutubeDL(context)`.
- [ ] Settings screen: choose default download folder, select default format, toggle clipboard auto-paste.
- [ ] Persist settings using `DataStore<Preferences>` or `SharedPreferences`.
- [ ] Auto-paste: detect YouTube/TikTok URLs on clipboard when app opens, prompt user.
- [ ] Download history: store past downloads in a simple local list (in-memory or Room for persistence).
- [ ] Edge case handling: no network, storage full, invalid URL, playlist URLs blocked.
- [ ] App icon and splash screen.

### Non-Functional
- [ ] Smooth animations: fade-in for cards, slide-in for progress section, scale animation for buttons.
- [ ] Haptic feedback on download start and completion.
- [ ] Material 3 snackbar for transient errors and status messages.
- [ ] Accessible: content descriptions on all icons and buttons.

## Implementation Steps

### 1. Create Update Engine Logic
- [ ] `UpdateManager` class: wraps `YoutubeDL.getInstance().updateYoutubeDL(context, updateChannel)`.
- [ ] Returns update status: `AlreadyUpToDate`, `Updated(version)`, `Error(message)`.
- [ ] Expose as suspend function for coroutine usage.

**File:** `app/src/main/java/com/musicdownloader/app/data/repository/UpdateManager.kt`

### 2. Create Settings Data Store
- [ ] `SettingsRepository` class: manages preferences using `SharedPreferences` or `DataStore`.
- [ ] Keys: `default_save_path`, `default_format`, `auto_paste_enabled`, `engine_last_updated`.
- [ ] Expose settings as `Flow<Settings>` for reactive UI updates.

**File:** `app/src/main/java/com/musicdownloader/app/data/repository/SettingsRepository.kt`  
**File:** `app/src/main/java/com/musicdownloader/app/data/models/Settings.kt`

### 3. Create Settings Screen
- [ ] `SettingsScreen` composable with Material 3 list items:
  - Download folder selector (opens SAF picker).
  - Default format selector (dropdown or radio).
  - Auto-paste toggle switch.
  - "Update yt-dlp Engine" button with loading state.
  - Engine version display.
  - App version display.

**File:** `app/src/main/java/com/musicdownloader/app/ui/screens/SettingsScreen.kt`

### 4. Create Settings ViewModel
- [ ] `SettingsViewModel`: reads/writes settings, triggers engine update.
- [ ] Exposes `settingsState: StateFlow<Settings>` and `updateState: StateFlow<UpdateStatus>`.

**File:** `app/src/main/java/com/musicdownloader/app/ui/viewmodel/SettingsViewModel.kt`

### 5. Implement Clipboard Auto-Paste
- [ ] In `MainScreen`, on `LaunchedEffect(Unit)` (screen first composition):
  - Read clipboard content.
  - If it matches a supported URL pattern and auto-paste is enabled:
    - Show a dismissible banner: "Detected URL: [url]. Use it?"
    - On confirm: populate URL field and auto-fetch info.

**Modify:** `app/src/main/java/com/musicdownloader/app/ui/screens/MainScreen.kt`

### 6. Add Navigation
- [ ] Simple two-screen navigation: Main ↔ Settings.
- [ ] Use Compose Navigation (`NavHost`, `NavController`) or simple state-based navigation.
- [ ] Settings icon button in the top bar of MainScreen.

**Modify:** `app/src/main/java/com/musicdownloader/app/MainActivity.kt`  
**File:** `app/src/main/java/com/musicdownloader/app/ui/navigation/AppNavigation.kt`

### 7. Add Download History (Optional/Simple)
- [ ] `DownloadHistoryItem` data class: `title`, `filePath`, `format`, `timestamp`, `thumbnailUrl`.
- [ ] Store in an in-memory list within a `HistoryRepository` singleton (can be upgraded to Room later).
- [ ] Display in a collapsible section at the bottom of MainScreen.

**File:** `app/src/main/java/com/musicdownloader/app/data/repository/HistoryRepository.kt`  
**File:** `app/src/main/java/com/musicdownloader/app/data/models/DownloadHistoryItem.kt`

### 8. Final UI Polish
- [ ] Add `AnimatedVisibility` with `fadeIn`/`fadeOut` + `expandVertically`/`shrinkVertically` to all state transitions.
- [ ] Add `Modifier.animateContentSize()` to cards that change content.
- [ ] Add subtle `scale` animation on button press using `Modifier.clickable` with `interactionSource`.
- [ ] Ensure dark theme colors are rich (not pure black — use dark grays with slight color tint).

**Modify:** Multiple UI files.

### 9. Create App Icon
- [ ] Design or use a simple music note + download arrow icon.
- [ ] Add adaptive icon resources: `mipmap-anydpi-v26/ic_launcher.xml`.

**Files:** `app/src/main/res/mipmap-*/ic_launcher.png`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`

### 10. Add ProGuard/R8 Rules (for release build)
- [ ] Keep rules for youtubedl-android native classes.
- [ ] Keep rules for Kotlin serialization if used.

**File:** `app/proguard-rules.pro`

### 11. Error Handling Refinements
- [ ] Network check before starting download: show "No internet connection" error.
- [ ] Playlist URL detection: if URL contains `&list=` or `/playlist`, show warning and strip playlist parameter.
- [ ] Storage space check before download: warn if < 100MB free.

**Modify:** `app/src/main/java/com/musicdownloader/app/ui/viewmodel/DownloadViewModel.kt`  
**File:** `app/src/main/java/com/musicdownloader/app/util/NetworkHelper.kt`

## Files to Create/Modify

| File | Purpose |
|------|---------|
| `app/src/main/java/.../data/repository/UpdateManager.kt` | yt-dlp engine updater |
| `app/src/main/java/.../data/repository/SettingsRepository.kt` | Persistent settings |
| `app/src/main/java/.../data/models/Settings.kt` | Settings data class |
| `app/src/main/java/.../data/repository/HistoryRepository.kt` | Download history |
| `app/src/main/java/.../data/models/DownloadHistoryItem.kt` | History item model |
| `app/src/main/java/.../ui/screens/SettingsScreen.kt` | Settings UI |
| `app/src/main/java/.../ui/viewmodel/SettingsViewModel.kt` | Settings logic |
| `app/src/main/java/.../ui/navigation/AppNavigation.kt` | Screen navigation |
| `app/src/main/java/.../util/NetworkHelper.kt` | Network connectivity check |
| `app/proguard-rules.pro` | ProGuard keep rules |
| Multiple existing UI files | (MODIFY) Animation polish |

## Test Criteria (Phase 05 Verification)

### Test 1: Settings Data Model Tests

**File:** `app/src/test/java/com/musicdownloader/app/data/models/SettingsTest.kt`

```kotlin
package com.musicdownloader.app.data.models

import org.junit.Assert.*
import org.junit.Test

class SettingsTest {

    @Test
    fun `default settings have sensible values`() {
        val settings = Settings()
        assertTrue(settings.defaultSavePath.isNotBlank())
        assertEquals(DownloadFormat.M4A_AUDIO, settings.defaultFormat)
        assertTrue(settings.autoPasteEnabled)
    }

    @Test
    fun `settings can be copied with modifications`() {
        val original = Settings()
        val modified = original.copy(autoPasteEnabled = false)
        assertFalse(modified.autoPasteEnabled)
        assertTrue(original.autoPasteEnabled)
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.data.models.SettingsTest"
```
**Expected:** 2 tests passed, 0 failures.

### Test 2: History Repository Tests

**File:** `app/src/test/java/com/musicdownloader/app/data/repository/HistoryRepositoryTest.kt`

```kotlin
package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HistoryRepositoryTest {

    private lateinit var repo: HistoryRepository

    @Before
    fun setup() {
        repo = HistoryRepository()
    }

    @Test
    fun `initially history is empty`() {
        assertTrue(repo.getHistory().isEmpty())
    }

    @Test
    fun `addItem increases history size`() {
        repo.addItem(
            DownloadHistoryItem(
                title = "Test Song",
                filePath = "/downloads/test.m4a",
                format = "M4A",
                timestamp = System.currentTimeMillis(),
                thumbnailUrl = "https://example.com/thumb.jpg"
            )
        )
        assertEquals(1, repo.getHistory().size)
    }

    @Test
    fun `history items are in reverse chronological order`() {
        repo.addItem(DownloadHistoryItem("First", "/a", "M4A", 1000, ""))
        repo.addItem(DownloadHistoryItem("Second", "/b", "M4A", 2000, ""))

        val history = repo.getHistory()
        assertEquals("Second", history[0].title)
        assertEquals("First", history[1].title)
    }

    @Test
    fun `clearHistory empties the list`() {
        repo.addItem(DownloadHistoryItem("Song", "/a", "M4A", 1000, ""))
        repo.clearHistory()
        assertTrue(repo.getHistory().isEmpty())
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.data.repository.HistoryRepositoryTest"
```
**Expected:** 4 tests passed, 0 failures.

### Test 3: Network Helper Tests

**File:** `app/src/test/java/com/musicdownloader/app/util/NetworkHelperTest.kt`

```kotlin
package com.musicdownloader.app.util

import org.junit.Assert.*
import org.junit.Test

class NetworkHelperTest {

    @Test
    fun `stripPlaylistParam removes list parameter from URL`() {
        val url = "https://www.youtube.com/watch?v=abc123&list=PLxyz"
        val cleaned = NetworkHelper.stripPlaylistParam(url)
        assertEquals("https://www.youtube.com/watch?v=abc123", cleaned)
    }

    @Test
    fun `stripPlaylistParam leaves clean URL unchanged`() {
        val url = "https://www.youtube.com/watch?v=abc123"
        val cleaned = NetworkHelper.stripPlaylistParam(url)
        assertEquals(url, cleaned)
    }

    @Test
    fun `isPlaylistUrl detects playlist URLs`() {
        assertTrue(NetworkHelper.isPlaylistUrl("https://youtube.com/playlist?list=PLxyz"))
        assertTrue(NetworkHelper.isPlaylistUrl("https://youtube.com/watch?v=abc&list=PLxyz"))
    }

    @Test
    fun `isPlaylistUrl returns false for single video URLs`() {
        assertFalse(NetworkHelper.isPlaylistUrl("https://youtube.com/watch?v=abc123"))
        assertFalse(NetworkHelper.isPlaylistUrl("https://youtu.be/abc123"))
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.util.NetworkHelperTest"
```
**Expected:** 4 tests passed, 0 failures.

### Test 4: Settings Screen Compose UI Test (Instrumented)

**File:** `app/src/androidTest/java/com/musicdownloader/app/ui/SettingsScreenTest.kt`

```kotlin
package com.musicdownloader.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.musicdownloader.app.ui.screens.SettingsScreen
import com.musicdownloader.app.ui.theme.MusicDownloaderTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysUpdateEngineButton() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }
        composeTestRule.onNodeWithTag("update_engine_button").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysAutopasteToggle() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }
        composeTestRule.onNodeWithTag("autopaste_toggle").assertIsDisplayed()
    }

    @Test
    fun settingsScreen_displaysDefaultFormatSelector() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                SettingsScreen(onNavigateBack = {})
            }
        }
        composeTestRule.onNodeWithTag("default_format_selector").assertIsDisplayed()
    }
}
```

Run:
```bash
./gradlew connectedDebugAndroidTest --tests "com.musicdownloader.app.ui.SettingsScreenTest"
```
**Expected:** 3 tests passed on connected device/emulator.

### Test 5: Complete Final Test Suite
```bash
./gradlew testDebugUnitTest
```
**Expected:** All unit tests from all phases pass (minimum 40+ tests, 0 failures).

```bash
./gradlew connectedDebugAndroidTest
```
**Expected:** All instrumented tests pass (minimum 9 tests, 0 failures).

### Test 6: Release Build Verification
```bash
./gradlew assembleRelease
```
**Expected:** BUILD SUCCESSFUL. Release APK is generated (may require signing config or `--debug` signing).

### Test 7: Manual Smoke Test Checklist
- [ ] App launches without crash on Android 8.0+ device/emulator.
- [ ] Paste a YouTube URL → fetch info → see thumbnail, title, duration.
- [ ] Select M4A → download → see progress → file saved in chosen folder.
- [ ] Select 1080p → download → video plays correctly.
- [ ] Press cancel during download → download stops.
- [ ] Navigate to Settings → change default format → go back → format is persisted.
- [ ] Press "Update Engine" → shows loading → shows success/already up-to-date.
- [ ] Put app in background during download → notification shows progress.
- [ ] Download completes in background → notification shows "Complete".
- [ ] Re-open app → UI shows download completed state.

## Notes

- The `UpdateManager` must run on `Dispatchers.IO` because `YoutubeDL.updateYoutubeDL()` is a blocking network call.
- On first launch, `YoutubeDL.init()` unpacks the Python runtime (~60MB) into the app's internal storage. This takes several seconds. Show a splash/loading indicator.
- For release builds, consider adding ABI splits to reduce APK size per architecture.
- Download history is in-memory for MVP. Upgrade to Room database in a future phase if persistence across app restarts is needed.

---
**🎉 MVP Complete!**

After this phase, the Music Downloader app has:
- Full download functionality (M4A with cover art, video 1080p/720p)
- Glassmorphism UI with smooth animations
- Background download service with notifications
- Engine self-update capability
- Settings persistence
- Comprehensive test coverage (40+ unit tests, 9+ instrumented tests)
