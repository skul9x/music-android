# Phase 04: Foreground Service & Notifications

Status: ✅ Completed  
Dependencies: Phase 03 (UI Main Screen)

## Objective

Implement an Android Foreground Service that runs downloads in the background, showing a persistent notification with real-time progress. This ensures downloads continue even when the user navigates away from the app. The service communicates with the UI via a bound service pattern or broadcast mechanism.

## Requirements

### Functional
- [x] `DownloadService` foreground service that executes `youtubedl-android` downloads.
- [x] Persistent notification with download progress bar, title, speed, and cancel action.
- [x] Notification updates in real-time as download progresses.
- [x] Notification transforms to "Download Complete" with an "Open" action on success.
- [x] Notification transforms to "Download Failed" with a "Retry" action on error.
- [x] Service stops itself after download completes or is cancelled.
- [x] UI receives progress updates from the service via a shared `StateFlow` or `BroadcastReceiver`.

### Non-Functional
- [x] Foreground service type: `dataSync`.
- [x] Notification channel: "Downloads" with `IMPORTANCE_LOW` (no sound).
- [x] Request `POST_NOTIFICATIONS` runtime permission on Android 13+.
- [x] Handle `onTimeout()` callback for Android 14+ foreground service restrictions.

## Implementation Steps

### 1. Create Notification Helper
- [x] `NotificationHelper` object: creates notification channel, builds progress notification, builds completion notification, builds error notification.
- [x] Notification includes: small icon, content title (video title), progress bar, speed text, cancel action via `PendingIntent`.

**File:** `app/src/main/java/com/musicdownloader/app/service/NotificationHelper.kt`

### 2. Create Download Foreground Service
- [x] `DownloadService` extends `Service`.
- [x] `onStartCommand()`: receives URL, save path, and format via `Intent` extras.
- [x] Calls `startForeground()` with the initial notification.
- [x] Runs download using `DownloadRepository` on a coroutine scope.
- [x] Updates notification on each progress callback.
- [x] On completion: updates notification to "Complete", calls `stopSelf()`.
- [x] On error: updates notification to "Failed", calls `stopSelf()`.
- [x] On cancel (via notification action): cancels download, calls `stopSelf()`.

**File:** `app/src/main/java/com/musicdownloader/app/service/DownloadService.kt`

### 3. Create Service Communication Layer
- [x] `DownloadServiceBridge` singleton: shared `MutableStateFlow<DownloadUiState>` that both the service and UI observe.
- [x] Service writes progress to this flow; UI collects it.
- [x] This avoids complex bound-service patterns while keeping the architecture simple.

**File:** `app/src/main/java/com/musicdownloader/app/service/DownloadServiceBridge.kt`

### 4. Update ViewModel to Use Service
- [x] Modify `DownloadViewModel.startDownload()` to launch the `DownloadService` via `context.startForegroundService(intent)` instead of running inline.
- [x] ViewModel observes `DownloadServiceBridge.state` for progress updates.

**Modify:** `app/src/main/java/com/musicdownloader/app/ui/viewmodel/DownloadViewModel.kt`

### 5. Update Manifest
- [x] Declare `DownloadService` with `foregroundServiceType="dataSync"`.
- [x] Ensure permissions: `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `POST_NOTIFICATIONS`.

**Modify:** `app/src/main/AndroidManifest.xml`

### 6. Handle Runtime Permissions
- [x] In `MainScreen`, request `POST_NOTIFICATIONS` permission before starting a download (Android 13+).
- [x] Use `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`.

**Modify:** `app/src/main/java/com/musicdownloader/app/ui/screens/MainScreen.kt`

### 7. Handle Save Path Selection
- [x] Use SAF (`ActivityResultContracts.OpenDocumentTree()`) to let the user pick a download folder.
- [x] Persist the URI via `contentResolver.takePersistableUriPermission()`.
- [x] Store the chosen path in `SharedPreferences` for reuse.
- [x] Default to `Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)` if no folder selected.

**File:** `app/src/main/java/com/musicdownloader/app/util/StorageHelper.kt`

## Files to Create/Modify

| File | Purpose |
|------|---------|
| `app/src/main/java/.../service/NotificationHelper.kt` | Notification builder utility |
| `app/src/main/java/.../service/DownloadService.kt` | Foreground download service |
| `app/src/main/java/.../service/DownloadServiceBridge.kt` | Shared state between service and UI |
| `app/src/main/java/.../util/StorageHelper.kt` | SAF folder picker + SharedPreferences |
| `app/src/main/java/.../ui/viewmodel/DownloadViewModel.kt` | (MODIFY) Delegate to service |
| `app/src/main/java/.../ui/screens/MainScreen.kt` | (MODIFY) Permission handling |
| `app/src/main/AndroidManifest.xml` | (MODIFY) Service declaration |

## Test Criteria (Phase 04 Verification)

### Test 1: DownloadServiceBridge Unit Tests

**File:** `app/src/test/java/com/musicdownloader/app/service/DownloadServiceBridgeTest.kt`

```kotlin
package com.musicdownloader.app.service

import com.musicdownloader.app.data.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadServiceBridgeTest {

    @Before
    fun reset() {
        DownloadServiceBridge.reset()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(DownloadUiState.Idle, DownloadServiceBridge.state.value)
    }

    @Test
    fun `updateState changes the state flow`() = runTest {
        DownloadServiceBridge.updateState(
            DownloadUiState.Downloading(
                DownloadProgress(50f, 10, "2.0MiB/s", "[download] 50%")
            )
        )
        val state = DownloadServiceBridge.state.value
        assertTrue(state is DownloadUiState.Downloading)
        assertEquals(50f, (state as DownloadUiState.Downloading).progress.percent, 0.01f)
    }

    @Test
    fun `reset returns state to Idle`() = runTest {
        DownloadServiceBridge.updateState(DownloadUiState.Success("/path"))
        DownloadServiceBridge.reset()
        assertEquals(DownloadUiState.Idle, DownloadServiceBridge.state.value)
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.service.DownloadServiceBridgeTest"
```
**Expected:** 3 tests passed, 0 failures.

### Test 2: StorageHelper Unit Tests

**File:** `app/src/test/java/com/musicdownloader/app/util/StorageHelperTest.kt`

```kotlin
package com.musicdownloader.app.util

import org.junit.Assert.*
import org.junit.Test

class StorageHelperTest {

    @Test
    fun `default download path is not empty`() {
        // StorageHelper.getDefaultDownloadPath() should return a non-empty string
        val path = StorageHelper.getDefaultDownloadPath()
        assertTrue("Default path should not be blank", path.isNotBlank())
    }

    @Test
    fun `sanitizeFileName removes illegal characters`() {
        val cleaned = StorageHelper.sanitizeFileName("My Video / Title: \"Test\" <file>")
        assertFalse("Should not contain /", cleaned.contains("/"))
        assertFalse("Should not contain :", cleaned.contains(":"))
        assertFalse("Should not contain \"", cleaned.contains("\""))
        assertFalse("Should not contain <", cleaned.contains("<"))
        assertFalse("Should not contain >", cleaned.contains(">"))
    }

    @Test
    fun `sanitizeFileName preserves valid characters`() {
        val cleaned = StorageHelper.sanitizeFileName("My Video Title 2024")
        assertEquals("My Video Title 2024", cleaned)
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.util.StorageHelperTest"
```
**Expected:** 3 tests passed, 0 failures.

### Test 3: NotificationHelper Unit Tests

**File:** `app/src/test/java/com/musicdownloader/app/service/NotificationHelperTest.kt`

```kotlin
package com.musicdownloader.app.service

import org.junit.Assert.*
import org.junit.Test

class NotificationHelperTest {

    @Test
    fun `CHANNEL_ID is defined and non-empty`() {
        assertTrue(NotificationHelper.CHANNEL_ID.isNotBlank())
    }

    @Test
    fun `NOTIFICATION_ID is positive`() {
        assertTrue(NotificationHelper.NOTIFICATION_ID > 0)
    }
}
```

Run:
```bash
./gradlew testDebugUnitTest --tests "com.musicdownloader.app.service.NotificationHelperTest"
```
**Expected:** 2 tests passed, 0 failures.

### Test 4: Instrumented Service Test

**File:** `app/src/androidTest/java/com/musicdownloader/app/service/DownloadServiceTest.kt`

```kotlin
package com.musicdownloader.app.service

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DownloadServiceTest {

    @Test
    fun service_intent_can_be_created() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val intent = Intent(context, DownloadService::class.java).apply {
            putExtra("url", "https://youtube.com/watch?v=test")
            putExtra("save_path", "/storage/emulated/0/Download")
            putExtra("format", "M4A_AUDIO")
        }
        assertNotNull("Intent should be non-null", intent)
        assertEquals("https://youtube.com/watch?v=test", intent.getStringExtra("url"))
    }
}
```

Run:
```bash
./gradlew connectedDebugAndroidTest --tests "com.musicdownloader.app.service.DownloadServiceTest"
```
**Expected:** 1 test passed on connected device/emulator.

### Test 5: Full Unit Test Suite
```bash
./gradlew testDebugUnitTest
```
**Expected:** All tests from Phase 01-04 pass (minimum 30 tests, 0 failures).

## Notes

- `DownloadServiceBridge` is a simple singleton with a `MutableStateFlow`. This is intentionally simpler than a bound service or AIDL for this single-activity app. If multi-activity support is needed later, it can be refactored.
- For Android 14+ (API 34), foreground services have stricter enforcement. We declare `dataSync` type which is appropriate for file download tasks.
- The notification cancel action uses a `PendingIntent` that sends an `ACTION_CANCEL` intent back to the service.

---
Next Phase: [phase-05-polish-settings.md](./phase-05-polish-settings.md)
