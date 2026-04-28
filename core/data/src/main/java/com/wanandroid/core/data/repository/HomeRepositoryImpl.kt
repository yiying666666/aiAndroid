package com.wanandroid.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wanandroid.core.data.paging.ArticlePagingSource
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Banner
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: WanApiService,
) : HomeRepository {

    override suspend fun getBanners(): Result<List<Banner>> =
        runCatching { api.getBanners().toResult().getOrThrow() }

    override fun getArticlesPagingFlow(): Flow<PagingData<Article>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { ArticlePagingSource(api) },
        ).flow
}
