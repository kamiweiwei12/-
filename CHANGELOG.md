# Changelog

本项目使用简化的 Keep a Changelog 结构；日期采用 Asia/Taipei。

## [Unreleased]

### Added

- 初始化空仓库的第一阶段设计与协作基线。
- 建立需求、架构、数据库/API/枚举契约初稿。
- 建立 ER、3NF、业务流程和测试计划。
- 完成五个指定 GitHub 项目的源码级参考研究与许可证记录。
- 建立第2阶段18项决策清单和未分配团队任务板。
- 接入GitHub远端并发布 `main`、`develop` 基线分支。
- 补充队友克隆、feature分支、PR和双Codex协作规范。
- 追加第2阶段确认记录，恢复原始提案的Git可追溯性。
- 建立操作、测试证据、Skill合规、决策和问题审计体系。
- 建立契约变更、Git PR和测试证据三个项目级Skill。
- 登记PR核查发现：PR #1目标分支错误（R5-01）、审计记录与PR创建时间不一致（R5-02）、三分支无保护（R5-03）、价格规则全空组合语义缺失（R5-04）、占用状态枚举缺失（R5-05）。
- 新增 `OccupancyStatus`（`HELD/CONFIRMED/IN_USE/RELEASED`）与 `VerificationResult`（`ACCEPTED/REJECTED`）两个公共枚举。
- 新增 `.gitattributes`（`* text=auto eol=lf`），消除跨平台行尾警告；未对历史文件执行 renormalize。
- 新增任务卡 GOV-06「双人协作链路打通」，并把 GOV-02 改名为「GitHub远端接入与main/develop发布」。

### Changed

- 将未经双人Review的公共契约纠正为v1.0-rc候选状态。
- 订单调整为支持多间住宿、套餐与单选项混合购物车，并统一一次整单支付。
- 移除quoteToken，创建订单由服务端重算并使用 `PRICE_CHANGED` 处理报价变化。
- 价格规则统一为“基础价 + 单一最高优先级胜出规则”。
- 补齐Selection DTO、支付/订单幂等语义、价格冲突作用域、核销接口和套餐编码边界。
- 批准D-001方案B：允许全营地住宿模式价格规则，资源类型兼容性由发布与报价服务校验。
- 批准D-002方案A：由后端定时任务幂等标记逾期租赁，逾期不自动释放库存。
- 将协作准备PR #1的目标分支由 `main` 纠正为 `develop`，符合AGENTS.md §6的集成路径。
- 更正冻结记录中关于成员B协作权限与分支保护的陈述：`Janovo2117` 已具备write权限，但双人PR Review仍未发生。
- 修复 `design/DATABASE_DESIGN.md` 与数据库契约的不一致：`payment.idempotency_key` 候选码改为 `(order_id, idempotency_key)`，补 `customer_order` 创建幂等键与两个核销关系的ER/码摘要。
- 住宿排斥谓词改用 `HELD/CONFIRMED/IN_USE`，`occupancy_status` 明确使用 `OccupancyStatus`，`RELEASED` 不参与排斥（D-003）。
- 核销新增部分唯一索引 `UNIQUE(token_id) WHERE result='ACCEPTED'`，把“同一令牌至多一次成功”下沉到数据库（D-004）。
- 修正 `API_CONTRACT` 中住宿 `quantity` 的歧义表述：`LodgingSelection` 不提供 `quantity`，房间数量由数组长度表达。
- `contract-change` Skill 的第5步由“四份事实源”改为8项显式消费者清单，防止ER文档漏同步。

### Known limitations

- 尚未创建 Spring Boot、React 工程和正式 Flyway 业务迁移。
- 尚无可运行系统，因此没有功能、并发或性能测试结果。
