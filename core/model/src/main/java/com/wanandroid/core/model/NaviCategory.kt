package com.wanandroid.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NaviCategory(
    @SerialName("cid") val cid: Int = 0,
    val name: String = "",
    val articles: List<Article> = emptyList(),
)
