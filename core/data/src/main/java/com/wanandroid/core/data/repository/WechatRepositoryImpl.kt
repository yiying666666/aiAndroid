package com.wanandroid.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wanandroid.core.data.paging.WechatPagingSource
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Category
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WechatRepositoryImpl @Inject constructor(
    private val api: WanApiService,
) : WechatRepository {

    override suspend fun getWxChapters(): Result<List<Category>> =
        runCatching { api.getWxChapters().toResult().getOrThrow() }

    override fun getWxArticlesPagingFlow(id: Int): Flow<PagingData<Article>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { WechatPagingSource(api, id) },
        ).flow
}
