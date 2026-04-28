package com.wanandroid.core.data.repository

import com.wanandroid.core.model.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String, repassword: String): Result<User>
    suspend fun logout(): Result<Unit>
}
