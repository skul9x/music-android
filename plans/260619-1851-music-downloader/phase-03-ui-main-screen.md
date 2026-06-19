# Phase 03: UI — Main Screen (Glassmorphism)

Status: ✅ Completed  
Dependencies: Phase 02 (Download Engine)

## Objective

Build the main screen UI using Jetpack Compose and Material 3 with a Glassmorphism-inspired design. The screen includes a URL input field, format selector, download button, video info preview card, and a real-time progress indicator. Wire the UI to the `DownloadViewModel` created in Phase 02.

## Requirements

### Functional
- [x] URL input field with paste-from-clipboard button.
- [x] "Fetch Info" button to load video metadata.
- [x] Video info card showing: thumbnail, title, uploader, duration.
- [x] Format selector chips: M4A Audio, Video 1080p, Video 720p.
- [x] Download button that starts the download.
- [x] Cancel button visible during active download.
- [x] Linear progress bar showing percentage + speed text.
- [x] Success state: show file path and "Open Folder" button.
- [x] Error state: show error message with "Retry" button.
- [x] Snackbar for transient messages.

### Non-Functional
- [x] Glassmorphism effect: frosted-glass cards with `Brush.linearGradient` + semi-transparent backgrounds + border.
- [x] Smooth state transitions using `AnimatedVisibility` and `animateContentSize`.
- [x] Dark theme by default (with light theme support via Material 3 dynamic color).
- [x] Typography: use Google Fonts (Inter or Outfit).
- [x] All interactive elements have unique `testTag` modifiers for UI testing.

## Implementation Steps

### 1. Create Glassmorphism Components
- [x] `GlassCard` composable: a reusable card with frosted glass effect (semi-transparent background, subtle border, rounded corners, slight blur overlay).
- [x] `GlassButton` composable: primary action button with gradient background and press animation.

**File:** `app/src/main/java/com/musicdownloader/app/ui/components/GlassComponents.kt`

### 2. Create URL Input Section
- [x] `UrlInputSection` composable: `OutlinedTextField` styled with glass effect + trailing icon button to paste from clipboard.
- [x] `FetchInfoButton` composable: styled button that triggers `viewModel.fetchInfo(url)`.

**File:** `app/src/main/java/com/musicdownloader/app/ui/components/UrlInputSection.kt`

### 3. Create Video Info Card
- [x] `VideoInfoCard` composable: displays thumbnail (loaded via Coil), title, uploader, duration in a `GlassCard`.
- [x] Duration formatted as `MM:SS`.
- [x] Thumbnail with rounded corners and subtle shadow.

**File:** `app/src/main/java/com/musicdownloader/app/ui/components/VideoInfoCard.kt`

### 4. Create Format Selector
- [x] `FormatSelector` composable: row of `FilterChip` components for M4A, 1080p, 720p.
- [x] Selected chip is highlighted with accent color.
- [x] Each chip shows an icon (music note, HD badge, etc.).

**File:** `app/src/main/java/com/musicdownloader/app/ui/components/FormatSelector.kt`

### 5. Create Download Progress Section
- [x] `DownloadProgressSection` composable: `LinearProgressIndicator` + percentage text + speed text + ETA text.
- [x] Cancel button (icon button with X).
- [x] Animated visibility: slides in when downloading, slides out when done.

**File:** `app/src/main/java/com/musicdownloader/app/ui/components/DownloadProgressSection.kt`

### 6. Create Success/Error States
- [x] `DownloadSuccessCard` composable: shows checkmark icon, file path, "Open Folder" button.
- [x] `DownloadErrorCard` composable: shows error icon, error message, "Retry" button.

**File:** `app/src/main/java/com/musicdownloader/app/ui/components/DownloadResultCards.kt`

### 7. Assemble Main Screen
- [x] `MainScreen` composable: orchestrates all above components.
- [x] Observes `viewModel.uiState` via `collectAsStateWithLifecycle()`.
- [x] Background: animated gradient (dark purple → deep blue → dark teal) slowly shifting.
- [x] Top bar: app title "Music Downloader" with subtle glow text effect.
- [x] Content arranged in a `LazyColumn` or `Column` with `verticalScroll`.

**File:** `app/src/main/java/com/musicdownloader/app/ui/screens/MainScreen.kt`

### 8. Update MainActivity
- [x] Wire `MainScreen` into `setContent {}`.
- [x] Pass `DownloadViewModel` (using `viewModel()` factory or manual creation).

**Modify:** `app/src/main/java/com/musicdownloader/app/MainActivity.kt`

### 9. Add Clipboard Utility
- [x] `ClipboardHelper` object: `getClipboardText(context: Context): String?`.

**File:** `app/src/main/java/com/musicdownloader/app/util/ClipboardHelper.kt`

### 10. Add Duration Formatter
- [x] `formatDuration(seconds: Long): String` — converts seconds to `HH:MM:SS` or `MM:SS`.

**File:** `app/src/main/java/com/musicdownloader/app/util/Formatters.kt`

## Files to Create/Modify

| File | Purpose |
|------|---------|
| `app/src/main/java/.../ui/components/GlassComponents.kt` | Reusable glassmorphism composables |
| `app/src/main/java/.../ui/components/UrlInputSection.kt` | URL input + paste button |
| `app/src/main/java/.../ui/components/VideoInfoCard.kt` | Video metadata display |
| `app/src/main/java/.../ui/components/FormatSelector.kt` | Download format chips |
| `app/src/main/java/.../ui/components/DownloadProgressSection.kt` | Progress bar + cancel |
| `app/src/main/java/.../ui/components/DownloadResultCards.kt` | Success/error state cards |
| `app/src/main/java/.../ui/screens/MainScreen.kt` | Main screen composition |
| `app/src/main/java/.../util/ClipboardHelper.kt` | Clipboard access |
| `app/src/main/java/.../util/Formatters.kt` | Duration/size formatters |
| `app/src/main/java/.../MainActivity.kt` | (MODIFY) Wire MainScreen |

## Test Criteria (Phase 03 Verification)

### Test 1: Formatter Unit Tests

**File:** `app/src/test/java/com/musicdownloader/app/util/FormattersTest.kt`

```kotlin
package com.musicdownloader.app.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `formatDuration with seconds less than a minute`() {
        assertEquals("0:45", formatDuration(45))
    }

    @Test
    fun `formatDuration with minutes and seconds`() {
        assertEquals("3:25", formatDuration(205))
    }

    @Test
    fun `formatDuration with hours`() {
        assertEquals("1:05:30", formatDuration(3930))
    }

    @Test
    fun `formatDuration with zero`() {
        assertEquals("0:00", formatDuration(0))
    }

    @Test
    fun `formatDuration with exactly one minute`() {
        assertEquals("1:00", formatDuration(60))
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.util.FormattersTest"
```
**Expected:** 5 tests passed, 0 failures.

### Test 2: Compose UI Tests (Instrumented)

**File:** `app/src/androidTest/java/com/musicdownloader/app/ui/MainScreenTest.kt`

```kotlin
package com.musicdownloader.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.musicdownloader.app.data.models.*
import com.musicdownloader.app.data.repository.FakeDownloadRepository
import com.musicdownloader.app.ui.screens.MainScreen
import com.musicdownloader.app.ui.theme.MusicDownloaderTheme
import com.musicdownloader.app.ui.viewmodel.DownloadViewModel
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createViewModel(): DownloadViewModel {
        return DownloadViewModel(FakeDownloadRepository())
    }

    @Test
    fun mainScreen_displaysUrlInput() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("url_input").assertIsDisplayed()
    }

    @Test
    fun mainScreen_displaysFormatSelector() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("format_selector").assertIsDisplayed()
    }

    @Test
    fun mainScreen_fetchInfoButton_exists() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("fetch_info_button").assertIsDisplayed()
    }

    @Test
    fun mainScreen_pasteButton_exists() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("paste_button").assertIsDisplayed()
    }

    @Test
    fun mainScreen_downloadButton_disabledWhenUrlEmpty() {
        composeTestRule.setContent {
            MusicDownloaderTheme {
                MainScreen(viewModel = createViewModel())
            }
        }
        composeTestRule.onNodeWithTag("download_button").assertIsNotEnabled()
    }
}
```

Run:
```bash
./gradlew connectedDebugAndroidTest --tests "com.musicdownloader.app.ui.MainScreenTest"
```
**Expected:** 5 tests passed on connected device/emulator.

### Test 3: Build Verification
```bash
./gradlew assembleDebug
```
**Expected:** BUILD SUCCESSFUL. The APK launches and displays the Glassmorphism main screen with URL input, format selector, and action buttons.

### Test 4: Full Unit Test Suite
```bash
./gradlew testDebugUnitTest
```
**Expected:** All tests from Phase 01 + 02 + 03 pass (minimum 22 tests, 0 failures).

## Notes

- The Glassmorphism effect in Compose is achieved through layering: a `Box` with a `Brush.linearGradient` background at low alpha, a `Border` with `Brush.linearGradient` at low alpha, and large `RoundedCornerShape`.
- True blur (`RenderEffect.createBlurEffect`) requires API 31+. For API 26-30, use a solid semi-transparent background as a graceful fallback.
- Use `Modifier.testTag("tag_name")` on all interactive elements for UI test discoverability.
- Image loading via Coil: `AsyncImage(model = thumbnailUrl, ...)`.

---
Next Phase: [phase-04-foreground-service.md](./phase-04-foreground-service.md)
