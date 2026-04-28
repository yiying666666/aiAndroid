package com.wanandroid.core.model.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val errorCode: Int = -1,
    val errorMsg: String = "",
    val data: T? = null,
)

class ApiException(val code: Int, override val message: String) : Exception(message)

fun <T> ApiResponse<T>.toResult(): Result<T> =
    if (errorCode == 0 && data != null) Result.success(data)
    else Result.failure(ApiException(errorCode, errorMsg.ifBlank { "请求失败，错误码：$errorCode" }))
