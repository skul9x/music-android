# Phase 01: Analysis & Data Model Extensions

Status: ✅ Completed
Dependencies: None

## Objective
Extend core data models (`VideoInfo`, `DownloadProgress`) and update link validators (`UrlValidator`, `NetworkHelper`) to support YouTube playlist URLs and batch progress metadata.

## Requirements
### Functional
- Support detecting and validating YouTube playlist URLs (e.g. `https://www.youtube.com/playlist?list=...` and video watch links containing a `list` query parameter).
- Preserve single-video download behaviour when the URL does not contain any playlist parameters.
- Provide batch download metadata in the progress callback (current video index and total video count).

### Non-Functional
- Backwards compatibility: Existing models must retain their signatures with default values to prevent compilation breakage of existing features.

## Implementation Steps

1. [x] **Update URL patterns in [UrlValidator.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/util/UrlValidator.kt)**:
   Add regexes for playlist paths:
   - `https?://(www\.)?youtube\.com/playlist\?list=.+`
   - `https?://m\.youtube\.com/playlist\?list=.+`
   - Add watch URL with playlist query: `https?://(www\.)?youtube\.com/watch\?v=.+&list=.+`

2. [x] **Refine [NetworkHelper.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/util/NetworkHelper.kt)**:
   Ensure `isPlaylistUrl(url)` returns true for valid playlists and ensure `stripPlaylistParam(url)` is only used when the user explicitly requests to download only a single video from a combined URL.

3. [x] **Extend [VideoInfo](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/models/DownloadModels.kt)**:
   Add fields:
   - `val isPlaylist: Boolean = false`
   - `val videoCount: Int = 0`

4. [x] **Extend [DownloadProgress](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/models/DownloadModels.kt)**:
   Add fields to keep track of batch progress:
   - `val currentItem: Int = 0`
   - `val totalItems: Int = 0`

## Files to Create/Modify
- [UrlValidator.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/util/UrlValidator.kt) - Support playlist URL validation.
- [DownloadModels.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/models/DownloadModels.kt) - Add batch-related properties to `VideoInfo` and `DownloadProgress`.

## Test Criteria
- [x] Verify `UrlValidator.isSupported()` returns true for various playlist URL formats.
- [x] Verify that `VideoInfo` and `DownloadProgress` compile with their new default parameters.

---
Next Phase: [Phase 02: Playlist Metadata Extraction](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-02-download-engine.md)
