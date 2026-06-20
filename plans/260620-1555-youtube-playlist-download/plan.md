# Plan: YouTube Playlist Downloading Feature

Created: 2026-06-20  
Status: 🟡 In Progress  
Feature: Support downloading all videos from a YouTube playlist

## Overview

Extend the Android Music Downloader app to support downloading entire playlists. Users will paste a YouTube playlist link (either a direct playlist URL or a video link with a playlist ID). The app will:
1. Validate and recognize the playlist link.
2. Fetch playlist metadata quickly (using `yt-dlp` flat-playlist extraction).
3. Display playlist title, uploader, and total video count in the UI.
4. Download all videos in the playlist sequentially using the background foreground service.
5. Display detailed batch download progress ("Downloading item X of Y") in notifications and screen UI.

## Tech Stack

- **Language:** Kotlin (2.0+)
- **UI:** Jetpack Compose + Material 3
- **Download Engine:** `youtubedl-android` (wrapping `yt-dlp` executable)
- **JSON Parsing:** Jackson ObjectMapper (available from the `youtubedl-android` library classpath) or `org.json.JSONObject`

## Phases

| Phase | Name | Status | Est. Tasks |
|-------|------|--------|------------|
| 01 | Analysis & Data Model Extensions | ✅ Completed | 4 |
| 02 | Playlist Metadata Extraction | ✅ Completed | 5 |
| 03 | Download Engine & Progress Parsing | ✅ Completed | 6 |
| 04 | Foreground Service & Notification Updates | ✅ Completed | 4 |
| 05 | UI Component Integration | ✅ Completed | 5 |
| 06 | Unit Testing & Verification | ⬜ Pending | 4 |

**Total:** ~28 tasks

## Quick Commands

- Start Phase 1: `/code phase-01`
- Check progress: `/next`
- Save context: `/save-brain`
