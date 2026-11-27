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
- 按项目负责人架构审查意见修订架构设计 v2：模块内四层（api/application/domain/infrastructure.persistence）+ 依赖倒置，新增 ArchUnit 强制依赖规则 R1~R13（ADR-006）。
- 重组为 11 个业务模块（identity/catalog/pricing/booking/order/payment/lodging/winter-activity/rental/verification/reporting），以 `booking` 为唯一编排中心，消除 order↔资源模块循环依赖；新增数据所有权表与安全架构（ADR-007~ADR-010）。
- 建立后端 S1 工程骨架：Maven Wrapper 3.9.16、Spring Boot 4.1.3、common 统一响应/错误码/全局异常、ArchUnit 架构测试。
- 建立项目级工程治理 Skill `.agents/skills/project-governance/`（每轮启动机制、文档同步、开发日志、Git/GitHub、队友协作、测试证据、结束闭环），并在 `AGENTS.md` 强制每轮触发。
- 收编本地开发数据库环境：`docker-compose.yml`（`postgres:16-alpine`、命名卷、healthcheck）与 `.env.example` 纳入版本控制。
- 后端接入数据访问层：`mybatis-spring-boot-starter:4.1.0`（显式锁版本）、PostgreSQL 驱动 `42.7.14`、`spring-boot-starter-flyway` + `flyway-database-postgresql:12.4.0`；新增 `-Pdb` profile 隔离数据库集成测试。
- 新增 Flyway 迁移 `V1__baseline.sql`：`role`/`app_user`/`user_role` 三表及唯一、检查、外键、复合主键约束与 CUSTOMER/ADMIN 种子角色。
- 新增 `identity/infrastructure/persistence` 下的 `AppUserMapper`（全 `#{}` 参数绑定）与 `AppUserRecord`，作为 MyBatis 链路的真实验证对象。
- 新增 `scripts/test-db-init.ps1`，幂等创建与开发库隔离的测试库 `snowfield_test`。
- 新增 15 个真实 PostgreSQL 集成测试：Flyway 空库迁移、唯一/检查/外键/复合主键约束反例、失败语句不残留、参数绑定不可注入。
- 新增 `V2__campground_and_resource.sql`：`campground`/`season`/`resource_type`/`resource_unit`/`resource_type_stay_mode`/`resource_open_rule`/`resource_maintenance` 七表，含 `btree_gist` 扩展、币种/时区/枚举/容量/时间窗/区间非空等约束。
- 新增 `V3__price_rules_and_calendar.sql`：`holiday`/`price_rule`/`price_rule_weekday`/`price_rule_date` 四表，按 D-001 方案B 允许 `resource_type_id=NULL`，`stay_mode` 受枚举 CHECK；`R5-04` 双空语义未裁定，刻意不加 CHECK。
- 新增 `ResourceAndPriceConstraintIT`：21 个约束反例/正例测试，覆盖营地、季节、资源、开放规则、维修区间、价格规则与节假日唯一性。
- 接入 `springdoc-openapi 3.2.0`（父 POM 为 Boot 4.1.2，与项目 4.1.3 同代），暴露 `/v3/api-docs` 与 Swagger UI。
- 新增 `V4__order_and_payment.sql`：`package_offer`/`customer_order`/`order_package`/`order_item`/`order_status_history`/`order_item_status_history`/`payment`/`price_snapshot_line` 八表；含创建幂等键 `(user_id,key)` 与支付幂等键 `(order_id,key)` 作用域约束、`uq_payment_succeeded_per_order` 部分唯一索引（一单至多一条成功支付）、`method` 恒为 `SIMULATED` 的 CHECK。
- 新增 `OrderAndPaymentConstraintIT`：20 个订单/支付/套餐/快照约束测试。
- 新增 `WebContractIT`：以 `RANDOM_PORT` 真实启动内嵌服务器，验证统一成功信封、错误信封、404→`RESOURCE_NOT_FOUND` 映射、错误响应不泄漏堆栈/SQL/内部包名、OpenAPI 文档可访问且含健康接口。

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
- 技术栈基线由 Spring Boot 3.4.5 迁移到 4.1.3（Boot 3.4/3.5 已 EOL；mybatis-spring-boot-starter 4.1.0 官方对应 Boot 4.1，待 S3 接入验证）。
- `docs/ARCHITECTURE.md` 追加 ADR-006~ADR-012 并重构模块/依赖/安全/时间/测试章节，保留 ADR-001~005 原文。
- 本地开发数据库端口由 `5432` 改为 `5433`：本机原生 PostgreSQL 18 服务占用 5432，会导致 JDBC 连到本机实例而非容器；`.env.example` 与 `application.yml` 同步。
- `docs/ARCHITECTURE.md` 版本矩阵中 springdoc 由「3.2.x / S5 引入（暂缓）」写实为「3.2.0 / S2 已引入」：实测 3.2.1 父 POM 已为 Boot 4.2.0，与 ADR-012 冲突。

### Known limitations

- React 前端工程尚未创建；正式业务迁移只完成身份与权限三张表（`V1__baseline.sql`），营地/资源/价格/订单/库存待 S3。
- 后端 S2 已验证：MyBatis 4.1.0 + Spring Boot 4.1.3 + PostgreSQL 16 + Flyway 12.4.0 在 Java 21 下真实可用，`mvn -B -Pdb test` 33 个测试全绿（TE-20261126-19）；统一响应/异常/健康/OpenAPI 已在真实 HTTP 服务上验证。Spring Security/JWT 仍未接入（S4）。
- 集成测试需要真实 PostgreSQL：先 `docker compose up -d db` 与 `scripts/test-db-init.ps1`；默认 `mvn test` 不连数据库，不得作为数据库证据。
- 尚无可运行业务，因此没有业务闭环、并发或性能测试结果。
