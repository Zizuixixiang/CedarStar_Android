# CedarStar Android

CedarStar Android 是一个基于 Jetpack Compose + Material 3 的 Android 应用骨架。当前仓库重点在架构、导航、共享状态、SSE 管线与基于 Mock 的 UI 脚手架，业务逻辑暂未落地。

## 顶层目录职责

- `core/`：应用级基础设施，如网络配置、SSE 客户端骨架和 Hilt 模块。
- `data/`：DTO、领域模型、仓库接口，以及 Mock / Impl 仓库层。
- `ui/`：Compose 页面、共享 UI 状态、导航、主题和可复用组件。
- `util/`：日志、格式化等小型工具函数。
- `app/src/main/AndroidManifest.xml`：应用入口、权限和组件声明。
- `build.gradle.kts` / `settings.gradle.kts`：Gradle 构建与依赖管理。

## 依赖列表

项目刻意限制在以下库范围内。

### Android Core / Lifecycle
- `androidx.core:core-ktx`
- `androidx.lifecycle:lifecycle-runtime-ktx`
- `androidx.lifecycle:lifecycle-viewmodel-compose`
- `androidx.activity:activity-compose`

### Compose
- `androidx.compose:compose-bom`
- `androidx.compose.ui:ui`
- `androidx.compose.ui:ui-graphics`
- `androidx.compose.ui:ui-tooling-preview`
- `androidx.compose.material3:material3`
- `androidx.compose.material:material-icons-extended`
- `androidx.compose.ui:ui-tooling`（仅 debug）

### Navigation
- `androidx.navigation:navigation-compose`

### Networking / Serialization
- `com.squareup.retrofit2:retrofit`
- `com.squareup.retrofit2:converter-kotlinx-serialization`
- `com.squareup.okhttp3:okhttp`
- `com.squareup.okhttp3:logging-interceptor`
- `org.jetbrains.kotlinx:kotlinx-serialization-json`

### SSE
- `com.launchdarkly:okhttp-eventsource`

### Coroutines
- `org.jetbrains.kotlinx:kotlinx-coroutines-android`

### DI
- `com.google.dagger:hilt-android`
- `com.google.dagger:hilt-compiler`（KSP）
- `androidx.hilt:hilt-navigation-compose`

## 如何运行

1. 在本机安装 Android SDK。
2. 在项目根目录创建 `local.properties`，指向本机 SDK 路径，例如：

```properties
sdk.dir=C:\\Android\\Sdk
```

3. 用 Android Studio 打开项目，或在项目根目录直接执行 Gradle。
4. 构建 debug 包：
5. 首次运行前，需在 `app/src/main/res/values/themes.xml` 定义 `Theme.CedarStarAndroid`，否则编译会因 `AndroidManifest.xml` 引用该主题而失败。
6. 应用图标需通过 Android Studio Image Asset Studio 接入（File 视图右键 `res` -> New -> Image Asset）。

```bash
./gradlew assembleDebug
```

Windows PowerShell 下：

```powershell
.\gradlew.bat assembleDebug
```

## Mock 模式

当前应用默认以 Mock 模式启动。

- `ChatRepositoryMock` 返回固定聊天历史。
- `StatusRepositoryMock` 负责驱动顶部状态变化（待进一步完善）。
- `ConnectionRepositoryMock` 提供平台连接状态指示。
- `RepositoryModule` 在 Hilt 中默认绑定 Mock 实现。

这意味着即使没有真实后端，应用也能渲染外壳、导航和占位状态。

Mock Repository 默认会定时扰动数据（计划中），用于验证顶栏动画和抽屉局部刷新；实际定时逻辑待补（见 `HANDOFF.md` TODO）。

## 已知 TODO

- `StatusRepositoryMock` 仍需加入定时更新循环，以便更丰富地验证顶部栏动画。
- `ConnectionRepositoryMock` 仍需加入定时随机切换，用于连接指示器动画验证。
- `SseClient` 目前仍是结构性占位，未维持真实 SSE 流。
- `StatusRepositoryImpl` / `ChatRepositoryImpl` 目前是未来 REST + SSE 集成的占位。
- `DashboardScreen`、`ChatScreen`、`JournalScreen`、`CompanionScreen`、`ClioScreen` 仍为占位 UI。
- `MainScreen` 的导航/抽屉/仪表盘状态目前是脚手架，尚未完全接入最终 UX 行为。
- `local.properties` 按设计不入库，需要本地自行创建。
