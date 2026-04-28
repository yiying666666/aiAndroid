# CLAUDE.md

本文件为 Claude Code（claude.ai/code）在该仓库中工作时提供指导。

## 项目简介

基于 **组件化 + 模块化 + Kotlin + 协程 + Flow + Retrofit + Jetpack Compose + MVVM** 架构实现的 [WanAndroid](https://wanandroid.com) 客户端。API 文档见 [wanandroid.com/blog/show/2](https://wanandroid.com/blog/show/2)，接口 Base URL 为 `https://wanandroid.com`。

---

## 构建命令

```bash
./gradlew assembleDebug            # 构建 Debug APK
./gradlew assembleRelease          # 构建 Release APK（已开启混淆）
./gradlew test                     # 运行所有单元测试
./gradlew connectedAndroidTest     # 运行仪器测试（需连接设备）
./gradlew :feature:home:test       # 运行单个模块的单元测试
```

---

## 整体架构

多模块 **MVVM + Repository** 模式，使用 Hilt 依赖注入，UI 层全面采用 Jetpack Compose。

### 模块依赖图

```
app
├── feature:auth / home / project / wechat / navi / mine / search / article
├── core:ui          （Compose 公共组件、Material3 主题）
├── core:data        （Repository、Paging、DataStore）
│   └── core:network （Retrofit、OkHttp、Cookie 持久化）
└── core:model       （可序列化数据类，无其他依赖）
```

- `core:model` 不依赖任何其他核心模块，是最底层的数据定义层
- 功能模块统一依赖 `core:ui` + `core:data` + `core:model`
- `feature:article` 仅依赖 `core:ui`（WebView 展示，刻意保持轻量）

---

## 数据流

```
Screen（collectAsStateWithLifecycle）
  → ViewModel（MutableStateFlow<*UiState>，viewModelScope）
  → Repository（suspend 函数，返回 Result<T>）
  → WanApiService（Retrofit 接口，ApiResponse<T>.toResult()）
```

- API 响应统一用 `ApiResponse<T>` 包裹；`.toResult()` 扩展函数将其转换为 `Result<T>`，当 `errorCode != 0` 时抛出 `ApiException`
- Repository 层**不对外抛异常**，调用方使用 `.onSuccess / .onFailure` 处理结果
- 分页页面（首页、项目、公众号、搜索）Repository 返回 `Flow<PagingData<T>>`，由自定义 `*PagingSource` 驱动；ViewModel 调用 `.cachedIn(viewModelScope)`，UI 层用 `collectAsLazyPagingItems()` 消费

---

## 核心机制

### 状态管理

- 每个 ViewModel 持有 `MutableStateFlow<*UiState>`（数据类）
- 状态更新统一使用 `_uiState.update { it.copy(...) }`，禁止直接赋值
- 对外只暴露 `.asStateFlow()` 只读流

### 导航

`app/src/main/java/com/wanandroid/app/navigation/AppNavigation.kt` 定义两层导航：

- **根 NavHost**：`AppRoute` 密封类 —— `Login`、`Register`、`Main`、`Search`、`ArticleDetail`
- **嵌套 NavHost**（MainScaffold 内）：`BottomNavItem` 密封类 —— 5 个底部 Tab

启动目标由 `UserPreferencesDataStore.isLoggedIn` 决定；文章详情路由使用 `Uri.encode(url)` 对 URL 参数编码，防止特殊字符破坏路由。

### 依赖注入（Hilt）

- `WanApplication` 标注 `@HiltAndroidApp`，`MainActivity` 标注 `@AndroidEntryPoint`
- 所有 ViewModel 使用 `@HiltViewModel` + `@Inject constructor`
- `DataModule`（`core/data/di/`）：将 Repository 接口绑定到实现类
- `NetworkModule`（`core/network/di/`）：提供 `OkHttpClient`、`Retrofit`、`WanApiService`
- 网络与数据层组件均为 `@Singleton`

### 网络层

- Base URL：`https://wanandroid.com`
- 序列化：`kotlinx.serialization`，配置 `ignoreUnknownKeys = true`、`coerceInputValues = true`
- Cookie：`PersistentCookieJar` 持久化到 DataStore，退出登录时清除
- 日志：Debug 包输出 `Level.BODY`，Release 包 `Level.NONE`；Logcat Tag 为 `WanNetwork`
- 响应拦截器：自动将响应体中的 `https://www.wanandroid.com` 替换为 `https://wanandroid.com`
- 超时：连接 / 读取 / 写入均为 15 秒

### 用户偏好存储

`UserPreferencesDataStore`（DataStore Preferences）存储登录状态、用户 ID、用户名、昵称、头像，注入到 `MainActivity`（用于路由判断）和需要登录上下文的 Repository。

---

## 编码规范

### 命名约定

| 类型 | 命名规则 | 示例 |
|---|---|---|
| 页面 Composable | `*Screen.kt` | `HomeScreen.kt` |
| ViewModel | `*ViewModel.kt` | `HomeViewModel.kt` |
| UI 状态 | `*UiState` 数据类 | `HomeUiState` |
| Repository 接口 | `*Repository` | `AuthRepository` |
| Repository 实现 | `*RepositoryImpl` | `AuthRepositoryImpl` |
| 分页数据源 | `*PagingSource` | `ArticlePagingSource` |
| Hilt 模块 | `*Module` | `DataModule` |

### 功能模块结构

每个 feature 模块严格遵循以下结构：

```
feature/<name>/src/main/java/com/wanandroid/feature/<name>/
├── <Name>Screen.kt     # UI 层，无状态，从 ViewModel 获取数据
└── <Name>ViewModel.kt  # 状态管理，持有 UiState 和业务逻辑
```

### Compose 规范

- 页面级 Composable 通过 **lambda 参数**接收导航回调，不传入 NavController
- 所有 Composable 提供 `modifier: Modifier = Modifier` 默认参数
- 内部子 Composable 声明为 `@Composable private fun`
- 页面为无状态设计，所有状态通过 `collectAsStateWithLifecycle()` 从 ViewModel 流入

### Flow 使用模式

- 搜索、筛选等关键词变化场景使用 `flatMapLatest` 切换新流，避免展示过期结果
- `stateIn(WhileSubscribed(5000))` 将冷流转换为 StateFlow，供 UI 安全订阅

---

## 关键依赖版本

| 依赖 | 版本 | 说明 |
|---|---|---|
| Kotlin | 2.0.21 | `kotlin.code.style=official` |
| Compose BOM | 2024.09.00 | 全面使用 Material3 |
| Hilt | 2.56.1 | 基于 KSP |
| Retrofit | 2.11.0 | 搭配 kotlinx-serialization 转换器 |
| OkHttp | 4.12.0 | |
| Paging | 3.3.6 | 每页 20 条，禁用占位符 |
| Coil | 2.7.0 | 使用 `coil.compose` 加载图片 |
| DataStore | 1.1.4 | Preferences 模式（非 Proto） |
| Coroutines | 1.9.0 | 搭配 Flow 使用 |
