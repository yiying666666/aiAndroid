package com.wanandroid.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int = 0,
    val name: String = "",
    val order: Int = 0,
    val children: List<Category> = emptyList(),
)
