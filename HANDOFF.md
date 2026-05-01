# 交接说明

本文列出当前阶段刻意留到后续实现或后端集成的内容。

## 空实现或占位实现

- `core/sse/SseClient.kt` 目前是结构性占位，尚未维护真实 SSE 连接。
- `data/repository/StatusRepositoryImpl.kt` 是未来基于 SSE 的状态更新占位。
- `data/repository/ChatRepositoryImpl.kt` 是未来基于 REST 的历史消息与发送接口占位。
- `data/repository/ConnectionRepositoryMock.kt` 当前仅包含静态示例平台状态。
- `data/repository/StatusRepositoryMock.kt` 若需更丰富动画验证，仍需加入定时更新循环。
- `ui/components/DramaModeOverlay.kt` 仅为蒙层骨架，真实换皮逻辑（顶栏色调、输入框装饰等）待补。

## 今日进度补丁（2026-05-01）

- Android SDK 已本地安装。
- `local.properties` 已配置（不入库）。
- Gradle 配置验证通过（含阿里云镜像兜底）。
- 当前编译失败原因：缺资源文件 `mipmap/ic_launcher` 和 `style/Theme.CedarStarAndroid`。
- `Theme.CedarStarAndroid` 应在 `res/values/themes.xml` 创建，父主题 `Theme.Material3.DayNight.NoActionBar`，深色模式版在 `values-night/themes.xml`。
- 应用图标设计稿已确定（极简卡通三角色：女孩 + 黑狗 + 白猫），原图保存为 `cedarstar_icon_source.png`，待通过 Android Studio Image Asset Studio 接入。
- 当前阻塞：Image Asset Studio 入口在 Android Studio 中不显示，正在排查（疑似 Gradle sync 未成功导致项目未识别为 Android module）。
- 完成功能模块映射表（ARCHITECTURE.md），所有 T1-T4 功能已归属到 Chat / Journal / Companion / Clio / 顶栏 / 抽屉 / Dashboard / 全局后台能力 八大模块。

## 硬编码占位值

- Token Provider 目前返回硬编码 mock token。
- API Base URL 仍是占位地址。
- `DashboardOverviewDto` 含占位文本。
- Mock 聊天历史为固定示例消息。
- 四个 Tab 页面与 Dashboard 目前均使用占位内容。

## 当前刻意未实现的能力

- 历史消息、发送消息、仪表盘总览的真实 REST 请求处理。
- 真实 SSE 连接管理、重连策略与解析循环。
- Chat、Journal、Companion、Clio 页面的完整内容 UI。
- 超出占位路由之外的最终 Dashboard UI 与路由行为。
- 真实 Token 持久化或鉴权存储。
- 任何生产级后端集成。
- 任何本地数据持久化层。

## 后续后端集成 TODO

- 后端就绪后，将 `core/di/RepositoryModule.kt` 中的 Mock 绑定替换为 Impl 绑定。
- 将 `StatusRepositoryImpl` 接入 SSE 事件消费，并以 `StateFlow<AppStatus>` 对外暴露。
- 将 `ChatRepositoryImpl` 接入 `CedarStarApi` 的发送/历史接口。
- 真实平台连接数据可用后，扩展 `ConnectionRepositoryImpl`。
- 增加真实 SSE event-source 生命周期管理与重试策略。
- 将硬编码 Token Provider 替换为基于存储的真实 Provider。

## 后续需要补做的验证

- 本机安装 Android SDK 后，执行一次真实 Android 构建。
- 在真机/模拟器上验证顶部栏动画时序。
- 使用 mock 或真实 SSE 事件验证抽屉连接指示器刷新。
- 验证 Dashboard 弹层路由与返回行为。
- 在设备上验证 Tab 切换行为。

## 待研究功能（产品决策未定）

以下功能的 UI 模块归属取决于产品决策或技术调研结果，当前不实现：

### 让小克自主玩社交平台（X / Twitter 等）

- 实现路径未定：浏览器自动化 / 系统无障碍服务 / 平台官方 API，三种方案 UI 入口完全不同
  - 浏览器自动化 → 独立页面或新 Tab
  - 系统无障碍 → 后台运行 + 开关页（归 Clio）
  - 平台 API → 内容流接入 Companion Tab
- 需先调研技术可行性再决定 UI 模块归属

### AI 生成图相册

- 存储已确定：复用 Cloudflare R2（杉杉数据库备份用 bucket）
- 待决定：是否新开独立 bucket、CDN 域名、元数据 schema、存储范围（仅小克生成图 / 加 Chat 互发图 / 加共玩截图）
- UI 暂定：Journal Tab 下的"小克的相册"子页（瀑布流或时间轴）
- 实现暂缓，等产品细节确定
