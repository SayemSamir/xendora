package com.sayem.notevault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun observeNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Query("UPDATE notes SET isDeleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun moveToTrash(id: String, now: Long)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun permanentlyDelete(id: String)

    @Query("UPDATE notes SET isPinned = :pinned, updatedAt = :now WHERE id = :id")
    suspend fun setPinned(id: String, pinned: Boolean, now: Long)

    @Query("UPDATE notes SET isFavorite = :favorite, updatedAt = :now WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean, now: Long)

    @Query("SELECT COUNT(*) FROM notes WHERE isDeleted = 0")
    fun observeCount(): Flow<Int>
}
