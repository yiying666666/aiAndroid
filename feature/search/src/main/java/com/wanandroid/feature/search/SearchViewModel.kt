package com.wanandroid.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wanandroid.core.data.repository.SearchRepository
import com.wanandroid.core.data.repository.SearchRepositoryImpl
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.HotKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val keyword: String = "",
    val hotKeys: List<HotKey> = emptyList(),
    val isLoadingHotKeys: Boolean = false,
    val isSearching: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepositoryImpl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchKeyword = MutableStateFlow("")

    val searchPagingFlow: Flow<PagingData<Article>> = searchKeyword
        .flatMapLatest { kw ->
            if (kw.isBlank()) emptyFlow()
            else searchRepository.getSearchPagingFlow(kw)
        }
        .cachedIn(viewModelScope)

    init { loadHotKeys() }

    private fun loadHotKeys() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingHotKeys = true) }
            searchRepository.getHotKeys()
                .onSuccess { hotKeys -> _uiState.update { it.copy(hotKeys = hotKeys, isLoadingHotKeys = false) } }
                .onFailure { _uiState.update { it.copy(isLoadingHotKeys = false) } }
        }
    }

    fun onKeywordChange(value: String) = _uiState.update { it.copy(keyword = value) }

    fun search() {
        val kw = _uiState.value.keyword.trim()
        if (kw.isBlank()) return
        _uiState.update { it.copy(isSearching = true) }
        searchKeyword.value = kw
    }

    fun onHotKeyClick(hotKey: HotKey) {
        _uiState.update { it.copy(keyword = hotKey.name, isSearching = true) }
        searchKeyword.value = hotKey.name
    }
}

