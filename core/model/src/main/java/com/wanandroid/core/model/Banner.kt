package com.wanandroid.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: Int = 0,
    val title: String = "",
    @kotlinx.serialization.SerialName("imagePath") val imagePath: String = "",
    val url: String = "",
    val desc: String = "",
)
