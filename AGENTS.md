# AGENTS.md

本文件约束仓库内的人工开发者和自动化协作代理。每次开始任务前必须先阅读本文件，再读取任务涉及的 `docs/` 契约。

## 1. 技术栈（冻结）

- Frontend：React + TypeScript + Vite + Ant Design + ECharts + React Router + Axios。
- Backend：Java 21 + Spring Boot + Spring Security + JWT + MyBatis + Bean Validation + Springdoc OpenAPI。
- Database：PostgreSQL；Flyway 是唯一数据库结构迁移入口。
- Test：JUnit、Spring Boot Test、Postman/Swagger UI、JMeter。
- 禁止未经双方确认引入 ORM 自动建表、Redis、消息队列、微服务、真实支付、真实天气 API 或 AI 功能。

## 2. 架构边界

- 浏览器只访问 `/api/v1/**`，不得直接操作数据库。
- Controller 只负责协议、校验和权限入口；事务边界位于 Service；SQL 位于 MyBatis Mapper 或受版本控制的数据库对象中。
- 数据库负责不可绕过的完整性：主外键、唯一/检查约束、住宿时间排斥、关键库存不变量。
- 一个用户体系、一个认证体系、一个订单主表、一个库存流水体系。业务扩展使用订单明细/资源占用表，不新建平行订单系统。

## 2.1 双人及多Codex唯一事实源

- 数据库表、约束和关系以 `docs/DATABASE_CONTRACT.md` 为准。
- API、DTO、响应和错误码以 `docs/API_CONTRACT.md` 为准。
- 枚举和状态转换以 `docs/ENUMS.md` 为准。
- 业务流程以 `docs/design/BUSINESS_FLOWS.md` 及冻结记录为准。
- 任何成员或Codex不得重新设计第二套用户、认证、订单、库存、支付或价格体系。
- Mock 数据必须严格符合API契约；后端完成后必须改为真实接口联调。
- 核心共享表或API变更必须先提交设计变更，经另一成员确认后再实现。

## 3. 代码规范

- Java 包名暂定 `edu.sysu.snowfield`；类名 PascalCase，方法/变量 camelCase。
- TypeScript 开启 strict；组件 PascalCase；公共接口类型不得在页面中重复手写。
- 数据库对象采用 `snake_case`；主键统一 `bigint` identity；外键名 `<entity>_id`。
- 时间：API 使用 ISO-8601；业务日期用 `YYYY-MM-DD`；时刻用带偏移量字符串；数据库时刻用 `timestamptz`。
- 金额：API 使用字符串十进制（如 `"1280.00"`）；数据库使用 `numeric(12,2)`；统一币种 `CNY`。
- 不在日志中输出密码、JWT、支付令牌或完整身份证件信息。

## 4. 数据库与 Flyway

- 迁移位置：`backend/src/main/resources/db/migration/`。
- 命名：`V<版本>__<英文说明>.sql`，例如 `V1__baseline.sql`。
- 不得依赖 pgAdmin 手工改表，不得启用 Hibernate/JPA DDL 自动更新。
- 已进入 `develop` 的迁移不可编辑；冲突时重编号并重新在空库验证。
- DDL 必须明确 PK、FK、NOT NULL、UNIQUE、CHECK 与删除策略；外键查询列按工作负载决定索引。
- PostgreSQL 专属设计（range、GiST、partial index）必须在数据库契约中说明。
- 迁移必须在干净数据库从 V1 全量成功执行；回滚依赖 Git/数据库重建，不编造不可验证的 down migration。

## 5. API 与 DTO

- 所有业务接口以 `/api/v1` 开头。
- 使用 [API_CONTRACT.md](docs/API_CONTRACT.md) 定义的响应和错误结构。
- 请求 DTO 使用 Bean Validation；Entity/数据库行不得直接作为响应。
- 分页统一从 `page=0&size=20` 开始，响应含 `page/size/totalElements/totalPages`。
- 幂等动作（模拟支付、取消、核销）必须有业务唯一约束或 `Idempotency-Key`。
- 前端不得猜字段；契约变更需同步 DTO、OpenAPI、前端类型、测试和文档。

## 6. Git 规范

- 长期分支：`main`（可提交版本）、`develop`（集成）。
- 功能分支：`feature/database-core`、`feature/booking-pricing`、`feature/frontend`、`feature/ski-lift`、`feature/equipment`。
- 提交格式：`type(scope): summary`，如 `docs(contract): define booking states`。
- 每次提交只包含一个可审查单元；提交前检查 `git diff --check` 和测试。
- 禁止 `git reset --hard`、强推共享分支、直接修改他人未合并工作。
- 每个开发任务先 `git fetch origin`，切换 `develop` 并执行 `git pull --ff-only`，再创建自己的 `feature/*` 分支。
- 禁止从过期feature分支继续派生新模块；禁止两个成员在同一feature分支并行提交未协调的公共文件。
- `main` 只接受从已验证 `develop` 发起的版本PR；日常feature PR目标为 `develop`。

## 7. 两人职责

- A：数据库、公共契约、认证、资源、价格、订单核心、并发、数据库高级特性。
- B：前端、滑雪票/课程/缆车、装备资产/租赁/维修及对应后端测试。
- 公共契约、ER图、订单关系、联调、系统测试、报告由双方共同确认。
- B 的模块必须复用公共用户、订单和库存流水，不得另建同义体系。

## 8. 测试门禁

- 后端：单元测试 + PostgreSQL 集成测试；关键并发测试不可用 H2 替代。
- 数据库：空库迁移、约束反例、事务回滚、并发超卖和 `EXPLAIN (ANALYZE, BUFFERS)`。
- 前端：至少验证 loading/empty/error/success、表单校验、权限路由和核心流程。
- 功能完成必须同时具备代码、测试通过记录、文档/CHANGELOG 更新。
- 失败、跳过或未运行的测试必须明确报告，不得写成“通过”。

## 9. 公共契约变更流程

1. 在 PR 中说明原因、兼容性、数据迁移与受影响端。
2. 由另一名成员确认。
3. 同一 PR 或有序 PR 同步数据库、枚举、API、DTO、前端类型、测试、文档。
4. 合并前在干净环境验证 Flyway 与核心接口。

## 10. 禁止事项

- 禁止复制或改名后提交完整开源项目代码。
- 禁止虚构参考项目能力、测试数据、性能结果或业务运行结果。
- 禁止把 Mock 或静态页面宣称为已完成数据库业务。
- 禁止在契约冻结前创建大量业务表/接口。
- 禁止真实收款；模拟支付必须清晰标识。
- 禁止把派生可用库存作为唯一事实来源；事实来自资产/总量、占用和流水。

## 11. 每个开发单元的完成清单

阅读规范 → 确认契约 → 最小实现 → 测试 → 检查 Diff → 更新文档与 CHANGELOG → 提交 → 报告风险与下一步。
