# new-repository

为已有 feature 模块新建或完善 Repository（接口 + 实现 + Hilt 绑定），不涉及新 feature 模块脚手架。

## 触发时机

用户说"新建 Repository"、"添加数据层"、"创建 XXXRepository" 时调用此 skill。

## 执行步骤

### 1. 确认参数

- `<Name>`：Repository 名称前缀（如 `Bookmark`）
- 需要哪些数据操作方法（普通挂起 / 分页 Flow）
- 是否依赖 `UserPreferencesDataStore`（需要登录上下文时注入）

### 2. Repository 接口

文件：`core/data/src/main/java/com/wanandroid/core/data/repository/<Name>Repository.kt`

```kotlin
package com.wanandroid.core.data.repository

import androidx.paging.PagingData
import com.wanandroid.core.model.Article
import kotlinx.coroutines.flow.Flow

interface <Name>Repository {
    suspend fun getXxx(): Result<XxxData>
    fun getXxxPagingFlow(): Flow<PagingData<Article>>
}
```

### 3. Repository 实现

文件：`core/data/src/main/java/com/wanandroid/core/data/repository/<Name>RepositoryImpl.kt`

```kotlin
package com.wanandroid.core.data.repository

import com.wanandroid.core.model.network.runSuspendCatching
import com.wanandroid.core.model.network.toResult
import com.wanandroid.core.network.WanApiService
import javax.inject.Inject

class <Name>RepositoryImpl @Inject constructor(
    private val api: WanApiService,
    // 需要登录上下文时注入：
    // private val userPreferences: UserPreferencesDataStore,
) : <Name>Repository {

    override suspend fun getXxx(): Result<XxxData> =
        runSuspendCatching { api.getXxx().toResult().getOrThrow() }
}
```

### 4. 注册 Hilt 绑定

文件：`core/data/src/main/java/com/wanandroid/core/data/di/DataModule.kt`

在 `DataModule` 中添加：

```kotlin
@Binds @Singleton
abstract fun bind<Name>Repository(impl: <Name>RepositoryImpl): <Name>Repository
```

### 5. 分页 Repository 实现

若含分页场景，实现如下：

```kotlin
override fun getXxxPagingFlow(): Flow<PagingData<Article>> =
    Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { <Name>PagingSource(api) },
    ).flow
```

同时新建 `core/data/.../paging/<Name>PagingSource.kt`（参见 `new-api` skill）。

## 检查清单

- [ ] 接口文件和实现文件均放在 `core/data/.../repository/`
- [ ] 实现类用 `@Inject constructor` 注入依赖
- [ ] `DataModule` 添加了 `@Binds @Singleton` 绑定
- [ ] 普通请求用 `runSuspendCatching { api.xxx().toResult().getOrThrow() }`
- [ ] 分页使用 `PagingConfig(pageSize = 20, enablePlaceholders = false)`
- [ ] 分页起始页码 `params.key ?: 0`
