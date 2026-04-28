package com.wanandroid.core.data.repository

import androidx.paging.PagingData
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Banner
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getBanners(): Result<List<Banner>>
    fun getArticlesPagingFlow(): Flow<PagingData<Article>>
}
