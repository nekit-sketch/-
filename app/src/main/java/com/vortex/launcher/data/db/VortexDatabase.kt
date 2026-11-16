package com.vortex.launcher.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProfileEntity::class], version = 1, exportSchema = false)
abstract class VortexDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}
