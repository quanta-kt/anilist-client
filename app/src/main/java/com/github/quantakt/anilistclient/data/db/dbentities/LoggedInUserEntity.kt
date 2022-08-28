package com.github.quantakt.anilistclient.data.db.dbentities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.quantakt.anilistclient.domain.entities.LoggedInUser

@Entity(tableName = "LoggedInUsers")
data class LoggedInUserEntity(
    @PrimaryKey
    override val id: Int,
    override val token: String
) : LoggedInUser
