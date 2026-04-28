package com.wanandroid.core.network

import com.wanandroid.core.model.ArticleListData
import com.wanandroid.core.model.Banner
import com.wanandroid.core.model.Category
import com.wanandroid.core.model.HotKey
import com.wanandroid.core.model.NaviCategory
import com.wanandroid.core.model.User
import com.wanandroid.core.model.network.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface WanApiService {

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): ApiResponse<User>

    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String,
    ): ApiResponse<User>

    @GET("/user/logout/json")
    suspend fun logout(): ApiResponse<Unit>

    @GET("/banner/json")
    suspend fun getBanners(): ApiResponse<List<Banner>>

    @GET("/article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): ApiResponse<ArticleListData>

    @GET("/article/top/json")
    suspend fun getTopArticles(): ApiResponse<List<com.wanandroid.core.model.Article>>

    @GET("/navi/json")
    suspend fun getNavi(): ApiResponse<List<NaviCategory>>

    @GET("/tree/json")
    suspend fun getTree(): ApiResponse<List<Category>>

    @GET("/project/tree/json")
    suspend fun getProjectTree(): ApiResponse<List<Category>>

    @GET("/project/list/{page}/json")
    suspend fun getProjectList(
        @Path("page") page: Int,
        @Query("cid") cid: Int,
    ): ApiResponse<ArticleListData>

    @GET("/wxarticle/chapters/json")
    suspend fun getWxChapters(): ApiResponse<List<Category>>

    @GET("/wxarticle/list/{id}/{page}/json")
    suspend fun getWxArticleList(
        @Path("id") id: Int,
        @Path("page") page: Int,
    ): ApiResponse<ArticleListData>

    @POST("/lg/collect/{id}/json")
    suspend fun collectArticle(@Path("id") id: Int): ApiResponse<Unit>

    @POST("/lg/uncollect_originId/{id}/json")
    suspend fun uncollectArticle(@Path("id") id: Int): ApiResponse<Unit>

    @GET("/hotkey/json")
    suspend fun getHotKeys(): ApiResponse<List<HotKey>>

    @GET("/article/query/{page}/json")
    suspend fun searchArticles(
        @Path("page") page: Int,
        @Query("k") keyword: String,
    ): ApiResponse<ArticleListData>
}
