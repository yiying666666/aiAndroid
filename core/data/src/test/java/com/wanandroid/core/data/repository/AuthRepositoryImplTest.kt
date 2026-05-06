package com.wanandroid.core.data.repository

import com.wanandroid.core.data.datastore.UserPreferencesDataStore
import com.wanandroid.core.model.User
import com.wanandroid.core.model.network.ApiException
import com.wanandroid.core.model.network.ApiResponse
import com.wanandroid.core.network.WanApiService
import com.wanandroid.core.network.cookie.CookieCleaner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryImplTest {

    private val api: WanApiService = mockk()
    private val userPrefs: UserPreferencesDataStore = mockk(relaxed = true)
    private val cookieCleaner: CookieCleaner = mockk(relaxed = true)
    private val repository = AuthRepositoryImpl(api, userPrefs, cookieCleaner)

    @Test
    fun `login success saves user and returns success`() = runTest {
        val user = User(id = 1, username = "test")
        coEvery { api.login("test", "123456") } returns ApiResponse(errorCode = 0, data = user)

        val result = repository.login("test", "123456")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { userPrefs.saveUser(user) }
    }

    @Test
    fun `login api error returns failure and does not save user`() = runTest {
        coEvery { api.login(any(), any()) } returns ApiResponse(errorCode = -1001, errorMsg = "账号或密码错误")

        val result = repository.login("test", "wrong")

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as ApiException
        assertEquals(-1001, exception.code)
        coVerify(exactly = 0) { userPrefs.saveUser(any()) }
    }

    @Test
    fun `login exception returns failure and does not save user`() = runTest {
        coEvery { api.login(any(), any()) } throws RuntimeException("timeout")

        val result = repository.login("test", "123456")

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { userPrefs.saveUser(any()) }
    }

    @Test
    fun `logout calls api clearUser and clearCookies`() = runTest {
        coEvery { api.logout() } returns ApiResponse(errorCode = 0, data = Unit)

        val result = repository.logout()

        assertTrue(result.isSuccess)
        coVerify { userPrefs.clearUser() }
        coVerify { cookieCleaner.clearCookies() }
    }

    @Test
    fun `logout clears local data even when api throws`() = runTest {
        coEvery { api.logout() } throws RuntimeException("network error")

        val result = repository.logout()

        assertTrue(result.isSuccess)
        coVerify { userPrefs.clearUser() }
        coVerify { cookieCleaner.clearCookies() }
    }

    @Test
    fun `register success saves user and returns success`() = runTest {
        val user = User(id = 2, username = "newuser")
        coEvery { api.register("newuser", "123456", "123456") } returns ApiResponse(errorCode = 0, data = user)

        val result = repository.register("newuser", "123456", "123456")

        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { userPrefs.saveUser(user) }
    }
}
