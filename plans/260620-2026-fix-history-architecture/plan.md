# Plan: Fix Download History Persistence and UI-Repository Architecture
Created: 2026-06-20T20:26:20+07:00
Status: 🟡 In Progress

## Overview
This plan addresses two architectural and functional bugs identified in the **Music Downloader** app:
1. **Temporary Memory Loss (Download History stored only in RAM)**: History is lost when the app process is terminated or reclaimed by Android OS. We will fix this by persisting the download history to a local SQLite database using a lightweight and dependency-free abstraction.
2. **Architectural Deviation (Direct Repository Initialization in Compose UI)**: UI screens (`MainScreen.kt`) are directly instantiating and referencing repositories (`SettingsRepository` and `HistoryRepository`). We will move repository instantiations to ViewModel factories, wire repository state changes through ViewModels, and decouple the UI layer entirely from direct data layer references.

## Tech Stack
- Kotlin
- Jetpack Compose
- Android SQLite (`SQLiteOpenHelper`)
- Kotlin Coroutines (StateFlow, viewModelScope)
- Android ViewModel

## Phases

| Phase | Name | Status | Progress | Description |
|-------|------|--------|----------|-------------|
| 01 | [Persistent Local History](file:///d:/skul9x/music-android-main/plans/260620-2026-fix-history-architecture/phase-01-local-history.md) | ⬜ Pending | 0% | Implement SQLite persistence for history items and isolate testing. |
| 02 | [UI & ViewModel Refactoring](file:///d:/skul9x/music-android-main/plans/260620-2026-fix-history-architecture/phase-02-architecture-refactoring.md) | ⬜ Pending | 0% | Decouple UI from direct repositories and move state management to ViewModels. |
| 03 | [Verification and Testing](file:///d:/skul9x/music-android-main/plans/260620-2026-fix-history-architecture/phase-03-verification-testing.md) | ⬜ Pending | 0% | Execute JVM tests, verify persistence, and check for UI architectural sanity. |

## Quick Commands
- Start Phase 1: `/code phase-01`
- Start Phase 2: `/code phase-02`
- Check progress: `/next`
- Save context: `/save-brain`

---
Next Phase: [phase-01-local-history.md](file:///d:/skul9x/music-android-main/plans/260620-2026-fix-history-architecture/phase-01-local-history.md)
