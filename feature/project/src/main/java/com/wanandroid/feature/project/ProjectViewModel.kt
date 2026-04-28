package com.wanandroid.feature.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.wanandroid.core.data.repository.ProjectRepository
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

data class ProjectUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectUiState())
    val uiState: StateFlow<ProjectUiState> = _uiState.asStateFlow()

    private val selectedCid = MutableStateFlow(-1)

    val articlePagingFlow: Flow<PagingData<Article>> = selectedCid
        .flatMapLatest { cid -> if (cid == -1) kotlinx.coroutines.flow.flowOf(PagingData.empty()) else projectRepository.getProjectPagingFlow(cid) }
        .cachedIn(viewModelScope)

    init { loadCategories() }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            projectRepository.getProjectCategories()
                .onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories, isLoading = false) }
                    if (categories.isNotEmpty()) selectedCid.value = categories[0].id
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun selectCategory(index: Int) {
        val categories = _uiState.value.categories
        if (index in categories.indices) {
            _uiState.update { it.copy(selectedCategoryIndex = index) }
            selectedCid.value = categories[index].id
        }
    }
}
