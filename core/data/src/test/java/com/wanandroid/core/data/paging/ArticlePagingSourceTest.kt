package com.wanandroid.core.data.paging

import androidx.paging.PagingSource
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.ArticleListData
import com.wanandroid.core.model.network.ApiResponse
import com.wanandroid.core.network.WanApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ArticlePagingSourceTest {

    private val api: WanApiService = mockk()
    private val source = ArticlePagingSource(api)

    private fun params(page: Int) = PagingSource.LoadParams.Refresh(
        key = page,
        loadSize = 20,
        placeholdersEnabled = false,
    )

    @Test
    fun `load first page has null prevKey and nextKey 1 when not over`() = runTest {
        val articles = listOf(Article(id = 1))
        coEvery { api.getArticleList(0) } returns ApiResponse(
            errorCode = 0,
            data = ArticleListData(datas = articles, over = false),
        )

        val result = source.load(params(0)) as PagingSource.LoadResult.Page

        assertNull(result.prevKey)
        assertEquals(1, result.nextKey)
        assertEquals(articles, result.data)
    }

    @Test
    fun `load middle page has correct prevKey and nextKey`() = runTest {
        coEvery { api.getArticleList(3) } returns ApiResponse(
            errorCode = 0,
            data = ArticleListData(datas = emptyList(), over = false),
        )

        val result = source.load(params(3)) as PagingSource.LoadResult.Page

        assertEquals(2, result.prevKey)
        assertEquals(4, result.nextKey)
    }

    @Test
    fun `load last page has null nextKey when over is true`() = runTest {
        coEvery { api.getArticleList(5) } returns ApiResponse(
            errorCode = 0,
            data = ArticleListData(datas = emptyList(), over = true),
        )

        val result = source.load(params(5)) as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `load returns Error on IOException`() = runTest {
        coEvery { api.getArticleList(any()) } throws IOException("timeout")

        val result = source.load(params(0))

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `load rethrows CancellationException`() = runTest {
        coEvery { api.getArticleList(any()) } throws CancellationException("cancelled")

        var thrown = false
        try {
            source.load(params(0))
        } catch (e: CancellationException) {
            thrown = true
        }
        assertTrue(thrown)
    }

    @Test
    fun `load returns Error on api error code`() = runTest {
        coEvery { api.getArticleList(any()) } returns ApiResponse(
            errorCode = -1,
            errorMsg = "请求失败",
        )

        val result = source.load(params(0))

        assertTrue(result is PagingSource.LoadResult.Error)
    }
}
