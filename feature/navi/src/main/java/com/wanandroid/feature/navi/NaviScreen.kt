package com.wanandroid.feature.navi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.core.model.Article
import com.wanandroid.core.ui.component.ErrorScreen
import com.wanandroid.core.ui.component.LoadingIndicator
import com.wanandroid.core.ui.component.WanTopAppBar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NaviScreen(
    onArticleClick: (Article) -> Unit,
    viewModel: NaviViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { WanTopAppBar(title = "导航") }) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorScreen(message = uiState.error!!, onRetry = viewModel::loadNavi)
            else -> Row(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    itemsIndexed(uiState.categories) { index, category ->
                        val selected = uiState.selectedIndex == index
                        Text(
                            text = category.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.selectCategory(index) }
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                val selectedCategory = uiState.categories.getOrNull(uiState.selectedIndex)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        selectedCategory?.let { category ->
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    category.articles.forEach { article ->
                                        AssistChip(
                                            onClick = { onArticleClick(article) },
                                            label = { Text(article.title, style = MaterialTheme.typography.bodySmall) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
