package com.sayem.notevault.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class, FolderEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NoteVaultDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao
}
