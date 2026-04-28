package com.wanandroid.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
