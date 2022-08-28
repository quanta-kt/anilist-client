package com.github.quantakt.anilistclient.data.db.dao

import androidx.room.*
import com.github.quantakt.anilistclient.data.db.dbentities.RemoteMediatorRemoteKey

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: RemoteMediatorRemoteKey)

    @Query("SELECT * FROM RemoteMediatorRemoteKey WHERE label = :label")
    suspend fun getByLabel(label: String): RemoteMediatorRemoteKey?

    @Query("DELETE FROM RemoteMediatorRemoteKey WHERE label = :label")
    suspend fun deleteByLabel(label: String)
}