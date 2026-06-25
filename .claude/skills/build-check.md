# build-check

运行常用构建与检查命令，快速验证代码是否可以编译、测试是否通过、Lint 是否干净。

## 触发时机

用户说"构建"、"编译"、"跑测试"、"运行 Lint"、"检查代码"、"构建 APK" 时调用此 skill。

## 常用命令速查

### 构建 APK

```bash
# Debug APK（默认 dev flavor）
./gradlew assembleDevDebug

# Release APK（prod flavor，已开启 R8 混淆）
./gradlew assembleProdRelease
```

> `core:network` 使用 `dev` / `prod` 两个 product flavor，构建任务需加 flavor 限定符。

### 运行测试

```bash
# 所有模块单元测试
./gradlew test

# 单个模块单元测试
./gradlew :<module-path>:test
# 示例：
./gradlew :feature:home:test
./gradlew :core:data:test
./gradlew :core:network:test

# 仪器测试（需连接 Android 设备或模拟器）
./gradlew connectedAndroidTest
```

### Lint 检查

```bash
# 所有模块
./gradlew lint

# 单个模块
./gradlew :<module-path>:lint
# 示例：
./gradlew :core:network:lint
./gradlew :feature:home:lint
```

### 增量构建（节省时间）

```bash
# 仅编译不打包（检查编译错误）
./gradlew :app:compileDevDebugKotlin

# 仅运行 KSP（检查 Hilt / Serialization 注解处理）
./gradlew :app:kspDevDebugKotlin
```

## 执行步骤

1. 根据用户需求选择对应命令
2. 若用户未指定 flavor，默认使用 `dev` + `Debug`
3. 执行命令并输出结果摘要
4. 若构建失败，提取关键错误信息并给出修复建议

## 常见错误处理

| 错误特征 | 可能原因 | 解决方向 |
|---|---|---|
| `Unresolved reference` | 依赖未添加或模块未 include | 检查 `settings.gradle.kts` 和对应 `build.gradle.kts` |
| `@Inject constructor` 找不到 | Hilt 绑定缺失 | 检查 `DataModule` 或 `NetworkModule` 是否注册 |
| `@Serializable class` 编译失败 | KSP 未生成代码 | 运行 `kspDevDebugKotlin` 再重新构建 |
| `PagingSource` 类型不匹配 | 起始页码或泛型错误 | 确认 `params.key ?: 0` 且泛型 `<Int, T>` 正确 |
| Lint `MissingClass` | 混淆规则缺失 | 在对应模块添加 `proguard-rules.pro` |

## 检查清单

- [ ] 构建使用正确的 flavor（`dev`/`prod`）+ buildType（`Debug`/`Release`）
- [ ] Release 构建前确认混淆配置完整
- [ ] 单元测试包含 Repository 和 PagingSource 的关键路径
- [ ] Lint 无 Error 级别问题
