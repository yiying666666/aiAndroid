# new-feature

新建一个完整的 feature 模块（Screen + ViewModel），严格遵循项目分层规范。

## 触发时机

用户说"新建 feature"、"添加 XXX 功能模块"、"创建 XXX 页面"、"新增 XXX Screen" 时调用此 skill。

## 执行步骤

### 1. 确认参数

从用户输入中提取：
- `<name>`：模块名（小写，如 `bookmark`）
- `<Name>`：类名前缀（首字母大写，如 `Bookmark`）
- 是否需要分页（Paging）
- 是否需要新增 API 接口

如信息不足，先询问。

### 2. 创建模块目录结构

```
feature/<name>/
├── build.gradle.kts
└── src/
    └── main/
        ├── AndroidManifest.xml
        └── java/com/wanandroid/feature/<name>/
            ├── <Name>Screen.kt
            └── <Name>ViewModel.kt
```

### 3. build.gradle.kts 模板

```kotlin
plugins {
    alias(libs.plugins.wanandroid.android.feature)
}

android {
    namespace = "com.wanandroid.feature.<name>"
}
```

> 使用 `libs.plugins.wanandroid.android.feature` convention plugin，它已包含 compose、hilt、kotlin-serialization 等依赖。

### 4. AndroidManifest.xml 模板

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

### 5. UiState + ViewModel 模板

```kotlin
package com.wanandroid.feature.<name>

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wanandroid.core.data.repository.<Name>Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class <Name>UiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    // TODO: 添加具体状态字段
)

@HiltViewModel
class <Name>ViewModel @Inject constructor(
    private val <name>Repository: <Name>Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(<Name>UiState())
    val uiState: StateFlow<<Name>UiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            <name>Repository.get<Name>()
                .onSuccess { data ->
                    _uiState.update { it.copy(isLoading = false /*, TODO: 更新数据 */) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
```

**ViewModel 规范要点：**
- 状态更新一律用 `_uiState.update { it.copy(...) }`，禁止直接赋值
- 对外只暴露 `.asStateFlow()` 只读流
- 分页场景额外声明 `val <name>PagingFlow: Flow<PagingData<T>> = repo.get<Name>PagingFlow().cachedIn(viewModelScope)`

### 6. Screen 模板

```kotlin
package com.wanandroid.feature.<name>

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wanandroid.core.ui.component.ErrorScreen
import com.wanandroid.core.ui.component.LoadingIndicator
import com.wanandroid.core.ui.component.WanTopAppBar

@Composable
fun <Name>Screen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: <Name>ViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            WanTopAppBar(
                title = "<Name>",
                navigationIcon = { /* 返回按钮 */ },
            )
        },
        modifier = modifier,
    ) { padding ->
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorScreen(
                message = uiState.error!!,
                onRetry = viewModel::load,
            )
            else -> {
                // TODO: 实现具体内容
            }
        }
    }
}
```

**Screen 规范要点：**
- 通过 lambda 参数接收导航回调，**不传入 NavController**
- 必须声明 `modifier: Modifier = Modifier` 默认参数
- 无状态设计，所有状态通过 `collectAsStateWithLifecycle()` 流入
- 内部子 Composable 声明为 `@Composable private fun`
- 提供带 `@Preview` 的预览函数

### 7. 注册到 settings.gradle.kts

在 `settings.gradle.kts` 末尾添加：

```kotlin
include(":feature:<name>")
```

### 8. 注册到 app 模块

在 `app/build.gradle.kts` 的 dependencies 块添加：

```kotlin
implementation(project(":feature:<name>"))
```

### 9. 注册到导航

在 `app/src/main/java/com/wanandroid/app/navigation/AppNavigation.kt` 中：
1. 向 `AppRoute` 或 `BottomNavItem` 密封类添加路由对象
2. 在 NavHost 中添加 `composable` 路由条目，传入 lambda 导航回调

### 10. 若需要新 Repository

依次创建：
1. `core/data/.../repository/<Name>Repository.kt`（接口）
2. `core/data/.../repository/<Name>RepositoryImpl.kt`（实现，注入 `WanApiService`，用 `runSuspendCatching` 包裹网络调用）
3. 在 `DataModule` 中添加 `@Binds @Singleton abstract fun bind<Name>Repository(...): <Name>Repository`
4. 若需要分页，创建 `core/data/.../paging/<Name>PagingSource.kt`（起始页码为 `0`）

### 11. 若需要新 API 接口

在 `WanApiService` 中按 Retrofit 注解规范添加接口方法，返回类型为 `ApiResponse<T>`。

## 检查清单

- [ ] `build.gradle.kts` 使用正确的 convention plugin
- [ ] ViewModel 使用 `@HiltViewModel` + `@Inject constructor`
- [ ] 状态用 `_uiState.update { it.copy(...) }`
- [ ] Screen 通过 lambda 接收导航回调
- [ ] Screen 包含 `modifier: Modifier = Modifier`
- [ ] 分页起始页码为 `0`，`pageSize = 20`，`enablePlaceholders = false`
- [ ] Repository 实现用 `runSuspendCatching` 包裹网络调用
- [ ] DataModule 添加了 `@Binds @Singleton` 绑定
- [ ] settings.gradle.kts 已 include 新模块
- [ ] app 模块已添加依赖
- [ ] 导航已注册