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
1. Open this folder in Android Studio.
2. Use JDK 17.
3. Install Android SDK Platform 36.
4. Sync Gradle.
5. Run the app on an emulator or Android device.

## Current behavior and limitations
- Notes are persisted in Room.
- Search is local and reactive.
- The editor auto-saves non-empty drafts after approximately 700 ms without typing. The Done button closes the editor; Room persists the draft locally.
- Deleting a note marks it as deleted; a Trash/restore UI is a later milestone.
- Folder deletion detaches notes from that folder rather than deleting notes.
- The light-mode toggle is a starter visual mode; a full light Material theme is a follow-up.
- The note editor currently supports plain text, not rich-text formatting.
- Phase 1 does not yet implement encryption, PIN lock, Firebase sync, PDF/DOCX export or sharing. Those are separate milestones.
- No APK has been built or device-tested in this environment.

## Database note
Schema version is 1. For future schema changes, add Room migrations; do not use destructive migration for user data.
