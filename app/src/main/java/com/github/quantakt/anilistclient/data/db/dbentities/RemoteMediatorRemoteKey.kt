package com.github.quantakt.anilistclient.data.db.dbentities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteMediatorRemoteKey(
    @PrimaryKey val label: String,
    val nextKey: Int?,
)
