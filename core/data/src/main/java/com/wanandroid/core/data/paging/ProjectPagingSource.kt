package com.wanandroid.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService

class ProjectPagingSource(
    private val api: WanApiService,
    private val cid: Int,
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = api.getProjectList(page, cid).toResult().getOrThrow()
            LoadResult.Page(
                data = response.datas,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.over) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
