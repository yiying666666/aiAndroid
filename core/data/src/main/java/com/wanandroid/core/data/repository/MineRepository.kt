package com.wanandroid.core.data.repository

import com.wanandroid.core.model.HotKey
import com.wanandroid.core.model.User
import kotlinx.coroutines.flow.Flow

interface MineRepository {
    val currentUser: Flow<User?>
    val isLoggedIn: Flow<Boolean>
    suspend fun getCollectedArticles(): Result<Unit>
}

interface SearchRepository {
    suspend fun getHotKeys(): Result<List<HotKey>>
}
