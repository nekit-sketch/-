package com.vortex.launcher.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY id")
    fun observeAll(): Flow<List<ProfileEntity>>

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int

    @Query("SELECT * FROM profiles ORDER BY id LIMIT 1")
    suspend fun firstOrNull(): ProfileEntity?

    @Insert
    suspend fun insert(profile: ProfileEntity): Long

    @Query("UPDATE profiles SET name = :name WHERE id = :id")
    suspend fun rename(id: Long, name: String)

    @Query("UPDATE profiles SET avatarUri = :avatarUri WHERE id = :id")
    suspend fun setAvatar(id: Long, avatarUri: String?)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun delete(id: Long)
}
