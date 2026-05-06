package com.wanandroid.feature.home

import androidx.paging.PagingData
import com.wanandroid.core.data.repository.HomeRepository
import com.wanandroid.core.model.Banner
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val homeRepository: HomeRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { homeRepository.getArticlesPagingFlow() } returns emptyFlow()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads banners on success`() = runTest {
        val banners = listOf(Banner(id = 1, title = "Banner"))
        coEvery { homeRepository.getBanners() } returns Result.success(banners)

        val viewModel = HomeViewModel(homeRepository)

        assertEquals(banners, viewModel.uiState.value.banners)
        assertFalse(viewModel.uiState.value.isBannerLoading)
        assertNull(viewModel.uiState.value.bannerError)
    }

    @Test
    fun `init sets bannerError on failure`() = runTest {
        coEvery { homeRepository.getBanners() } returns Result.failure(Exception("网络错误"))

        val viewModel = HomeViewModel(homeRepository)

        assertTrue(viewModel.uiState.value.banners.isEmpty())
        assertFalse(viewModel.uiState.value.isBannerLoading)
        assertEquals("网络错误", viewModel.uiState.value.bannerError)
    }

    @Test
    fun `loadBanners can reload banners after initial load`() = runTest {
        val initial = listOf(Banner(id = 1, title = "Old"))
        val refreshed = listOf(Banner(id = 2, title = "New"))
        coEvery { homeRepository.getBanners() } returnsMany listOf(
            Result.success(initial),
            Result.success(refreshed),
        )

        val viewModel = HomeViewModel(homeRepository)
        assertEquals(initial, viewModel.uiState.value.banners)

        viewModel.loadBanners()
        assertEquals(refreshed, viewModel.uiState.value.banners)
    }
}
