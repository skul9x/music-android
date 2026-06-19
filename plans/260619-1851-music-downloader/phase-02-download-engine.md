# Phase 02: Core Download Engine Integration

Status: ✅ Completed  
Dependencies: Phase 01 (Project Setup)

## Objective

Implement the core download logic using `youtubedl-android`. Create a `DownloadManager` class and a `DownloadViewModel` that can fetch video metadata, execute downloads with real-time progress, and handle cancellation. This phase does NOT include UI — it focuses purely on the business logic layer and its unit tests.

## Requirements

### Functional
- [x] Fetch video/audio metadata (title, thumbnail URL, duration, available formats) from a given URL.
- [x] Download audio as M4A with embedded thumbnail (cover art).
- [x] Download video at 1080p or 720p with merged audio.
- [x] Report real-time download progress (percentage, ETA, speed) via `StateFlow`.
- [x] Support download cancellation.
- [x] Handle errors gracefully (invalid URL, network failure, yt-dlp errors).

### Non-Functional
- [x] All download operations run on `Dispatchers.IO`.
- [x] ViewModel exposes immutable `StateFlow<DownloadUiState>` for UI observation.
- [x] No Android UI code in this phase (pure ViewModel + repository layer).

## Implementation Steps

### 1. Define Data Models
- [x] `DownloadFormat` enum: `M4A_AUDIO`, `VIDEO_1080P`, `VIDEO_720P`, `VIDEO_BEST`.
- [x] `VideoInfo` data class: `title`, `thumbnailUrl`, `duration`, `uploader`, `url`.
- [x] `DownloadProgress` data class: `percent` (Float), `etaSeconds` (Long), `speedStr` (String), `line` (String).
- [x] `DownloadUiState` sealed class: `Idle`, `FetchingInfo`, `InfoReady(VideoInfo)`, `Downloading(DownloadProgress)`, `Success(filePath: String)`, `Error(message: String)`, `Cancelled`.

**File:** `app/src/main/java/com/musicdownloader/app/data/models/DownloadModels.kt`

### 2. Create Download Repository
- [x] `DownloadRepository` class encapsulating all `YoutubeDL` interactions.
- [x] `fetchVideoInfo(url: String): VideoInfo` — runs `yt-dlp --dump-json --no-playlist`.
- [x] `download(url: String, savePath: String, format: DownloadFormat, onProgress: (DownloadProgress) -> Unit): String` — executes the download with format-specific arguments, returns the output file path.
- [x] `cancelDownload()` — calls `YoutubeDL.getInstance().destroyProcessById(processId)`.
- [x] Build yt-dlp arguments matching the Desktop MusicYT logic:
  - M4A: `-x --audio-format m4a --embed-thumbnail --convert-thumbnails jpg`
  - 1080p: `-f "bestvideo[height<=1080]+bestaudio/best[height<=1080]"`
  - 720p: `-f "bestvideo[height<=720]+bestaudio/best[height<=720]"`
  - Best: `-f "bestvideo+bestaudio/best"`

**File:** `app/src/main/java/com/musicdownloader/app/data/repository/DownloadRepository.kt`

### 3. Create DownloadViewModel
- [x] `DownloadViewModel` with `_uiState: MutableStateFlow<DownloadUiState>` and public `uiState: StateFlow<DownloadUiState>`.
- [x] `fetchInfo(url: String)` — validates URL, calls repository, updates state to `InfoReady`.
- [x] `startDownload(url: String, savePath: String, format: DownloadFormat)` — launches coroutine on `Dispatchers.IO`, updates state with progress, transitions to `Success` or `Error`.
- [x] `cancelDownload()` — cancels the coroutine job and calls repository cancel.
- [x] `resetState()` — resets to `Idle`.
- [x] URL validation: must start with `https://` and contain `youtube.com`, `youtu.be`, `tiktok.com`, or similar supported domains.

**File:** `app/src/main/java/com/musicdownloader/app/ui/viewmodel/DownloadViewModel.kt`

### 4. Create DownloadRepository Interface (for testability)
- [x] `IDownloadRepository` interface extracted from `DownloadRepository` to allow fake implementations in unit tests.

**File:** `app/src/main/java/com/musicdownloader/app/data/repository/IDownloadRepository.kt`

### 5. Create Fake Repository for Testing
- [x] `FakeDownloadRepository` implementing `IDownloadRepository` with controllable responses (success, failure, progress simulation).

**File:** `app/src/test/java/com/musicdownloader/app/data/repository/FakeDownloadRepository.kt`

## Files to Create/Modify

| File | Purpose |
|------|---------|
| `app/src/main/java/com/musicdownloader/app/data/models/DownloadModels.kt` | Data classes and sealed state |
| `app/src/main/java/com/musicdownloader/app/data/repository/IDownloadRepository.kt` | Repository interface |
| `app/src/main/java/com/musicdownloader/app/data/repository/DownloadRepository.kt` | Real implementation using youtubedl-android |
| `app/src/main/java/com/musicdownloader/app/ui/viewmodel/DownloadViewModel.kt` | ViewModel with state management |
| `app/src/test/java/com/musicdownloader/app/data/repository/FakeDownloadRepository.kt` | Fake for unit tests |

## Test Criteria (Phase 02 Verification)

### Test 1: DownloadViewModel Unit Tests

**File:** `app/src/test/java/com/musicdownloader/app/ui/viewmodel/DownloadViewModelTest.kt`

```kotlin
package com.musicdownloader.app.ui.viewmodel

import com.musicdownloader.app.data.models.*
import com.musicdownloader.app.data.repository.FakeDownloadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadViewModelTest {

    private lateinit var fakeRepo: FakeDownloadRepository
    private lateinit var viewModel: DownloadViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeDownloadRepository()
        viewModel = DownloadViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(DownloadUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `fetchInfo transitions to FetchingInfo then InfoReady`() = runTest {
        fakeRepo.videoInfoToReturn = VideoInfo(
            title = "Test Video",
            thumbnailUrl = "https://example.com/thumb.jpg",
            duration = 240,
            uploader = "TestUser",
            url = "https://youtube.com/watch?v=test123"
        )

        viewModel.fetchInfo("https://youtube.com/watch?v=test123")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be InfoReady", state is DownloadUiState.InfoReady)
        assertEquals("Test Video", (state as DownloadUiState.InfoReady).videoInfo.title)
    }

    @Test
    fun `fetchInfo with invalid URL transitions to Error`() = runTest {
        viewModel.fetchInfo("not-a-valid-url")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is DownloadUiState.Error)
    }

    @Test
    fun `startDownload transitions to Success on completion`() = runTest {
        fakeRepo.downloadResultPath = "/storage/downloads/test.m4a"

        viewModel.startDownload(
            "https://youtube.com/watch?v=test123",
            "/storage/downloads",
            DownloadFormat.M4A_AUDIO
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Success", state is DownloadUiState.Success)
        assertEquals("/storage/downloads/test.m4a", (state as DownloadUiState.Success).filePath)
    }

    @Test
    fun `startDownload with repo error transitions to Error`() = runTest {
        fakeRepo.shouldThrowOnDownload = true

        viewModel.startDownload(
            "https://youtube.com/watch?v=test123",
            "/storage/downloads",
            DownloadFormat.M4A_AUDIO
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Error", state is DownloadUiState.Error)
    }

    @Test
    fun `cancelDownload transitions to Cancelled`() = runTest {
        fakeRepo.simulateSlowDownload = true

        viewModel.startDownload(
            "https://youtube.com/watch?v=test123",
            "/storage/downloads",
            DownloadFormat.VIDEO_1080P
        )
        // Cancel before completion
        viewModel.cancelDownload()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("State should be Cancelled", state is DownloadUiState.Cancelled)
    }

    @Test
    fun `resetState returns to Idle`() = runTest {
        fakeRepo.downloadResultPath = "/storage/downloads/test.m4a"
        viewModel.startDownload("https://youtube.com/watch?v=t", "/d", DownloadFormat.M4A_AUDIO)
        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(DownloadUiState.Idle, viewModel.uiState.value)
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.ui.viewmodel.DownloadViewModelTest"
```
**Expected:** 6 tests passed, 0 failures.

### Test 2: URL Validation Tests

**File:** `app/src/test/java/com/musicdownloader/app/util/UrlValidatorTest.kt`

```kotlin
package com.musicdownloader.app.util

import org.junit.Assert.*
import org.junit.Test

class UrlValidatorTest {

    @Test
    fun `valid youtube URL returns true`() {
        assertTrue(UrlValidator.isSupported("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertTrue(UrlValidator.isSupported("https://youtu.be/dQw4w9WgXcQ"))
        assertTrue(UrlValidator.isSupported("https://m.youtube.com/watch?v=abc123"))
    }

    @Test
    fun `valid tiktok URL returns true`() {
        assertTrue(UrlValidator.isSupported("https://www.tiktok.com/@user/video/1234567890"))
        assertTrue(UrlValidator.isSupported("https://vm.tiktok.com/ABC123/"))
    }

    @Test
    fun `invalid URLs return false`() {
        assertFalse(UrlValidator.isSupported(""))
        assertFalse(UrlValidator.isSupported("not a url"))
        assertFalse(UrlValidator.isSupported("http://example.com"))
        assertFalse(UrlValidator.isSupported("ftp://youtube.com/watch?v=test"))
    }

    @Test
    fun `null-like empty string returns false`() {
        assertFalse(UrlValidator.isSupported("   "))
    }
}
```

This requires creating:

**File:** `app/src/main/java/com/musicdownloader/app/util/UrlValidator.kt`
```kotlin
object UrlValidator {
    private val SUPPORTED_PATTERNS = listOf(
        Regex("https?://(www\\.)?youtube\\.com/watch\\?v=.+"),
        Regex("https?://youtu\\.be/.+"),
        Regex("https?://m\\.youtube\\.com/watch\\?v=.+"),
        Regex("https?://(www\\.|vm\\.)?tiktok\\.com/.+"),
    )

    fun isSupported(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return false
        return SUPPORTED_PATTERNS.any { it.matches(trimmed) }
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.util.UrlValidatorTest"
```
**Expected:** 4 tests passed, 0 failures.

### Test 3: Data Model Tests

**File:** `app/src/test/java/com/musicdownloader/app/data/models/DownloadModelsTest.kt`

```kotlin
package com.musicdownloader.app.data.models

import org.junit.Assert.*
import org.junit.Test

class DownloadModelsTest {

    @Test
    fun `DownloadFormat enum has all expected values`() {
        val formats = DownloadFormat.values()
        assertEquals(4, formats.size)
        assertTrue(formats.contains(DownloadFormat.M4A_AUDIO))
        assertTrue(formats.contains(DownloadFormat.VIDEO_1080P))
        assertTrue(formats.contains(DownloadFormat.VIDEO_720P))
        assertTrue(formats.contains(DownloadFormat.VIDEO_BEST))
    }

    @Test
    fun `VideoInfo data class holds correct data`() {
        val info = VideoInfo(
            title = "My Song",
            thumbnailUrl = "https://img.youtube.com/vi/abc/maxresdefault.jpg",
            duration = 300,
            uploader = "Artist",
            url = "https://youtube.com/watch?v=abc"
        )
        assertEquals("My Song", info.title)
        assertEquals(300, info.duration)
        assertEquals("Artist", info.uploader)
    }

    @Test
    fun `DownloadProgress data class defaults`() {
        val progress = DownloadProgress(
            percent = 45.5f,
            etaSeconds = 30,
            speedStr = "1.2MiB/s",
            line = "[download] 45.5%"
        )
        assertEquals(45.5f, progress.percent, 0.01f)
        assertEquals(30, progress.etaSeconds)
    }

    @Test
    fun `DownloadUiState sealed class instances`() {
        val idle = DownloadUiState.Idle
        val error = DownloadUiState.Error("Network error")
        val success = DownloadUiState.Success("/path/to/file.m4a")

        assertTrue(idle is DownloadUiState)
        assertEquals("Network error", (error as DownloadUiState.Error).message)
        assertEquals("/path/to/file.m4a", (success as DownloadUiState.Success).filePath)
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.data.models.DownloadModelsTest"
```
**Expected:** 4 tests passed, 0 failures.

### Test 4: Full Phase 02 Test Suite
```bash
./gradlew testDebugUnitTest
```
**Expected:** All tests from Phase 01 + Phase 02 pass (minimum 13 tests total, 0 failures).

## Notes

- The `DownloadRepository` directly wraps `youtubedl-android` API. The interface `IDownloadRepository` allows us to swap in `FakeDownloadRepository` for unit tests without needing an Android device.
- The ViewModel unit tests use `kotlinx-coroutines-test` with `UnconfinedTestDispatcher` for synchronous coroutine execution in tests.
- Add `kotlinx-coroutines-test` to the version catalog and `testImplementation` dependencies.
- Real integration tests of `DownloadRepository` against actual YouTube URLs would be instrumented tests (placed in `src/androidTest/`) and are deferred to Phase 05 polish.

---
Next Phase: [phase-03-ui-main-screen.md](./phase-03-ui-main-screen.md)
