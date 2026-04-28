package com.wanandroid.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleListData(
    val datas: List<Article> = emptyList(),
    @SerialName("curPage") val curPage: Int = 0,
    val total: Int = 0,
    @SerialName("pageCount") val pageCount: Int = 0,
    val offset: Int = 0,
    val size: Int = 20,
    val over: Boolean = false,
)
