# 两人团队任务板

状态说明：`DONE`、`IN_REVIEW`、`BLOCKED`、`TODO`。A已确认为朱承威，B已确认为袁顺熙；具体任务在创建对应feature分支时再从 `UNASSIGNED` 改为姓名，避免把推荐分工误写为已开工。

| ID | 任务 | 优先级 | 依赖 | 推荐角色 | 当前负责人 | 状态 | 验收标准 |
|---|---|---|---|---|---|---|---|
| GOV-01 | 第一阶段需求/架构/ER/契约草案 | P0 | 无 | 共同 | UNASSIGNED | DONE | 文档、参考研究和两次基线提交存在 |
| GOV-02 | GitHub接入main/develop | P0 | GOV-01 | 共同 | UNASSIGNED | DONE | 本地分支跟踪远端且提交SHA一致 |
| GOV-03 | 确认18项决策 | P0 | GOV-01 | 共同 | 朱承威/袁顺熙 | DONE | 用户集中确认，记录选择和理由 |
| GOV-04 | 冻结DB/API/ENUM v1 | P0 | GOV-03 | 共同 | 朱承威/袁顺熙 | IN_REVIEW | 当前仅v1.0-rc；D-001/D-002关闭且另一成员批准PR后才可正式冻结 |
| GOV-05 | R-3/R-4契约与审计整改 | P0 | GOV-03 | 共同 | UNASSIGNED | IN_REVIEW | 八项缺口有处理记录、六类审计文件和三个项目Skill，等待人工审查 |
| INF-01 | 初始化Spring Boot/Maven | P0 | GOV-04 | A | UNASSIGNED | TODO | Java21构建和基础JUnit通过 |
| INF-02 | PostgreSQL Docker Compose | P0 | GOV-04 | A | UNASSIGNED | TODO | 健康检查通过，数据卷/环境变量说明完整 |
| INF-03 | MyBatis/Flyway连接 | P0 | INF-01, INF-02 | A | UNASSIGNED | TODO | 空库迁移成功，禁用自动DDL |
| INF-04 | 统一响应/异常/健康/OpenAPI | P0 | INF-01 | A | UNASSIGNED | TODO | 接口真实启动且集成测试通过 |
| INF-05 | 初始化React/Vite/TS/Ant Design | P0 | GOV-04 | B | UNASSIGNED | TODO | lint/build通过，基础页面可访问 |
| INF-06 | 前端路由/API客户端/状态页 | P0 | INF-04, INF-05 | B | UNASSIGNED | TODO | 用户/管理路由及Loading/Empty/Error示例 |
| DB-01 | V1基础扩展/用户/角色迁移 | P0 | INF-03 | A | UNASSIGNED | TODO | 空库迁移、唯一/检查/外键反例测试通过 |
| DB-02 | 营地/资源/开放规则迁移 | P0 | DB-01 | A | UNASSIGNED | TODO | 关系和种子数据与契约一致 |
| DB-03 | 价格规则/日历迁移 | P0 | GOV-04, DB-02 | A | UNASSIGNED | TODO | 冲突规则测试、价格查询测试通过 |
| DB-04 | 统一订单/价格快照迁移 | P0 | DB-01, DB-03 | A | UNASSIGNED | TODO | 状态、金额、历史和幂等约束通过 |
| BK-01 | 住宿可用性和报价 | P0 | DB-02, DB-03 | A | UNASSIGNED | TODO | 半开区间/维修/开放/价格用例通过 |
| BK-02 | 创建、支付、取消住宿订单 | P0 | DB-04, BK-01 | A | UNASSIGNED | TODO | 单事务、幂等、释放和回滚测试通过 |
| BK-03 | 住宿并发测试 | P0 | BK-02 | 共同 | UNASSIGNED | TODO | 100并发抢1资源仅1成功且无残留 |
| UI-01 | 登录和权限路由 | P0 | INF-06, DB-01 | B | UNASSIGNED | TODO | 真实API登录、401/403处理正确 |
| UI-02 | 住宿搜索/报价/下单 | P0 | BK-02, UI-01 | B | UNASSIGNED | TODO | 主流程真实写库，四类页面状态完整 |
| UI-03 | 我的订单/管理房态价格 | P0 | BK-02, UI-01 | B | UNASSIGNED | TODO | 分页、搜索、取消、日历真实API联调 |
| SKI-01 | 滑雪票/课程场次与容量 | P1 | GOV-04, DB-04 | B | UNASSIGNED | TODO | 行锁容量不超卖，API/测试完整 |
| LIFT-01 | 缆车时段和预约容量 | P1 | SKI-01 | B | UNASSIGNED | TODO | 预约量与容量一致，并发测试通过 |
| VER-01 | 文本码核销 | P1 | SKI-01, LIFT-01 | B | UNASSIGNED | TODO | 单次核销、重复409和事件日志通过 |
| EQ-01 | 装备型号/批量库存/资产迁移 | P1 | GOV-04, DB-04 | B | UNASSIGNED | TODO | BULK/ASSET约束及维修区间通过 |
| EQ-02 | 装备租赁/取还/维修 | P1 | EQ-01 | B | UNASSIGNED | TODO | 流水、释放、维修不可租和并发测试通过 |
| RPT-01 | 统计视图与Dashboard | P1 | BK-02, SKI-01, EQ-02 | 共同 | UNASSIGNED | TODO | 统计口径明确且源于真实业务数据 |
| PERF-01 | JMeter和EXPLAIN优化证据 | P0 | P0功能稳定 | 共同 | UNASSIGNED | TODO | 原始结果、索引前后执行计划可复核 |
| DOC-01 | 课程报告/演示/重装验证 | P0 | 全部P0 | 共同 | UNASSIGNED | TODO | 报告≥3000字、视频、全新环境复现 |

## 领取规则

领取任务前更新 `develop`，在任务卡记录计划使用的Flyway版本和feature分支，并将当前负责人改为成员姓名。任务未满足验收标准不得改为DONE。
