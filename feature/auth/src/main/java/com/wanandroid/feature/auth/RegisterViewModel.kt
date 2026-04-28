package com.wanandroid.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.core.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val repassword: String = "",
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) = _uiState.update { it.copy(username = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onRepasswordChange(value: String) = _uiState.update { it.copy(repassword = value, errorMessage = null) }

    fun register() {
        val state = _uiState.value
        when {
            state.username.isBlank() -> { _uiState.update { it.copy(errorMessage = "用户名不能为空") }; return }
            state.password.length < 6 -> { _uiState.update { it.copy(errorMessage = "密码不能少于6位") }; return }
            state.password != state.repassword -> { _uiState.update { it.copy(errorMessage = "两次密码不一致") }; return }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.register(state.username, state.password, state.repassword)
                .onSuccess { _uiState.update { it.copy(isLoading = false, registerSuccess = true) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }
}
