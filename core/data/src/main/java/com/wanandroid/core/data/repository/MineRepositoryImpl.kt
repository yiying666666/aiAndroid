package com.wanandroid.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wanandroid.core.data.datastore.UserPreferencesDataStore
import com.wanandroid.core.data.paging.SearchPagingSource
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.HotKey
import com.wanandroid.core.model.User
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MineRepositoryImpl @Inject constructor(
    private val userPrefs: UserPreferencesDataStore,
) : MineRepository {

    override val currentUser: Flow<User?> = userPrefs.currentUser
    override val isLoggedIn: Flow<Boolean> = userPrefs.isLoggedIn
    override suspend fun getCollectedArticles(): Result<Unit> = Result.success(Unit)
}

class SearchRepositoryImpl @Inject constructor(
    private val api: WanApiService,
) : SearchRepository {

    override suspend fun getHotKeys(): Result<List<HotKey>> =
        runCatching { api.getHotKeys().toResult().getOrThrow() }

    fun getSearchPagingFlow(keyword: String): Flow<PagingData<Article>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(api, keyword) },
        ).flow
}
