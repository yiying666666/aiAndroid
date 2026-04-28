# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (minification enabled)
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumentation tests
./gradlew :feature:home:test     # Run tests for a single module
```

## Architecture

Multi-module MVVM + Repository pattern with Hilt DI and Jetpack Compose.

### Module Graph

```
app
├── feature:auth / home / project / wechat / navi / mine / search / article
├── core:ui          (Compose components, Material3 theme)
├── core:data        (repositories, paging, DataStore)
│   └── core:network (Retrofit, OkHttp, cookie jar)
└── core:model       (serializable data classes — no dependencies)
```

`core:model` has zero dependencies on other core modules. Feature modules depend on `core:ui` + `core:data` + `core:model`; `feature:article` only depends on `core:ui` (intentionally lightweight).

### Data Flow

```
Screen (collectAsStateWithLifecycle)
  → ViewModel (MutableStateFlow<*UiState>, viewModelScope)
  → Repository (suspend fun returning Result<T>)
  → WanApiService (Retrofit, ApiResponse<T>.toResult())
```

API responses are wrapped in `ApiResponse<T>`. The `.toResult()` extension converts them to `Result<T>`, throwing `ApiException` when `errorCode != 0`. Repositories never throw — callers use `.onSuccess / .onFailure`.

For paginated screens (home, project, wechat, search), the repository returns `Flow<PagingData<T>>` from a `Pager` with a custom `*PagingSource`. ViewModels call `.cachedIn(viewModelScope)` and screens collect via `collectAsLazyPagingItems()`.

### State Management

Each ViewModel owns a `MutableStateFlow<*UiState>` (data class). All mutations use `_uiState.update { it.copy(...) }`. The read-only `.asStateFlow()` is exposed to the UI. Never mutate UI state directly.

### Navigation

`AppNavigation.kt` defines two levels:
- **Root NavHost**: `AppRoute` sealed class — `Login`, `Register`, `Main`, `Search`, `ArticleDetail`
- **Nested NavHost** inside `MainScaffold`: `BottomNavItem` sealed class — 5 bottom tabs

Start destination is determined at launch from `UserPreferencesDataStore.isLoggedIn`. Article deep links use `Uri.encode(url)` to handle special characters in route parameters.

### Dependency Injection

- `@HiltAndroidApp` on `WanApplication`, `@AndroidEntryPoint` on `MainActivity`
- `@HiltViewModel` + `@Inject constructor` on all ViewModels
- `DataModule` (`core/data/di/`) binds repository interfaces to implementations
- `NetworkModule` (`core/network/di/`) provides `OkHttpClient`, `Retrofit`, `WanApiService`
- All network and data components are `@Singleton`

### Network

- Base URL: `https://wanandroid.com`
- Serialization: `kotlinx.serialization` with `ignoreUnknownKeys = true`, `coerceInputValues = true`
- Cookies persisted via `PersistentCookieJar` → DataStore; cleared on logout
- Logging: `HttpLoggingInterceptor` with `Level.BODY` in debug, `Level.NONE` in release; Logcat tag `WanNetwork`
- Timeouts: 15 s for connect / read / write

### User Preferences

`UserPreferencesDataStore` (DataStore Preferences) stores login state, user ID, username, nickname, and avatar. Injected into `MainActivity` (for auth routing) and repositories that need login context.

## Coding Conventions

### Naming

| Artifact | Pattern | Example |
|---|---|---|
| Screen composable | `*Screen.kt` | `HomeScreen.kt` |
| ViewModel | `*ViewModel.kt` | `HomeViewModel.kt` |
| UI state | `*UiState` data class | `HomeUiState` |
| Repository interface | `*Repository` | `AuthRepository` |
| Repository impl | `*RepositoryImpl` | `AuthRepositoryImpl` |
| Paging source | `*PagingSource` | `ArticlePagingSource` |
| Hilt module | `*Module` | `DataModule` |

### Feature Module Structure

Every feature module follows exactly:
```
feature/<name>/src/main/java/com/wanandroid/feature/<name>/
├── <Name>Screen.kt
└── <Name>ViewModel.kt
```

### Compose Patterns

- Public screens accept navigation callbacks as lambda parameters (no NavController passed in)
- All composables accept `modifier: Modifier = Modifier`
- Private sub-composables are `@Composable private fun`
- Screens are stateless — all state flows from ViewModel via `collectAsStateWithLifecycle()`

### Key Libraries

| Library | Version | Notes |
|---|---|---|
| Kotlin | 2.0.21 | `kotlin.code.style=official` |
| Compose BOM | 2024.09.00 | Material3 throughout |
| Hilt | 2.56.1 | KSP-based |
| Retrofit | 2.11.0 | + kotlinx-serialization converter |
| OkHttp | 4.12.0 | |
| Paging | 3.3.6 | page size 20, no placeholders |
| Coil | 2.7.0 | `coil.compose` for images |
| DataStore | 1.1.4 | Preferences (no Proto) |
| Coroutines | 1.9.0 | `flatMapLatest` for search/filter flows |
