package com.wanandroid.core.data.repository

import androidx.paging.PagingData
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Category
import kotlinx.coroutines.flow.Flow

interface WechatRepository {
    suspend fun getWxChapters(): Result<List<Category>>
    fun getWxArticlesPagingFlow(id: Int): Flow<PagingData<Article>>
}
