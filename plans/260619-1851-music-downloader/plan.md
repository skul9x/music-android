# Plan: Music Downloader — Android App

Created: 2026-06-19  
Status: 🟡 In Progress

## Overview

Build a native Android application called **"Music Downloader"** using Kotlin, Jetpack Compose, and Material 3. The app downloads music (M4A with embedded cover art) and video (1080p/720p) from YouTube/TikTok using the `youtubedl-android` library (yt-dlp + ffmpeg running locally on-device). It features a Glassmorphism-inspired UI, real-time download progress, and a self-update mechanism for the yt-dlp engine.

## Tech Stack

- **Language:** Kotlin (2.0+)
- **UI:** Jetpack Compose + Material 3
- **Build:** Gradle Kotlin DSL (.kts) + Version Catalog (libs.versions.toml)
- **Download Engine:** `io.github.junkfood02.youtubedl-android:library:0.18.1`
- **FFmpeg:** `io.github.junkfood02.youtubedl-android:ffmpeg:0.18.1`
- **Image Loading:** Coil Compose
- **Architecture:** Single-module, MVVM (ViewModel + StateFlow)
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35

## Phases

| Phase | Name | Status | Est. Tasks |
|-------|------|--------|------------|
| 01 | Project Setup & Gradle Configuration | ⬜ Pending | 12 |
| 02 | Core Download Engine Integration | ⬜ Pending | 14 |
| 03 | UI — Main Screen (Glassmorphism) | ⬜ Pending | 16 |
| 04 | Foreground Service & Notifications | ⬜ Pending | 10 |
| 05 | Polish, Settings & Update Engine | ⬜ Pending | 11 |

**Total:** ~63 tasks

## Quick Commands

- Start Phase 1: `/code phase-01`
- Check progress: `/next`
- Save context: `/save-brain`
