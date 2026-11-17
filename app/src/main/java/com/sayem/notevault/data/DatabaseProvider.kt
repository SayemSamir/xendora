package com.sayem.notevault.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile private var INSTANCE: NoteVaultDatabase? = null

    fun get(context: Context): NoteVaultDatabase =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                NoteVaultDatabase::class.java,
                "notevault.db"
            ).build().also { INSTANCE = it }
        }
}
