package com.github.quantakt.anilistclient.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    private object Keys {
        val defaultUserId = intPreferencesKey("default_user_id")
    }

    val defaultUserIdFlow = context.dataStore.data.map { preferences ->
        preferences[Keys.defaultUserId]
    }.distinctUntilChanged()

    suspend fun setDefaultLoggedInUserId(userId: Int) {
        context.dataStore.edit {
            it[Keys.defaultUserId] = userId
        }
    }
}
