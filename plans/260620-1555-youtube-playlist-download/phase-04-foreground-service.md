# Phase 04: Foreground Service & Notification Updates

Status: ✅ Completed
Dependencies: [Phase 03](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-03-download-progress.md)

## Objective
Update `DownloadService` and `NotificationHelper` to display playlist batch status in the system notifications during background downloads.

## Requirements
### Functional
- If a playlist is downloading, show a notification message containing batch information, e.g., "Downloading 3 of 15" instead of standard single file progress.
- Maintain real-time progress bar, speed, and ETA calculation in notifications.

## Implementation Steps

1. [x] **Update [NotificationHelper.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/service/NotificationHelper.kt)**:
   Modify `buildProgressNotification`:
   - If `progress.totalItems > 0`, modify the notification content text or subtext to include:
     `"Downloading item ${progress.currentItem} of ${progress.totalItems} (${progress.percent.toInt()}%)"`
   - Ensure title reflects the playlist title if passed in the extras.

2. [x] **Update [DownloadService.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/service/DownloadService.kt)**:
   Ensure that the playlist title is passed from `MainActivity` / `DownloadViewModel` to the `DownloadService` intent so it can show the correct playlist name in the notification title.
   - Set the title extra to the playlist title if `isPlaylist` is true.

## Files to Create/Modify
- [NotificationHelper.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/service/NotificationHelper.kt) - Update notification text layouts to show batch download progress.
- [DownloadService.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/service/DownloadService.kt) - Propagate playlist information to the notification creator.

## Test Criteria
- [x] Verify notification outputs show "Downloading item X of Y" when simulated.
- [x] Verify notification remains clean and reverts to simple layouts for single video downloads.

---
Next Phase: [Phase 05: UI Component Integration](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-05-ui-integration.md)
