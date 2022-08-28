package com.github.quantakt.anilistclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.quantakt.anilistclient.data.db.dao.LoggedInUserDao
import com.github.quantakt.anilistclient.data.db.dao.MediaListDao
import com.github.quantakt.anilistclient.data.db.dao.RemoteKeysDao
import com.github.quantakt.anilistclient.data.db.dbentities.*

@Database(
    entities = [
        LoggedInUserEntity::class,

        MediaListItem::class,
        MediaCustomLists::class,
        UserCustomList::class,

        RemoteMediatorRemoteKey::class,
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun loggedInUserDao(): LoggedInUserDao
    abstract fun mediaListDao(): MediaListDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
