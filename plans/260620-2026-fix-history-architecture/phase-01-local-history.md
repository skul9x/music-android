# Phase 01: Persistent Local History
Status: ✅ Completed
Dependencies: None

## Objective
Convert `HistoryRepository` from an in-memory (`RAM`) store into a persistent store using Android's built-in SQLite framework. To keep tests fast and decouple data storage from Android dependencies, we will introduce a `HistoryPersistence` abstraction layer, allowing both a production SQLite database implementation and a JVM-testable in-memory implementation.

## Requirements

### Functional
- Every downloaded music/video file must be saved in a persistent database.
- Swiping the app away, rebooting the phone, or OS background memory reclamation must NOT erase download history.
- The history list must continue to display items sorted in reverse chronological order (newest first).
- Clearing history must erase all records from physical storage.

### Non-Functional
- **Performance**: Reading history from the SQLite database must occur asynchronously or instantly without freezing the main thread.
- **Robustness**: Proper schema definition and migrations should be defined to avoid crashes when updating.
- **Testability**: The persistence layer must be decoupled so that JUnit unit tests can run instantly on the JVM without requiring an emulator, Robolectric, or heavy mocks.

## Proposed Design & Architecture

```mermaid
graph TD
    HistoryRepository -->|delegates to| HistoryPersistence
    HistoryPersistence <|.. SQLiteHistoryPersistence
    HistoryPersistence <|.. InMemoryHistoryPersistence
    SQLiteHistoryPersistence -->|uses| HistoryDatabaseHelper
    HistoryDatabaseHelper -->|inherits| SQLiteOpenHelper
```

### 1. The `HistoryPersistence` Interface
Introduce an interface to decouple the repository from the storage technology:
```kotlin
package com.musicdownloader.app.data.repository

import com.musicdownloader.app.data.models.DownloadHistoryItem

interface HistoryPersistence {
    fun getHistory(): List<DownloadHistoryItem>
    fun addItem(item: DownloadHistoryItem)
    fun clearHistory()
}
```

### 2. SQLite Implementation (`SQLiteHistoryPersistence`)
Uses Android's standard `SQLiteOpenHelper` to write records to `download_history.db`.
- **Database Name**: `download_history.db`
- **Database Version**: `1`
- **Table name**: `download_history`
- **Schema Columns**:
  - `id` INTEGER PRIMARY KEY AUTOINCREMENT
  - `title` TEXT NOT NULL
  - `file_path` TEXT NOT NULL
  - `format` TEXT NOT NULL
  - `timestamp` INTEGER NOT NULL
  - `thumbnail_url` TEXT NOT NULL
  - `is_playlist` INTEGER NOT NULL (0 for false, 1 for true)

### 3. In-Memory Implementation (`InMemoryHistoryPersistence`)
Uses a simple thread-safe mutable list. This is passed to `HistoryRepository` during JVM unit tests.

## Implementation Steps

1. **Create Interface and Implementations**:
   - Create `HistoryPersistence.kt` in `com.musicdownloader.app.data.repository`.
   - Create `HistoryDatabaseHelper.kt` in `com.musicdownloader.app.data.repository`.
   - Create `SQLiteHistoryPersistence.kt` in `com.musicdownloader.app.data.repository`.
   - Create `InMemoryHistoryPersistence.kt` in `com.musicdownloader.app.data.repository`.

2. **Update `HistoryRepository`**:
   - Modify the primary constructor to receive `HistoryPersistence`.
   - Implement `getInstance(context: Context)` as a singleton that instantiates the SQLite implementation.
   - Cache values in the `MutableStateFlow` dynamically when operations are performed, ensuring the UI flow updates immediately.

3. **Update JVM Unit Tests**:
   - Update `app/src/test/java/com/musicdownloader/app/data/repository/HistoryRepositoryTest.kt` to initialize the repository using `InMemoryHistoryPersistence`.
   - Add new test cases verifying persistence edge cases (e.g., duplicate entries, long file paths, playlist flag parsing).

## Files to Create/Modify
- [NEW] [HistoryPersistence.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/HistoryPersistence.kt) - Storage abstraction interface.
- [NEW] [HistoryDatabaseHelper.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/HistoryDatabaseHelper.kt) - SQLite database helper.
- [NEW] [SQLiteHistoryPersistence.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/SQLiteHistoryPersistence.kt) - Production SQLite implementation.
- [NEW] [InMemoryHistoryPersistence.kt](file:///d:/skul9x/music-android-main/app/src/test/java/com/musicdownloader/app/data/repository/InMemoryHistoryPersistence.kt) - JVM testing mock persistence.
- [MODIFY] [HistoryRepository.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/HistoryRepository.kt) - Refactored to delegate data access.
- [MODIFY] [HistoryRepositoryTest.kt](file:///d:/skul9x/music-android-main/app/src/test/java/com/musicdownloader/app/data/repository/HistoryRepositoryTest.kt) - Updated test framework to use the in-memory persistence layer.

## Test Criteria (File-Based Tests)

To verify the database layer and repository logic, run the JVM Unit Tests:
```bash
./gradlew :app:testDebugUnitTest --tests "com.musicdownloader.app.data.repository.HistoryRepositoryTest"
```

### Test Verifications:
- `initially history is empty`
- `addItem increases history size and persists`
- `history items are returned in reverse chronological order based on timestamp`
- `clearHistory deletes all persisted records`
- `isPlaylist flag is correctly persisted and retrieved`

---
Next Phase: [phase-02-architecture-refactoring.md](file:///d:/skul9x/music-android-main/plans/260620-2026-fix-history-architecture/phase-02-architecture-refactoring.md)
