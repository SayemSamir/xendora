package com.sayem.notevault.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayem.notevault.data.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.UUID

data class NotesUiState(
    val notes: List<NoteEntity> = emptyList(),
    val folders: List<FolderEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedFolderId: String? = null,
    val isLoading: Boolean = true
) {
    val visibleNotes: List<NoteEntity>
        get() = notes.filter { note ->
            val matchesQuery = searchQuery.isBlank() ||
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.content.contains(searchQuery, ignoreCase = true)
            val matchesFolder = selectedFolderId == null || note.folderId == selectedFolderId
            matchesQuery && matchesFolder
        }
}

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val folderFilter = MutableStateFlow<String?>(null)

    val uiState: StateFlow<NotesUiState> = combine(
        repository.observeNotes(),
        repository.observeFolders(),
        query,
        folderFilter
    ) { notes, folders, search, folder ->
        NotesUiState(
            notes = notes,
            folders = folders,
            searchQuery = search,
            selectedFolderId = folder,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesUiState())

    fun setSearchQuery(value: String) { query.value = value }
    fun selectFolder(id: String?) { folderFilter.value = id }

    fun saveNote(note: NoteEntity) {
        viewModelScope.launch { repository.saveNote(note.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun saveDraft(id: String, title: String, content: String, folderId: String?, color: Long) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            val existing = repository.getNote(id)
            val now = System.currentTimeMillis()
            val note = NoteEntity(
                id = id,
                title = title.ifBlank { "Untitled note" },
                content = content,
                folderId = folderId,
                colorArgb = color,
                isPinned = existing?.isPinned ?: false,
                isFavorite = existing?.isFavorite ?: false,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )
            repository.saveNote(note)
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch { repository.deleteNote(id) }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch { repository.pinNote(note.id, !note.isPinned) }
    }

    fun toggleFavorite(note: NoteEntity) {
        viewModelScope.launch { repository.favoriteNote(note.id, !note.isFavorite) }
    }

    fun createFolder(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.saveFolder(FolderEntity(UUID.randomUUID().toString(), name.trim()))
        }
    }

    fun deleteFolder(id: String) {
        viewModelScope.launch { repository.deleteFolder(id) }
    }
}
