# Phase 03: Playlist Downloading & Progress Parsing

Status: ✅ Completed
Dependencies: [Phase 02](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-02-download-engine.md)

## Objective
Update the download engine to support batch playlist downloading and parse command-line output lines to extract item indices (e.g. "Downloading item 3 of 20").

## Requirements
### Functional
- When downloading a playlist URL, omit the `--no-playlist` option so `yt-dlp` fetches all videos.
- Organize downloaded files inside a subfolder named after the playlist title.
- Parse the terminal stdout stream line-by-line to find:
  `[download] Downloading item X of Y`
- Feed `currentItem = X` and `totalItems = Y` into the progress callback.

### Non-Functional
- Handle download cancellation correctly (terminating the active `yt-dlp` process).
- Preserve single-video download behaviour.

## Implementation Steps

1. [x] **Modify `download` in [DownloadRepository.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/DownloadRepository.kt)**:
   Check if the URL is a playlist:
   - If NOT: Keep `--no-playlist` option, output template: `"$savePath/%(title)s.%(ext)s"`.
   - If a playlist:
     - Remove `addOption("--no-playlist")`.
     - Update output template to place files in a subfolder: `"$savePath/%(playlist_title)s/%(title)s.%(ext)s"`.

2. [x] **Implement Output Parsing for Playlist Items**:
   In the `YoutubeDL.getInstance().execute` callback:
   - Add state variables `var currentItem = 0` and `var totalItems = 0` in the repository or local scope.
   - Use Regex: `val itemRegex = Regex("\\[download\\] Downloading item (\\d+) of (\\d+)")`.
   - If a line matches the regex, extract group 1 as `currentItem` and group 2 as `totalItems`.
   - Update `DownloadProgress` with these values:
     ```kotlin
     DownloadProgress(
         percent = progress,
         etaSeconds = eta,
         speedStr = speedStr,
         line = line,
         currentItem = currentItem,
         totalItems = totalItems
     )
     ```

3. [x] **Update File Path Resolution**:
   When downloading a playlist, `yt-dlp` downloads multiple files. The file path extracted at the end of the download can represent the parent directory containing all downloaded files:
   - Modify `extractFilePath` or handle playlist-specific success return paths (e.g., return the subfolder path `"$savePath/Playlist Title"` instead of a single file path).

## Files to Create/Modify
- [DownloadRepository.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/DownloadRepository.kt) - Update options, add regex parsing for playlist item count, and modify file path resolution for playlist outputs.

## Test Criteria
- [x] Verify regex extracts item index and count correctly from sample log outputs.
- [x] Verify that canceling the download properly stops the whole batch download.

---
Next Phase: [Phase 04: Foreground Service & Notification Updates](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-04-foreground-service.md)
