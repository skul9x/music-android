# Phase 05: UI Component Integration

Status: ✅ Completed
Dependencies: [Phase 04](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-04-foreground-service.md)

## Objective
Update the Compose UI components to display playlist metadata (like "Playlist with 24 items") and show detailed batch download progress in the main app layout.

## Requirements
### Functional
- Modify `VideoInfoCard` to handle `videoInfo.isPlaylist == true`:
  - Display a special "Playlist" tag.
  - Display the total number of videos (e.g. "24 videos") instead of duration.
- Modify `DownloadProgressSection` to display:
  - `"Downloading: item X of Y"` above or next to the progress bar.
- Ensure format selection (M4A audio, 1080p, etc.) still applies to all items in the playlist.

## Implementation Steps

1. [x] **Update [VideoInfoCard.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/components/VideoInfoCard.kt)**:
   - If `videoInfo.isPlaylist` is true:
     - Instead of the duration format text, show a row with:
       - A Playlist Icon (e.g. `Icons.Default.PlaylistPlay` or `Icons.AutoMirrored.Filled.PlaylistPlay`).
       - Text: `"${videoInfo.videoCount} videos"`.
     - Show a badge/tag with text "PLAYLIST" in a distinct color (like Accent red).

2. [x] **Update [DownloadProgressSection.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/components/DownloadProgressSection.kt)**:
   - If `progress.totalItems > 0`:
     - Change the status text from "Downloading..." to `"Downloading item ${progress.currentItem} of ${progress.totalItems}"`.

3. [x] **Update [MainScreen.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/screens/MainScreen.kt)**:
   - Verify alignment and UI rendering when playlist is detected and loading.
   - For history, when a playlist finishes downloading, add a history item that represents the folder.

## Files to Create/Modify
- [VideoInfoCard.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/components/VideoInfoCard.kt) - Customize UI layout for playlists.
- [DownloadProgressSection.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/components/DownloadProgressSection.kt) - Customize progress text for playlist/batch downloads.
- [MainScreen.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/screens/MainScreen.kt) - Align state logic for playlist items.

## Test Criteria
- [x] Verify Compose previews and layouts are responsive and look premium.
- [x] Verify that playlist info card renders with uploader details and item counts.

---
Next Phase: [Phase 06: Unit Testing & Verification](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-06-testing.md)
