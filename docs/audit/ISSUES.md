# 问题跟踪

| ID | 严重程度 | 原问题 | 修改方案 | 涉及文件 | 可能影响 | 验证方式 | 负责人 | 状态/解决证据 |
|---|---|---|---|---|---|---|---|---|
| R3-01 | HIGH | PackageSelection及其他Selection DTO字段不完整 | 定义五类Selection及判别式PackageComponentSelection；具体ASSET由服务端分配 | API、DB套餐关系、流程 | DTO校验、报价/下单、套餐数量核对 | API/DB字段交叉审查；后续DTO契约测试 | 共同 | RESOLVED_IN_RC；等待Review |
| R3-02 | HIGH | payment幂等键生成方、作用域和多次尝试不明 | 调用方生成；`(order_id,key)`唯一；请求指纹；失败可新键重试，成功后不再新增 | API、DB、支付流程 | 支付重试和唯一约束 | 文档一致性；后续PostgreSQL唯一/并发测试 | 共同 | RESOLVED_IN_RC；等待Review |
| R3-03 | HIGH | PRICE_RULE_CONFLICT“同作用域”不明确 | 定义具体报价单元；所有匹配规则在最高优先级并列即冲突，禁止隐式tie-break | DB、API错误、价格流程 | 发布校验、报价算法 | 边界用例审查；后续价格规则测试 | 共同 | RESOLVED_IN_RC；等待Review |
| R3-04 | HIGH | price_rule.stay_mode外键关系不明确 | 已批准D-001方案B：允许全营地模式规则，不建立复合外键，资源类型兼容性由发布与报价服务校验 | DB、API、决策日志、确认记录 | DDL、发布校验、价格测试 | 文档一致性；后续匹配/冲突测试 | 共同 | RESOLVED_IN_RC；待 `Janovo2117` PR Review |
| R3-05 | HIGH | `/verifications`缺请求体、幂等和返回 | 定义请求/响应；管理员+键作用域；事件请求指纹和唯一约束；同事务核销 | API、DB核销事件、流程 | 管理端核销、重放行为 | 文档交叉审查；后续事务/重复核销测试 | 共同 | RESOLVED_IN_RC；等待Review |
| R3-06 | MEDIUM | OVERDUE写入时机、主体和处理不明 | 已批准D-002方案A：后端定时扫描，逾期不释放库存 | ENUM、DB、API、装备流程、决策日志 | 状态及时性、调度和时间测试 | 文档一致性；后续可控时钟/重复/并发测试 | 共同 | RESOLVED_IN_RC；实现测试NOT RUN，待PR Review |
| R3-07 | HIGH | 订单与支付幂等键作用域未统一审查 | 区分用户+订单创建键与订单+支付尝试键；均保存请求指纹 | API、DB、支付流程 | 重放、跨用户/跨订单碰撞 | 文档一致性；后续唯一/重放测试 | 共同 | RESOLVED_IN_RC；等待Review |
| R3-08 | MEDIUM | 单营地模式下package_offer.code全局唯一边界不明 | rc保留全局唯一，仅限单营地外部稳定码；启用多营地前须契约迁移为复合唯一 | DB、冻结记录 | 多营地扩展和外部引用 | 模式审查；后续唯一约束反例测试 | 共同 | RESOLVED_IN_RC；等待Review |
| R3-09 | CRITICAL | 未经双人Review却标记正式FROZEN v1.0 | 全部相关状态纠正为CANDIDATE v1.0-rc，历史错误保留并追加纠正记录 | 冻结记录、契约、README、计划、任务板 | 是否允许工程初始化 | 全仓状态搜索；PR批准证据 | 共同 | REMEDIATED_IN_RC；正式冻结仍BLOCKED |
| R4-01 | HIGH | 缺少审计留痕和项目Skill | 建立六类审计文件、三个项目Skill和AGENTS强制规则 | `docs/audit/**`、`.agents/skills/**`、AGENTS | 后续任务可复核性 | quick_validate、链接/diff检查和人工审查 | 共同 | IN_REVIEW |
| R5-01 | HIGH | 已存在的 PR #1 目标分支为 `main`，违反 AGENTS.md §6「日常 feature PR 目标为 `develop`」 | 将 PR base 改为 `develop`（`gh pr edit`），不新增提交、不改写共享历史 | GitHub PR #1 | 合入路径错误，可能绕过 `develop` 集成 | `gh pr view 1 --json baseRefName` 返回 `develop`；证据TE-20261126-03 | 共同 | REMEDIATED_PENDING_REVIEW |
| R5-02 | MEDIUM | `OP-20261125-02` 记载“未创建、批准或合并PR”，但 GitHub 显示 PR #1 创建于 `2026-11-25T08:47:47Z`（16:47:47 +08:00），早于该条目记录的 17:05–17:08 | 保留原文不改写，以追加记录纠正 | `docs/audit/OPERATION_LOG.md`、本表 | 审计记录与外部可核验事实不一致 | 对比 GitHub `createdAt` 与条目时间；证据TE-20261126-02 | 共同 | RECORDED_CORRECTED |
| R5-03 | MEDIUM | `main`、`develop`、`feature/collaboration-prep` 三条分支均未设置分支保护 | 需项目成员在 GitHub 仓库设置中配置保护规则；本代理无此授权，不得自行修改 | GitHub 分支设置 | 可直接推送覆盖，Review 门禁形同虚设 | `gh api repos/.../branches` 的 `protected` 字段；证据TE-20261126-02 | 共同 | OPEN；需人工在 GitHub 配置 |
| R5-04 | MEDIUM | `price_rule` 未定义 `resource_type_id IS NULL AND stay_mode IS NULL` 的语义：D-001仅批准“资源类型为空且住宿模式非空”，未说明两者同时为空是否合法 | 待 Reviewer 判定：明确为“全营地全模式规则”，或加 CHECK 要求至少一项非空 | `docs/DATABASE_CONTRACT.md` §价格、`docs/audit/DECISION_LOG.md` | 规则匹配范围歧义，可能导致全营地规则被误用 | 契约审查＋后续规则匹配反例测试 | 共同 | OPEN_FOR_REVIEW；未修改契约 |
| R5-05 | LOW | `lodging_reservation.occupancy_status` 的取值 `PENDING_PAYMENT/CONFIRMED/IN_USE` 未在 `ENUMS.md` 中定义 | 待 Reviewer 判定：复用 `OrderItemStatus`，或新增独立枚举并同步DB/Java/TS | `docs/DATABASE_CONTRACT.md`、`docs/ENUMS.md` | 三层枚举一致性，后续DTO与CHECK生成依据 | 枚举交叉检索；证据TE-20261126-04 | 共同 | OPEN_FOR_REVIEW；未修改契约 |

关闭规则：`RESOLVED_IN_RC` 不是正式关闭；只有另一成员Review、适用测试及合并证据齐全后才可改为 `CLOSED`。
