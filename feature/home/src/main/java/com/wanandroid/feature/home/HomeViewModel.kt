package com.wanandroid.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wanandroid.core.data.repository.HomeRepository
import com.wanandroid.core.model.Article
import com.wanandroid.core.model.Banner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val banners: List<Banner> = emptyList(),
    val isBannerLoading: Boolean = false,
    val bannerError: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val articlePagingFlow: Flow<PagingData<Article>> =
        homeRepository.getArticlesPagingFlow().cachedIn(viewModelScope)

    init { loadBanners() }

    fun loadBanners() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBannerLoading = true, bannerError = null) }
            homeRepository.getBanners()
                .onSuccess { banners -> _uiState.update { it.copy(banners = banners, isBannerLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isBannerLoading = false, bannerError = e.message) } }
        }
    }
}
