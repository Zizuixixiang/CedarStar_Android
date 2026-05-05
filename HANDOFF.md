# 交接说明

本文列出当前阶段刻意留到后续实现或后端集成的内容。

## 空实现或占位实现

- `core/sse/SseClient.kt` 目前是结构性占位，尚未维护真实 SSE 连接。
- `data/repository/StatusRepositoryImpl.kt` 是未来基于 SSE 的状态更新占位。
- `data/repository/ChatRepositoryImpl.kt` 是未来基于 REST 的历史消息与发送接口占位。
- `data/repository/ConnectionRepositoryMock.kt` 当前仅包含静态示例平台状态。
- `data/repository/StatusRepositoryMock.kt` 若需更丰富动画验证，仍需加入定时更新循环。
- `ui/components/DramaModeOverlay.kt` 仅为蒙层骨架，真实换皮逻辑（顶栏色调、输入框装饰等）待补。

## 硬编码占位值

- Token Provider 目前返回硬编码 mock token。
- API Base URL 仍是占位地址。
- `DashboardOverviewDto` 含占位文本。
- Mock 聊天历史为固定示例消息。
- 除 Journal 内零花钱账本骨架外，各 Tab 主体与 Dashboard 多为占位内容。

## 当前刻意未实现的能力

- 历史消息、发送消息、仪表盘总览的真实 REST 请求处理。
- 真实 SSE 连接管理、重连策略与解析循环。
- Chat、Journal（零花钱外其他子模块）、Companion、Clio 页面的完整内容 UI。
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

---

## 零花钱账本 API 接口清单

字段命名以 `data/model/PocketMoney.kt` 与 `data/repository/PocketMoneyRepository.kt` 为准；以下 JSON 字段名沿用 Kotlin 模型驼峰命名。

### 数据模型

```json
Transaction {
    id: string,
    amount: float,
    type: "INCOME" | "EXPENSE",
    incomeCategory: "ALLOWANCE" | "EARNING" | "REWARD" | "INTEREST" | null,
    expenseCategory: "LOVE" | "DAILY" | "FINE" | null,
    loveSubCategory: "SNACK" | "GIFT" | null,
    note: string,
    timestamp: long,           // UTC 毫秒
    balanceAfter: float,       // 由后端按 timestamp + id 顺序回放计算
    requestedByAi: boolean
}

PocketMoneyConfig {
    monthlyAllowance: float,
    annualInterestRate: float  // 0~1 小数（前端按百分比输入，存的是小数）
}

PocketMoneyState {
    balance: float,
    transactions: Transaction[],
    config: PocketMoneyConfig
}
```

### REST 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/pocket-money/state | 返回完整 `PocketMoneyState`，对应 `PocketMoneyRepository.state` 的初始化拉取 |
| GET | /api/pocket-money/transactions?startTime=&endTime= | 返回 Transaction[]，对应 `getTransactions(startTime, endTime)`；区间均为闭区间 UTC 毫秒，可省略；排序由后端约定（前端 UI 自行倒序展示） |
| POST | /api/pocket-money/transactions | 新增账单，返回含 `balanceAfter` 的 Transaction 以及最新 `balance`（或直接返回 `PocketMoneyState`） |
| DELETE | /api/pocket-money/transactions/{id} | 删除账单。删除后剩余流水的 `balanceAfter` 需要重算，返回 `{ balance, transactions }` 或完整 `PocketMoneyState` |
| PUT | /api/pocket-money/config | 更新配置，返回最新 `PocketMoneyConfig` |

POST body 字段：
{ amount, type, incomeCategory?, expenseCategory?, loveSubCategory?, note, timestamp?, requestedByAi }

- `timestamp` 可省略，缺省取服务器当前时间（对应 Repository 的 `timestampUtcMillis: Long?`）。
- `type=INCOME` 时填 `incomeCategory`，忽略 `expenseCategory`/`loveSubCategory`。
- `type=EXPENSE` 时填 `expenseCategory`；`expenseCategory=LOVE` 必须再填 `loveSubCategory`，其他类目应忽略 `loveSubCategory`。
- `requestedByAi` 默认 false；当前 Mock 始终为 false，AI 申请的入账接口待审批流程定稿后再补。

PUT body 字段：
{ monthlyAllowance, annualInterestRate }

业务规则：调整**下月生效，本月不受影响**（与 `PocketMoneySettingsScreen` 中的提示一致）。即当月已生成的零花钱入账与已计利息不重算，下月 1 日按新值开始计入与计息。

### 后端定时任务

- 每月 1 号 00:00 UTC 自动入账月度零花钱：生成一笔 `INCOME / ALLOWANCE`，amount = `monthlyAllowance`。
- 每日 12:00 UTC 自动计息入账：生成一笔 `INCOME / INTEREST`，amount = `monthlyAllowance × annualInterestRate ÷ 365`，保留 4 位小数（前端按 4 位展示）。

> 当前 `PocketMoneyRepositoryMock` 即按上述规则生成 2026-05 的初始流水。利息计算基数采用 `monthlyAllowance` 而非实时余额，是 Mock 阶段的简化；真实后端如需改为按余额计息或复利，需要同步更新本节并在产品上确认。

### Android 接入说明

- 后端就绪后在 `core/di/RepositoryModule.kt` 将 `PocketMoneyRepositoryMock` 的绑定替换为 `PocketMoneyRepositoryImpl`。
- `PocketMoneyRepositoryImpl` 通过 `CedarStarApi`（或新建 `PocketMoneyApi`）调用上述端点，并以 `StateFlow<PocketMoneyState>` 对外暴露。
- SSE `status_update` 事件里已有 `pocketMoney` 字段（见 `core/sse/SseEvent.kt`），后端余额变更后推送即可同步顶栏数字；流水列表的实时同步暂未走 SSE，由 ViewModel 在写操作完成后刷新。

## 后续需要补做的验证

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
