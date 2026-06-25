# add-navigation

在 `AppNavigation.kt` 中为已有页面新增导航路由（AppRoute 或 BottomNavItem），或调整现有路由。

## 触发时机

用户说"添加路由"、"注册导航"、"新增 XXX 路由"、"跳转到 XXX 页面" 时调用此 skill。

## 执行步骤

### 1. 确认路由类型

| 类型 | 适用场景 |
|---|---|
| `AppRoute`（根 NavHost）| 全屏页面：登录、注册、搜索、文章详情等 |
| `BottomNavItem`（嵌套 NavHost）| 底部 Tab 页面 |

### 2. 确认是否携带参数

- **无参数**：使用 `object : AppRoute`（`object` 单例）
- **简单参数**（字符串/整数）：用 `/{param}` 路径参数 + `Uri.encode()` 处理特殊字符
- **复杂对象**：将对象序列化后用 `Uri.encode(json)` 传递（避免直接传递 Parcelable）

### 3. 文件位置

`app/src/main/java/com/wanandroid/app/navigation/AppNavigation.kt`

### 4. 无参路由示例

```kotlin
// 1. 在密封类中添加路由对象
sealed class AppRoute(val route: String) {
    // 已有路由...
    object Bookmark : AppRoute("bookmark")
}

// 2. 在 NavHost 中注册
composable(AppRoute.Bookmark.route) {
    BookmarkScreen(
        onBackClick = { navController.popBackStack() },
        onArticleClick = { article ->
            navController.navigate(
                AppRoute.ArticleDetail.createRoute(article.link, article.title)
            )
        },
    )
}
```

### 5. 带参路由示例

```kotlin
// 1. 密封类中定义路由 + 参数
object ArticleDetail : AppRoute("article_detail/{url}/{title}") {
    fun createRoute(url: String, title: String) =
        "article_detail/${Uri.encode(url)}/${Uri.encode(title)}"
}

// 2. NavHost 注册（解析参数）
composable(
    route = AppRoute.ArticleDetail.route,
    arguments = listOf(
        navArgument("url") { type = NavType.StringType },
        navArgument("title") { type = NavType.StringType },
    ),
) { backStackEntry ->
    val url = backStackEntry.arguments?.getString("url") ?: return@composable
    val title = backStackEntry.arguments?.getString("title") ?: return@composable
    ArticleDetailScreen(url = url, title = title, onBackClick = { navController.popBackStack() })
}
```

### 6. 底部 Tab 路由示例

```kotlin
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    // 已有 Tab...
    object Bookmark : BottomNavItem("bookmark", Icons.Filled.BookmarkBorder, "收藏")
}
```

### 7. Splash 跳转逻辑

`SplashScreen` 依赖 `UserPreferencesDataStore.isLoggedIn` 决定目标：
- 已登录 → `AppRoute.Main`
- 未登录 → `AppRoute.Login`
- 均需 `popUpTo(AppRoute.Splash.route) { inclusive = true }` 移除 Splash

## 规范要点

- URL 参数必须用 `Uri.encode()` 编码，防止 `/`、`?`、`#` 破坏路由
- Screen Composable 通过 **lambda** 接收导航回调，不传入 `NavController`
- 返回上一页统一用 `navController.popBackStack()`
- 跳转后需从返回栈移除源页面时用 `popUpTo(...) { inclusive = true }`

## 检查清单

- [ ] 密封类中添加了路由对象
- [ ] NavHost 中注册了对应 `composable` 条目
- [ ] Screen 通过 lambda 回调而非 NavController 处理导航
- [ ] 含路径参数的 URL 使用 `Uri.encode()` 编码
- [ ] Splash 跳转时用 `popUpTo` 移除 Splash 节点
