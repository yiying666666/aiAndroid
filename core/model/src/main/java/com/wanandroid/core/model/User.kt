package com.wanandroid.core.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val username: String = "",
    val nickname: String = "",
    val icon: String = "",
    val email: String = "",
    val type: Int = 0,
)
