package com.wanandroid.core.data.repository

import com.wanandroid.core.data.datastore.UserPreferencesDataStore
import com.wanandroid.core.model.User
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService
import com.wanandroid.core.network.cookie.CookieCleaner
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: WanApiService,
    private val userPrefs: UserPreferencesDataStore,
    private val cookieCleaner: CookieCleaner,
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<User> =
        runCatching {
            api.login(username, password).toResult().getOrThrow()
        }.onSuccess { user -> userPrefs.saveUser(user) }

    override suspend fun register(username: String, password: String, repassword: String): Result<User> =
        runCatching {
            api.register(username, password, repassword).toResult().getOrThrow()
        }.onSuccess { user -> userPrefs.saveUser(user) }

    override suspend fun logout(): Result<Unit> =
        runCatching {
            api.logout()
            userPrefs.clearUser()
            cookieCleaner.clearCookies()
        }
}
