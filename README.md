# NoteVault — Phase 1
**By Sayem Uddin Samir**

Phase 1 implementation includes:
- Room database with Notes and Folders tables
- DAO and repository layers
- MVVM ViewModel and StateFlow UI state
- Create, edit, soft-delete notes
- Pin and favorite actions
- Search across title and content
- Folder creation, filtering, deletion
- Note color palette
- Premium dark dashboard and basic light palette toggle

## Build

**Option A: cloud build, no PC needed.** Push this repository to GitHub. The workflow in `.github/workflows/android-debug-apk.yml` installs JDK 17, Android SDK Platform 36 and Gradle 8.14, then builds the debug APK. Download it from the run page under Artifacts (`NoteVault-debug-apk`). If the run has not started on its own, open the Actions tab and choose **Run workflow**.

**Option B: Android Studio.**
1. Open this folder in Android Studio.
2. Use JDK 17.
3. Install Android SDK Platform 36.
4. Sync Gradle.
5. Run the app on an emulator or Android device.

The debug build is signed with `app/debug-signing/notevault-debug.jks`, a throwaway debug-only key. It is committed on purpose so that each new APK can update the previous install without wiping Room data. Never use it for a release build.

## Current behavior and limitations
- Notes are persisted in Room.
- Search is local and reactive.
- The editor auto-saves non-empty drafts after approximately 700 ms without typing. Closing the editor by any means (Done, Cancel, back, outside tap) saves any edit still pending. Room persists the draft locally. Opening a note without editing it does not change its position in the list.
- Deleting a note marks it as deleted; a Trash/restore UI is a later milestone.
- Folder deletion detaches notes from that folder rather than deleting notes.
- The light-mode toggle is a starter visual mode; a full light Material theme is a follow-up.
- The note editor currently supports plain text, not rich-text formatting.
- Phase 1 does not yet implement encryption, PIN lock, Firebase sync, PDF/DOCX export or sharing. Those are separate milestones.
- The GitHub Actions run is the build of record. Device testing has not been done yet.
- Rotating the device while the editor is open closes the editor. Pending edits are saved first, but the editor is not restored. Restoring it is a follow-up.

## Database note
Schema version is 1. For future schema changes, add Room migrations; do not use destructive migration for user data.
