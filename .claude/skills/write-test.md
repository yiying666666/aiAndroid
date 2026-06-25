# write-test

为指定的 ViewModel、Repository 或 PagingSource 编写单元测试，遵循项目现有测试风格。

## 触发时机

用户说"写单元测试"、"添加测试"、"测试 XXX 类" 时调用此 skill。

## 执行步骤

### 1. 确认测试目标

- 测试类型：ViewModel / Repository / PagingSource
- 待测文件路径

### 2. 测试文件位置

测试文件镜像待测文件，放在对应模块的 `src/test/` 下：

```
feature/home/src/test/java/com/wanandroid/feature/home/HomeViewModelTest.kt
core/data/src/test/java/com/wanandroid/core/data/repository/HomeRepositoryImplTest.kt
core/data/src/test/java/com/wanandroid/core/data/paging/ArticlePagingSourceTest.kt
```

### 3. ViewModel 测试模板

```kotlin
package com.wanandroid.feature.<name>

import app.cash.turbine.test
import com.wanandroid.core.data.repository.<Name>Repository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class <Name>ViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: <Name>Repository = mockk()
    private lateinit var viewModel: <Name>ViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load success updates uiState`() = runTest {
        coEvery { repository.getXxx() } returns Result.success(/* 测试数据 */)
        viewModel = <Name>ViewModel(repository)

        viewModel.uiState.test {
            val initial = awaitItem()
            // 验证初始状态
            assertNull(initial.error)

            testDispatcher.scheduler.advanceUntilIdle()

            val loaded = awaitItem()
            assertEquals(false, loaded.isLoading)
            // 验证数据
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load failure updates error`() = runTest {
        val errorMsg = "网络错误"
        coEvery { repository.getXxx() } returns Result.failure(Exception(errorMsg))
        viewModel = <Name>ViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(errorMsg, viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}
```

### 4. Repository 测试模板

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class <Name>RepositoryImplTest {

    private val api: WanApiService = mockk()
    private val repository = <Name>RepositoryImpl(api)

    @Test
    fun `getXxx returns success when api succeeds`() = runTest {
        val mockData = XxxData(id = 1, title = "test")
        coEvery { api.getXxx() } returns ApiResponse(errorCode = 0, data = mockData, errorMsg = "")

        val result = repository.getXxx()

        assertTrue(result.isSuccess)
        assertEquals(mockData, result.getOrNull())
    }

    @Test
    fun `getXxx returns failure when errorCode != 0`() = runTest {
        coEvery { api.getXxx() } returns ApiResponse(errorCode = -1, data = null, errorMsg = "未登录")

        val result = repository.getXxx()

        assertTrue(result.isFailure)
    }
}
```

### 5. PagingSource 测试模板

```kotlin
class <Name>PagingSourceTest {

    private val api: WanApiService = mockk()
    private val pagingSource = <Name>PagingSource(api)

    @Test
    fun `load returns page on success`() = runTest {
        val mockArticles = listOf(Article(id = 1, title = "test"))
        coEvery { api.getXxxList(0) } returns ApiResponse(
            errorCode = 0,
            data = ArticleListData(datas = mockArticles, over = false),
            errorMsg = "",
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(mockArticles, page.data)
        assertNull(page.prevKey)
        assertEquals(1, page.nextKey)
    }

    @Test
    fun `load returns error on exception`() = runTest {
        coEvery { api.getXxxList(any()) } throws Exception("网络错误")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }
}
```

## 测试工具库

| 库 | 用途 |
|---|---|
| `kotlinx-coroutines-test` | `runTest`、`StandardTestDispatcher`、`advanceUntilIdle` |
| `turbine` | `Flow.test {}` 流测试 |
| `mockk` | Mock 依赖（`mockk()`、`coEvery`、`verify`） |
| `junit4` | `@Test`、`assertEquals`、`assertTrue` |

## 检查清单

- [ ] 测试文件放在 `src/test/`（单元测试）而非 `src/androidTest/`
- [ ] ViewModel 测试使用 `Dispatchers.setMain(testDispatcher)` 并在 `@After` 重置
- [ ] 使用 `testDispatcher.scheduler.advanceUntilIdle()` 推进协程
- [ ] Repository 测试同时覆盖 `errorCode = 0` 和 `errorCode != 0` 场景
- [ ] PagingSource 测试覆盖首页加载（`key = null`）和异常场景
- [ ] 分页起始页码断言为 `0`
