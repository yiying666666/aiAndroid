package com.wanandroid.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.wanandroid.core.data.paging.ProjectPagingSource
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Category
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    private val api: WanApiService,
) : ProjectRepository {

    override suspend fun getProjectCategories(): Result<List<Category>> =
        runCatching { api.getProjectTree().toResult().getOrThrow() }

    override fun getProjectPagingFlow(cid: Int): Flow<PagingData<Article>> =
        Pager(
            config = PagingConfig(pageSize = 15, enablePlaceholders = false),
            pagingSourceFactory = { ProjectPagingSource(api, cid) },
        ).flow
}
