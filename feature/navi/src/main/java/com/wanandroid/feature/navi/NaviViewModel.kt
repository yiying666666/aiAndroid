package com.wanandroid.feature.navi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.core.data.repository.NaviRepository
import com.wanandroid.core.model.NaviCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NaviUiState(
    val categories: List<NaviCategory> = emptyList(),
    val selectedIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class NaviViewModel @Inject constructor(
    private val naviRepository: NaviRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NaviUiState())
    val uiState: StateFlow<NaviUiState> = _uiState.asStateFlow()

    init { loadNavi() }

    fun loadNavi() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            naviRepository.getNavi()
                .onSuccess { categories -> _uiState.update { it.copy(categories = categories, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun selectCategory(index: Int) = _uiState.update { it.copy(selectedIndex = index) }
}
