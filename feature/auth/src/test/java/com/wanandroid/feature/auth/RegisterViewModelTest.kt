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
class RegisterViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register with blank username sets errorMessage`() = runTest {
        viewModel.onUsernameChange("")
        viewModel.onPasswordChange("123456")
        viewModel.onRepasswordChange("123456")
        viewModel.register()
        assertEquals("用户名不能为空", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
    }

    @Test
    fun `register with short password sets errorMessage`() = runTest {
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("12345")
        viewModel.onRepasswordChange("12345")
        viewModel.register()
        assertEquals("密码不能少于6位", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
    }

    @Test
    fun `register with mismatched passwords sets errorMessage`() = runTest {
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("123456")
        viewModel.onRepasswordChange("654321")
        viewModel.register()
        assertEquals("两次密码不一致", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any()) }
    }

    @Test
    fun `register success sets registerSuccess true`() = runTest {
        val user = User(id = 2, username = "user")
        coEvery { authRepository.register("user", "123456", "123456") } returns Result.success(user)
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("123456")
        viewModel.onRepasswordChange("123456")
        viewModel.register()
        assertTrue(viewModel.uiState.value.registerSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `register failure sets errorMessage`() = runTest {
        coEvery { authRepository.register(any(), any(), any()) } returns Result.failure(Exception("用户名已存在"))
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("123456")
        viewModel.onRepasswordChange("123456")
        viewModel.register()
        assertEquals("用户名已存在", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.registerSuccess)
    }

    @Test
    fun `onRegisterNavigated resets registerSuccess to false`() = runTest {
        val user = User(id = 2, username = "user")
        coEvery { authRepository.register("user", "123456", "123456") } returns Result.success(user)
        viewModel.onUsernameChange("user")
        viewModel.onPasswordChange("123456")
        viewModel.onRepasswordChange("123456")
        viewModel.register()
        assertTrue(viewModel.uiState.value.registerSuccess)
        viewModel.onRegisterNavigated()
        assertFalse(viewModel.uiState.value.registerSuccess)
    }
}
