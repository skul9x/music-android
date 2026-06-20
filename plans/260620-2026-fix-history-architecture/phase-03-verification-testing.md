# Phase 03: Verification & Testing
Status: ✅ Completed
Dependencies: [Phase 02: UI & ViewModel Refactoring](file:///d:/skul9x/music-android-main/plans/260620-2026-fix-history-architecture/phase-02-architecture-refactoring.md)

## Objective
Execute a comprehensive testing plan to verify that download history is persistent across app launches, and that the UI layer depends only on ViewModels without violating architectural boundaries.

## Verification Plan

### 1. Automated JVM Unit Tests
Run the entire unit test suite to check that the history persistence layer, viewmodel operations, and updates work correctly without any regression.

#### Commands:
```bash
# Run all unit tests in the project
./gradlew :app:testDebugUnitTest
```

#### Key Test Files:
- [HistoryRepositoryTest.kt](file:///d:/skul9x/music-android-main/app/src/test/java/com/musicdownloader/app/data/repository/HistoryRepositoryTest.kt) - Validates that CRUD operations propagate correctly through `HistoryRepository` using `InMemoryHistoryPersistence`.
- [DownloadViewModelTest.kt](file:///d:/skul9x/music-android-main/app/src/test/java/com/musicdownloader/app/ui/viewmodel/DownloadViewModelTest.kt) - Validates that `DownloadViewModel` captures download success actions and correctly logs them in history, without UI layer mediation.

---

### 2. Manual Verification (On Emulator or Physical Device)

#### Step 1: Persistent History Verification
1. Clean install the application on a device or emulator.
2. Search and download any audio or video item (e.g. YouTube URL).
3. Verify that the item appears in the "Download History" section.
4. Close the application, swipe it away from the Recents menu (kill the process).
5. Open the application again.
6. **Expected Result**: The download history must still be present and list the item downloaded in step 2.

#### Step 2: Settings Sync & VM Boundary Verification
1. Go to "Settings" screen.
2. Change the save folder path.
3. Switch off "Clipboard Auto-Paste".
4. Navigate back to the Main Screen.
5. **Expected Result**: The save folder path on the Main Screen must update immediately to the new path, and auto-paste must not prompt if a URL is in the clipboard.
6. Swipe away the application and open it again.
7. **Expected Result**: Settings choices must persist, and the UI must not contain lag/re-renders because it reads the states from `SettingsViewModel` and `DownloadViewModel` reactive flows.

---

### 3. Architectural Boundary Audit (Linter / Code Search Check)
Run a quick search check in terminal/command line to ensure that no screens under the `ui/screens/` directory contain imports or direct instantiations of the repositories.

#### Commands:
```powershell
# Verify no direct instantiations of repositories are left in Compose Screens
Select-String -Path "app/src/main/java/com/musicdownloader/app/ui/screens/*.kt" -Pattern "HistoryRepository", "SettingsRepository"
```
*(Expected matches should only be type declarations, if any, and not `remember` allocations or `.getInstance()` invocations).*
