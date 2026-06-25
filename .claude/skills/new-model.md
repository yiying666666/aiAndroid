# new-model

在 `core:model` 中新建可序列化数据类，遵循 `kotlinx.serialization` 规范。

## 触发时机

用户说"新建 Model"、"添加数据类"、"创建 XXX 数据结构" 时调用此 skill。

## 执行步骤

### 1. 确认参数

- `<Name>`：数据类名称
- JSON 字段列表（名称、类型、是否必填）
- 是否为列表容器（如 `ArticleListData`）

### 2. 普通 Model 模板

文件：`core/model/src/main/java/com/wanandroid/core/model/<Name>.kt`

```kotlin
package com.wanandroid.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class <Name>(
    val id: Int = 0,
    val title: String = "",
    // JSON key 与属性名不同时使用 @SerialName
    @SerialName("nice_date")
    val niceDate: String = "",
)
```

### 3. 列表容器 Model 模板（含分页信息）

```kotlin
@Serializable
data class <Name>ListData(
    val curPage: Int = 0,
    val datas: List<<Name>> = emptyList(),
    val offset: Int = 0,
    val over: Boolean = false,
    val pageCount: Int = 0,
    val size: Int = 0,
    val total: Int = 0,
)
```

### 4. 在 `WanApiService` 更新导入

若新建了列表容器 Model，在 `WanApiService` 中：

```kotlin
import com.wanandroid.core.model.<Name>ListData

@GET("/xxx/{page}/json")
suspend fun getXxxList(@Path("page") page: Int): ApiResponse<<Name>ListData>
```

## 规范要点

| 要点 | 说明 |
|---|---|
| `@Serializable` | 必须标注，配合 KSP 生成序列化代码 |
| 字段默认值 | 所有字段提供默认值，配合 `coerceInputValues = true` 避免 null crash |
| `@SerialName` | JSON key 与 Kotlin 属性名不一致时使用 |
| 无其他依赖 | `core:model` 不依赖 `core:network`、`core:data`、`core:ui` |
| 可序列化集合 | List 字段默认值用 `emptyList()`，Map 字段用 `emptyMap()` |

## 检查清单

- [ ] 标注 `@Serializable`
- [ ] 所有字段提供默认值
- [ ] 驼峰与下划线不一致的字段加 `@SerialName`
- [ ] 文件放在 `core/model/src/main/java/com/wanandroid/core/model/`
- [ ] 不引入其他核心模块依赖
