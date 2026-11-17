package com.sayem.notevault.data

class NoteRepository(
    private val notes: NoteDao,
    private val folders: FolderDao
) {
    fun observeNotes() = notes.observeNotes()
    fun observeFolders() = folders.observeFolders()
    fun observeCount() = notes.observeCount()

    suspend fun saveNote(note: NoteEntity) = notes.upsert(note)
    suspend fun getNote(id: String) = notes.getById(id)
    suspend fun deleteNote(id: String) = notes.moveToTrash(id, System.currentTimeMillis())
    suspend fun pinNote(id: String, value: Boolean) = notes.setPinned(id, value, System.currentTimeMillis())
    suspend fun favoriteNote(id: String, value: Boolean) = notes.setFavorite(id, value, System.currentTimeMillis())

    suspend fun saveFolder(folder: FolderEntity) = folders.upsert(folder)

    suspend fun deleteFolder(id: String) {
        folders.detachNotes(id)
        folders.delete(id)
    }
}
