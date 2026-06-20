# Phase 02: Playlist Metadata Extraction

Status: ✅ Completed
Dependencies: [Phase 01](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-01-analysis.md)

## Objective
Implement efficient playlist metadata extraction in the download repository using `yt-dlp`'s `--flat-playlist` and `--dump-single-json` options.

## Requirements
### Functional
- When a playlist URL is queried, do not fetch full metadata for all videos individually (which is slow and wastes data).
- Use `--flat-playlist` to fetch list metadata in a single fast JSON call.
- Parse the resulting JSON using Jackson ObjectMapper (or standard JSON parser) to build a playlist-specific `VideoInfo` instance.

### Non-Functional
- Do not block the UI thread.
- Error handling: Handle failures gracefully, including private playlists or invalid tokens.

## Implementation Steps

1. [x] **Update [IDownloadRepository.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/IDownloadRepository.kt)**:
   Add helper functions if needed, or keep the existing `fetchVideoInfo(url)` and let it internally route between single video and playlist.

2. [x] **Update [DownloadRepository.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/DownloadRepository.kt) - `fetchVideoInfo`**:
   Check if the URL is a playlist using `NetworkHelper.isPlaylistUrl(url)`.
   - If NOT a playlist, keep current behavior (`--no-playlist`).
   - If a playlist:
     - Construct a custom `YoutubeDLRequest(url)`.
     - Add options: `--flat-playlist`, `--dump-single-json`.
     - Run `YoutubeDL.getInstance().execute(request)`.
     - Extract standard output string `response.getOut()`.
     - Parse the output JSON.

3. [x] **Implement JSON Parsing**:
   Use Jackson ObjectMapper (accessed via `YoutubeDL.getInstance().getObjectMapper()`) or `org.json.JSONObject` to parse the output JSON:
   - Extract `title` (playlist title).
   - Extract uploader (`uploader` or `uploader_id` or default to "Unknown Uploader").
   - Extract uploader profile / playlist thumbnail (or use the thumbnail of the first item in the `entries` array).
   - Count the size of the `entries` array to get the `videoCount`.
   - Construct a `VideoInfo` where `isPlaylist = true`, `videoCount = entries.size`, `duration = 0L` (as duration is not loaded flatly).

## Files to Create/Modify
- [DownloadRepository.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/DownloadRepository.kt) - Add playlist detection and custom `--flat-playlist` JSON execution/extraction.

## Test Criteria
- [x] Verify playlist metadata loading prints correct title and video count.
- [x] Verify that invalid/private playlist URLs return appropriate exceptions or errors.

---
Next Phase: [Phase 03: Playlist Downloading & Progress Parsing](file:///d:/skul9x/music-android-main/plans/260620-1555-youtube-playlist-download/phase-03-download-progress.md)
