package com.wanandroid.core.data.repository

import androidx.paging.PagingData
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.HotKey
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun getHotKeys(): Result<List<HotKey>>
    fun getSearchPagingFlow(keyword: String): Flow<PagingData<Article>>
}
