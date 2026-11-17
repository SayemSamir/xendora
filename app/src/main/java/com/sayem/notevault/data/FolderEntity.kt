package com.sayem.notevault.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorArgb: Long = 0xFF8B7CFF,
    val createdAt: Long = System.currentTimeMillis()
)
