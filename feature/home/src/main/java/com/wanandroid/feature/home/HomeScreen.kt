package com.wanandroid.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Banner
import com.wanandroid.core.ui.component.ArticleCard
import com.wanandroid.core.ui.component.BannerPager
import com.wanandroid.core.ui.component.ErrorScreen
import com.wanandroid.core.ui.component.LoadingIndicator
import com.wanandroid.core.ui.component.WanTopAppBar
import com.wanandroid.core.ui.theme.WanAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArticleClick: (Article) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.articlePagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            WanTopAppBar(
                title = "玩Android",
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "搜索", tint = Color.White)
                    }
                },
            )
        },
    ) { padding ->
        val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                pagingItems.refresh()
                viewModel.loadBanners()
            },
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            when (val refresh = pagingItems.loadState.refresh) {
                is LoadState.Error -> ErrorScreen(
                    message = refresh.error.message ?: "加载失败",
                    onRetry = { pagingItems.refresh() },
                )
                else -> ArticleList(
                    banners = uiState.banners,
                    pagingItems = pagingItems,
                    onArticleClick = onArticleClick,
                    onBannerClick = { banner ->
                        onArticleClick(Article(link = banner.url, title = banner.title))
                    },
                )
            }
        }
    }
}

@Composable
private fun ArticleList(
    banners: List<Banner>,
    pagingItems: LazyPagingItems<Article>,
    onArticleClick: (Article) -> Unit,
    onBannerClick: (Banner) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            if (banners.isNotEmpty()) {
                BannerPager(
                    banners = banners,
                    onBannerClick = onBannerClick,
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                )
            }
        }
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.id },
        ) { index ->
            val article = pagingItems[index] ?: return@items
            ArticleCard(
                article = article,
                onArticleClick = onArticleClick,
                onCollectClick = {},
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        }
        item {
            when (val append = pagingItems.loadState.append) {
                is LoadState.Loading -> Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    LoadingIndicator()
                }
                is LoadState.Error -> ErrorScreen(
                    message = append.error.message ?: "加载失败",
                    onRetry = { pagingItems.retry() },
                )
                else -> Unit
            }
        }
    }
}

private val previewBanners = listOf(
    Banner(id = 1, title = "Jetpack Compose 最佳实践", imagePath = "", url = ""),
    Banner(id = 2, title = "Kotlin 协程深度解析", imagePath = "", url = ""),
)

private val previewArticles = listOf(
    Article(id = 1, title = "Android 15 新特性详解", author = "鸿洋", niceDate = "2024-01-01", chapterName = "官方"),
    Article(id = 2, title = "Jetpack Compose 性能优化", author = "扔物线", niceDate = "2024-01-02", chapterName = "Compose"),
    Article(id = 3, title = "Kotlin Flow 实战指南", author = "bennyhuo", niceDate = "2024-01-03", chapterName = "Kotlin"),
    Article(id = 4, title = "Hilt 依赖注入实战", author = "鸿洋", niceDate = "2024-01-04", chapterName = "架构", top = true),
    Article(id = 5, title = "Room 数据库最佳实践", author = "Jake Wharton", niceDate = "2024-01-05", chapterName = "数据库", fresh = true),
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    WanAndroidTheme {
        Scaffold(
            topBar = {
                WanTopAppBar(
                    title = "玩Android",
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Search, contentDescription = "搜索", tint = Color.White)
                        }
                    },
                )
            },
        ) { padding ->
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                item {
                    BannerPager(
                        banners = previewBanners,
                        onBannerClick = {},
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                    )
                }
                items(previewArticles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onArticleClick = {},
                        onCollectClick = {},
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
