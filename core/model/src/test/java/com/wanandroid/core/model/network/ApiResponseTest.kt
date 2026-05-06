package com.wanandroid.core.model.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiResponseTest {

    @Test
    fun `toResult returns success when errorCode is 0 and data is non-null`() {
        val response = ApiResponse(errorCode = 0, data = "hello")
        val result = response.toResult()
        assertTrue(result.isSuccess)
        assertEquals("hello", result.getOrNull())
    }

    @Test
    fun `toResult returns failure when errorCode is not 0`() {
        val response = ApiResponse<String>(errorCode = -1001, errorMsg = "未登录")
        val result = response.toResult()
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as ApiException
        assertEquals(-1001, exception.code)
        assertEquals("未登录", exception.message)
    }

    @Test
    fun `toResult returns failure when data is null even if errorCode is 0`() {
        val response = ApiResponse<String>(errorCode = 0, data = null)
        val result = response.toResult()
        assertTrue(result.isFailure)
    }

    @Test
    fun `toResult uses default message template when errorMsg is blank`() {
        val response = ApiResponse<String>(errorCode = -1, errorMsg = "")
        val result = response.toResult()
        val exception = result.exceptionOrNull() as ApiException
        assertTrue(exception.message.contains("-1"))
    }

    @Test
    fun `runSuspendCatching returns success for normal value`() = runTest {
        val result = runSuspendCatching { 42 }
        assertTrue(result.isSuccess)
        assertEquals(42, result.getOrNull())
    }

    @Test
    fun `runSuspendCatching wraps regular exceptions in failure`() = runTest {
        val result = runSuspendCatching { throw IllegalStateException("boom") }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `runSuspendCatching rethrows CancellationException`() = runTest {
        var thrown = false
        try {
            runSuspendCatching { throw CancellationException("cancelled") }
        } catch (e: CancellationException) {
            thrown = true
        }
        assertTrue(thrown)
    }

    @Test
    fun `ApiException carries correct code and message`() {
        val ex = ApiException(404, "not found")
        assertEquals(404, ex.code)
        assertEquals("not found", ex.message)
    }
}
