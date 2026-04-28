package com.wanandroid.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: Int = 0,
    val title: String = "",
    val link: String = "",
    val author: String = "",
    @SerialName("shareUser") val shareUser: String = "",
    val collect: Boolean = false,
    @SerialName("niceDate") val niceDate: String = "",
    @SerialName("superChapterName") val superChapterName: String = "",
    @SerialName("chapterName") val chapterName: String = "",
    val top: Boolean = false,
    val fresh: Boolean = false,
    @SerialName("envelopePic") val envelopePic: String = "",
    val desc: String = "",
)

val Article.displayAuthor: String get() = author.ifBlank { shareUser }
