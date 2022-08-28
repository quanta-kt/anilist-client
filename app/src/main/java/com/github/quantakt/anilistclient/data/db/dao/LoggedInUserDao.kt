package com.github.quantakt.anilistclient.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.quantakt.anilistclient.data.db.dbentities.LoggedInUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoggedInUserDao {

    @Query("SELECT * FROM LoggedInUsers")
    suspend fun getAll(): List<LoggedInUserEntity>

    @Query("SELECT * FROM LoggedInUsers WHERE id = :id")
    fun getById(id: Int): Flow<LoggedInUserEntity?>

    @Query("DELETE FROM LoggedInUsers WHERE id = :id")
    suspend fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: LoggedInUserEntity)
}