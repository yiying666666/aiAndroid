package com.wanandroid.core.model

import kotlinx.serialization.Serializable

@Serializable
data class HotKey(
    val id: Int = 0,
    val name: String = "",
    val order: Int = 0,
    val visible: Int = 1,
)
