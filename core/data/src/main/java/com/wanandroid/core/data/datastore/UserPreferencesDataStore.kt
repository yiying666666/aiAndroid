package com.wanandroid.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wanandroid.core.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore by preferencesDataStore(name = "wan_user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val keyLoggedIn = booleanPreferencesKey("is_logged_in")
    private val keyUserId = intPreferencesKey("user_id")
    private val keyUsername = stringPreferencesKey("username")
    private val keyNickname = stringPreferencesKey("nickname")
    private val keyIcon = stringPreferencesKey("icon")

    val isLoggedIn: Flow<Boolean> = context.userDataStore.data.map { it[keyLoggedIn] ?: false }

    val currentUser: Flow<User?> = context.userDataStore.data.map { prefs ->
        val id = prefs[keyUserId] ?: return@map null
        if (id == 0) null
        else User(
            id = id,
            username = prefs[keyUsername] ?: "",
            nickname = prefs[keyNickname] ?: "",
            icon = prefs[keyIcon] ?: "",
        )
    }

    suspend fun saveUser(user: User) {
        context.userDataStore.edit { prefs ->
            prefs[keyLoggedIn] = true
            prefs[keyUserId] = user.id
            prefs[keyUsername] = user.username
            prefs[keyNickname] = user.nickname
            prefs[keyIcon] = user.icon
        }
    }

    suspend fun clearUser() {
        context.userDataStore.edit { prefs ->
            prefs[keyLoggedIn] = false
            prefs[keyUserId] = 0
            prefs[keyUsername] = ""
            prefs[keyNickname] = ""
            prefs[keyIcon] = ""
        }
    }
}
