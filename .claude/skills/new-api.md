# new-api

在 `WanApiService` 中添加新接口，并将整条数据流（Service → Repository → ViewModel → Screen）串联完整。

## 触发时机

用户说"添加新接口"、"对接 XX API"、"新增 XX 请求"、"调用 XX 接口" 时调用此 skill。

## 执行步骤

### 1. 确认参数

从用户输入或 WanAndroid API 文档（https://wanandroid.com/blog/show/2）中提取：
- HTTP 方法（GET / POST）
- 接口路径（如 `/user/lg/collect/list/{page}/json`）
- 请求参数（Path / Query / Field）
- 响应数据类型（若不存在则需新建 Model）

### 2. 在 WanApiService 中添加接口方法

文件：`core/network/src/main/java/com/wanandroid/core/network/WanApiService.kt`

```kotlin
// GET 示例
@GET("/xxx/{page}/json")
suspend fun getXxx(
    @Path("page") page: Int,
    @Query("cid") cid: Int,
): ApiResponse<ArticleListData>

// POST 表单示例
@FormUrlEncoded
@POST("/xxx/json")
suspend fun postXxx(
    @Field("field1") field1: String,
): ApiResponse<Unit>
```

**规范要点：**
- 返回类型必须为 `ApiResponse<T>`（由 `kotlinx.serialization` 反序列化）
- POST 表单接口加 `@FormUrlEncoded`，参数用 `@Field`
- 路径参数用 `@Path`，查询参数用 `@Query`

### 3. 若响应体需要新 Model

在 `core/model/src/main/java/com/wanandroid/core/model/` 下新建数据类：

```kotlin
package com.wanandroid.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XxxData(
    val id: Int = 0,
    val title: String = "",
    // 字段名与 JSON key 不一致时加 @SerialName("json_key")
)
```

**规范要点：**
- 必须标注 `@Serializable`
- 所有字段提供默认值（配合 `coerceInputValues = true`）
- `core:model` 不依赖其他核心模块

### 4. Repository 接口新增方法

文件：`core/data/.../repository/<Name>Repository.kt`

```kotlin
// 普通挂起函数
suspend fun getXxx(): Result<XxxData>

// 分页流
fun getXxxPagingFlow(): Flow<PagingData<Article>>
```

### 5. Repository 实现

文件：`core/data/.../repository/<Name>RepositoryImpl.kt`

```kotlin
// 普通请求
override suspend fun getXxx(): Result<XxxData> =
    runSuspendCatching { api.getXxx().toResult().getOrThrow() }

// 分页请求
override fun getXxxPagingFlow(): Flow<PagingData<Article>> =
    Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { XxxPagingSource(api) },
    ).flow
```

**规范要点：**
- 一律用 `runSuspendCatching { }` 包裹，它会重新抛出 `CancellationException`，捕获其余异常
- 调用 `.toResult().getOrThrow()` 将 `ApiResponse<T>` 转换为 `Result<T>`
- Repository 层不对外抛异常

### 6. 若为分页接口，创建 PagingSource

文件：`core/data/.../paging/<Name>PagingSource.kt`

```kotlin
class <Name>PagingSource(
    private val api: WanApiService,
    // 若需要过滤参数，在此声明
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 0   // 起始页码为 0
        return try {
            val response = api.getXxxList(page).toResult().getOrThrow()
            LoadResult.Page(
                data = response.datas,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.over) null else page + 1,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
```

### 7. ViewModel 消费

```kotlin
// 普通请求
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }
    repository.getXxx()
        .onSuccess { data -> _uiState.update { it.copy(data = data, isLoading = false) } }
        .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
}

// 分页流
val xxxPagingFlow: Flow<PagingData<Article>> =
    repository.getXxxPagingFlow().cachedIn(viewModelScope)
```

## 检查清单

- [ ] `WanApiService` 方法返回 `ApiResponse<T>`
- [ ] 新 Model 标注 `@Serializable`，字段有默认值
- [ ] Repository 实现用 `runSuspendCatching` 包裹
- [ ] 分页起始页码为 `0`，pageSize = 20，enablePlaceholders = false
- [ ] PagingSource 的 `CancellationException` 重新抛出
- [ ] ViewModel 用 `.onSuccess / .onFailure` 处理 Result
- [ ] 分页 Flow 调用 `.cachedIn(viewModelScope)`
