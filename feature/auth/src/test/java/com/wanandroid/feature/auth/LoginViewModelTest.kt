package com.wanandroid.feature.auth

import com.wanandroid.core.data.repository.AuthRepository
import com.wanandroid.core.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with blank username sets errorMessage without calling repository`() = runTest {
        viewModel.onUsernameChange("")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        assertEquals("用户名不能为空", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `login with short password sets errorMessage without calling repository`() = runTest {
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("12345")
        viewModel.login()
        assertEquals("密码不能少于6位", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `login success sets loginSuccess true and clears loading`() = runTest {
        val user = User(id = 1, username = "user")
        coEvery { authRepository.login("user", "123456") } returns Result.success(user)
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        assertTrue(viewModel.uiState.value.loginSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `login failure sets errorMessage and keeps loginSuccess false`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception("登录失败"))
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        assertEquals("登录失败", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.loginSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onLoginNavigated resets loginSuccess to false`() = runTest {
        val user = User(id = 1, username = "user")
        coEvery { authRepository.login("user", "123456") } returns Result.success(user)
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        assertTrue(viewModel.uiState.value.loginSuccess)
        viewModel.onLoginNavigated()
        assertFalse(viewModel.uiState.value.loginSuccess)
    }

    @Test
    fun `onUsernameChange clears errorMessage`() {
        viewModel.onUsernameChange("")
        viewModel.login()
        viewModel.onUsernameChange("new")
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
