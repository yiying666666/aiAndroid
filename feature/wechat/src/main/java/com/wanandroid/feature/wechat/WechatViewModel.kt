package com.wanandroid.feature.wechat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wanandroid.core.data.repository.WechatRepository
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WechatUiState(
    val chapters: List<Category> = emptyList(),
    val selectedIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WechatViewModel @Inject constructor(
    private val wechatRepository: WechatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WechatUiState())
    val uiState: StateFlow<WechatUiState> = _uiState.asStateFlow()

    private val selectedId = MutableStateFlow(-1)

    val articlePagingFlow: Flow<PagingData<Article>> = selectedId
        .flatMapLatest { id -> if (id == -1) kotlinx.coroutines.flow.flowOf(PagingData.empty()) else wechatRepository.getWxArticlesPagingFlow(id) }
        .cachedIn(viewModelScope)

    init { loadChapters() }

    private fun loadChapters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            wechatRepository.getWxChapters()
                .onSuccess { chapters ->
                    _uiState.update { it.copy(chapters = chapters, isLoading = false) }
                    if (chapters.isNotEmpty()) selectedId.value = chapters[0].id
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun selectChapter(index: Int) {
        val chapters = _uiState.value.chapters
        if (index in chapters.indices) {
            _uiState.update { it.copy(selectedIndex = index) }
            selectedId.value = chapters[index].id
        }
    }
}
