package com.wanandroid.feature.wechat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.wanandroid.core.model.Article
import com.wanandroid.core.ui.component.ArticleCard
import com.wanandroid.core.ui.component.ErrorScreen
import com.wanandroid.core.ui.component.LoadingIndicator
import com.wanandroid.core.ui.component.WanTopAppBar

@Composable
fun WechatScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: WechatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.articlePagingFlow.collectAsLazyPagingItems()

    Scaffold(topBar = { WanTopAppBar(title = "公众号") }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.error != null) {
                ErrorScreen(message = uiState.error!!, onRetry = {})
            } else {
                if (uiState.chapters.isNotEmpty()) {
                    ScrollableTabRow(selectedTabIndex = uiState.selectedIndex) {
                        uiState.chapters.forEachIndexed { index, chapter ->
                            Tab(
                                selected = uiState.selectedIndex == index,
                                onClick = { viewModel.selectChapter(index) },
                                text = { Text(chapter.name) },
                            )
                        }
                    }
                }
                when (val refresh = pagingItems.loadState.refresh) {
                    is LoadState.Loading -> LoadingIndicator()
                    is LoadState.Error -> ErrorScreen(
                        message = refresh.error.message ?: "加载失败",
                        onRetry = { pagingItems.refresh() },
                    )
                    else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                    }
                }
            }
        }
    }
}
