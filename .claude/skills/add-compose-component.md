# add-compose-component

在 `core:ui` 中新增可复用的 Compose 组件，供所有 feature 模块引用。

## 触发时机

用户说"新建公共组件"、"抽取组件到 core:ui"、"添加 Composable 组件"、"封装 XXX 组件" 时调用此 skill。

## 执行步骤

### 1. 确认放置位置

`core/ui/src/main/java/com/wanandroid/core/ui/`

| 子目录 | 内容 |
|---|---|
| `component/` | 业务无关的通用 UI 组件 |
| `theme/` | Material3 主题（Color、Typography、Shape） |

### 2. 通用组件文件模板

文件：`core/ui/src/main/java/com/wanandroid/core/ui/component/<Name>.kt`

```kotlin
package com.wanandroid.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wanandroid.core.ui.theme.WanAndroidTheme

/**
 * <组件简介>
 *
 * @param modifier 修饰符，默认为 [Modifier]
 */
@Composable
fun <Name>(
    // 必要参数在前，有默认值的参数在后
    modifier: Modifier = Modifier,
) {
    // 实现
}

// 内部子组件声明为 private
@Composable
private fun <Name>Item(
    modifier: Modifier = Modifier,
) {
    // 实现
}

@Preview(showBackground = true)
@Composable
private fun <Name>Preview() {
    WanAndroidTheme {
        <Name>()
    }
}
```

### 3. 规范要点

| 要点 | 说明 |
|---|---|
| `modifier: Modifier = Modifier` | 所有对外 Composable 必须声明，且有默认值 |
| `@Composable private fun` | 内部子组件使用 `private`，不对外暴露 |
| 无状态 | 公共组件尽量无状态，通过参数接收数据和回调 |
| Material3 | 使用 `MaterialTheme.colorScheme` / `MaterialTheme.typography`，不硬编码颜色 |
| `@Preview` | 每个组件提供至少一个 Preview，使用 `WanAndroidTheme` 包裹 |
| 图片加载 | 使用 `coil.compose` 的 `AsyncImage`，不使用其他图片库 |

### 4. 已有公共组件（避免重复创建）

| 组件名 | 用途 |
|---|---|
| `WanTopAppBar` | 顶部应用栏，支持 `title`、`navigationIcon`、`actions` |
| `ArticleCard` | 文章卡片，含标题、作者、日期、收藏按钮 |
| `BannerPager` | 自动轮播 Banner |
| `LoadingIndicator` | 加载中指示器 |
| `ErrorScreen` | 错误页面，含 `message` 和 `onRetry` 回调 |

### 5. 在 feature 模块中使用

`core:ui` 已被所有 feature 模块通过 convention plugin 自动依赖，直接 import 即可：

```kotlin
import com.wanandroid.core.ui.component.<Name>
```

## 检查清单

- [ ] 文件放在 `core/ui/.../component/`
- [ ] 对外 Composable 声明 `modifier: Modifier = Modifier`
- [ ] 内部子组件标记 `private`
- [ ] 颜色使用 `MaterialTheme.colorScheme`，无硬编码
- [ ] 提供 `@Preview`，用 `WanAndroidTheme` 包裹
- [ ] 无状态设计，数据和回调通过参数传入
