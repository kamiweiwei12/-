# 人工决策日志

## 已批准

### D-001：price_rule.stay_mode关系约束

- 状态：`APPROVED_IN_RC`，2026-11-25（Asia/Taipei）。
- 最终决定：方案B。
- 批准证据：仓库所有者/项目负责人 `kamiweiwei12` 在本项目Codex任务中回复“批准”，承接上一消息明确列出的D-001=B、D-002=A。
- 原因：现有历史只确认单规则胜出，没有证据决定是否允许“全营地+住宿模式”的宽范围价格规则。
- 方案A（较严格）：`stay_mode`非空时必须同时有 `resource_type_id`，并以 `(resource_type_id, stay_mode)` 外键引用 `resource_type_stay_mode`。优点是数据库可强制兼容；缺点是不支持全营地DAY_PASS/OVERNIGHT规则。
- 方案B（较灵活）：允许 `resource_type_id` 为空、`stay_mode`非空；`stay_mode`使用公共枚举/CHECK，资源类型级规则在发布时检查兼容。优点是支持宽范围规则；缺点是完整性部分依赖发布服务。
- 影响：数据库约束、价格规则管理API、冲突检测和测试。
- 落地约束：允许全营地住宿模式规则；`stay_mode`受枚举/CHECK约束。资源类型兼容性由发布服务校验，报价查询再次限制支持模式；不建立会排除宽范围规则的复合外键。
- 审批边界：已进入v1.0-rc，但仍须 `Janovo2117` 在正式PR中Review；当前不得创建Flyway。

### D-002：RentalStatus.OVERDUE写入执行器

- 状态：`APPROVED_IN_RC`，2026-11-25（Asia/Taipei）。
- 最终决定：方案A。
- 批准证据：同D-001。
- 原因：历史定义了OVERDUE状态，但没有确认由定时任务还是访问时惰性校正。
- 方案A：Spring定时任务按 `Asia/Shanghai` 扫描并幂等更新。优点是状态及时；缺点是需要调度与时钟测试。
- 方案B：读取、取还或管理员对账时惰性更新。优点是实现简单；缺点是数据库状态可能晚于真实截止时刻。
- 共同不变量：逾期不自动释放库存；归还后转RETURNED并写流水。
- 影响：后台执行方式、运维语义、API展示和时间测试。
- 落地约束：后端按可配置周期幂等扫描并写入OVERDUE；逾期不释放库存，实际归还才释放。需要可控时钟、重复扫描和并发扫描测试。
- 审批边界：已进入v1.0-rc，但尚无代码或测试，仍须正式PR Review；当前不得实现执行器。

### D-003：住宿占用状态的枚举归属

- 状态：`APPROVED_IN_RC`，2026-11-26（Asia/Shanghai）。
- 最终决定：方案A，新建独立 `OccupancyStatus` 枚举（`HELD/CONFIRMED/IN_USE/RELEASED`），不复用 `OrderItemStatus`。
- 批准证据：项目负责人于 2026-11-26 10:19 +08:00 回复“全按推荐”，对应执行计划的决策1推荐项A。
- 原因：第二次审计P2-3指出 `lodging_reservation.occupancy_status` 的取值不在任何公共枚举中，`ENUMS.md` 规定枚举必须在DB/Java/OpenAPI/TypeScript完全一致，DDL无从生成CHECK。
- 方案A：独立枚举，占用只表达“是否仍占用资源时间区间”，不混入 `FULFILLED/EXPIRED` 等履约语义；待支付占用统一为 `HELD`，补 `RELEASED` 终态。
- 方案B（未采纳）：复用 `OrderItemStatus`，改动更小但混淆两套语义。
- 影响：`DATABASE_CONTRACT` 排斥谓词由 `PENDING_PAYMENT` 改为 `HELD`；后续DDL与DTO按新枚举生成。
- 审批边界：已进入v1.0-rc，仍须 `Janovo2117` 在PR #1中Review；当前不得创建Flyway。

### D-004：核销“同一令牌至多一次成功”的数据库约束

- 状态：`APPROVED_IN_RC`，2026-11-26（Asia/Shanghai）。
- 最终决定：方案A，增加部分唯一索引 `UNIQUE(token_id) WHERE result='ACCEPTED'`，并定义 `VerificationResult` 枚举（`ACCEPTED/REJECTED`）。
- 批准证据：同D-003，对应执行计划的决策2推荐项A。
- 原因：第二次审计P2-1指出核销仅有操作级幂等键 `UQ(operator_id, idempotency_key)`，不同管理员用不同键并发核销同一令牌时，最终防线仍落在应用层行锁，与“数据库负责不可绕过的完整性”冲突。
- 方案A：数据库层强制单次成功；`REJECTED` 事件不受限，可多次记录用于审计。
- 方案B（未采纳）：仅服务层行锁，无数据库约束。
- 影响：`verification_event` DDL、并发核销测试用例；测试当前`NOT RUN`。
- 审批边界：已进入v1.0-rc，仍须PR Review。

## 已有确认的引用

18项业务确认的追加式证据见 `../PHASE2_CONFIRMATIONS.md`。本文件不重复改写历史提案。
