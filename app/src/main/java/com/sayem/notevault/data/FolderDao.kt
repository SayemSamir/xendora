package com.sayem.notevault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders ORDER BY name COLLATE NOCASE ASC")
    fun observeFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun delete(id: String)

    @Query("UPDATE notes SET folderId = NULL WHERE folderId = :folderId")
    suspend fun detachNotes(folderId: String)
}
