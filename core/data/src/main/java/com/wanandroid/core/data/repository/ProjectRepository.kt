package com.wanandroid.core.data.repository

import androidx.paging.PagingData
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Category
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    suspend fun getProjectCategories(): Result<List<Category>>
    fun getProjectPagingFlow(cid: Int): Flow<PagingData<Article>>
}
