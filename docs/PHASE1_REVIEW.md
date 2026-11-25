# 第一阶段评审包

日期：2026-11-25。结论：设计基线已建立，业务实现应等待契约确认。

## 1. 仓库状态

- 启动时为空目录、非Git仓库，无代码、规范、依赖、数据库或提交历史。
- 已初始化 `main`，建立文档基线；尚未建立远端，无法核对GitHub URL、保护规则或推送权限。
- 未删除或覆盖用户文件；参考项目仅克隆至临时目录。

## 2. 第一阶段交付索引

| 要求 | 成果 |
|---|---|
| 参考项目研究 | `docs/references/*.md`、`REFERENCE_COMPARISON.md` |
| 项目需求 | `docs/REQUIREMENTS.md` |
| 功能模块图/架构图 | `docs/ARCHITECTURE.md` |
| 业务流程图 | `docs/design/BUSINESS_FLOWS.md` |
| ER图/关系模式/3NF | `docs/design/DATABASE_DESIGN.md` |
| 候选表 | `docs/DATABASE_CONTRACT.md` |
| API/DTO/错误/认证初稿 | `docs/API_CONTRACT.md` |
| ENUM初稿 | `docs/ENUMS.md` |
| 两人分工/Git策略 | `docs/TEAM_WORKFLOW.md`、`AGENTS.md` |
| 开发计划/风险/下一步 | `docs/DEVELOPMENT.md`、`PROJECT_SCOPE.md` |
| 测试方案 | `docs/testing/TEST_PLAN.md` |

## 3. 一致性审查

- 没有重复用户体系：`app_user + role + user_role`。
- 没有多个订单体系：`customer_order + order_item` 是唯一商业订单，各模块只有带外键的占用扩展。
- 没有独立且不可解释的“可用库存”真值：住宿/资产用时间占用，容量用锁定计数，装备有统一库存流水。
- API camelCase 与数据库 snake_case 的映射规则明确；金额、时间、ID和错误格式统一。
- OrderStatus 只有一份公共枚举；退款属于 PaymentStatus，不创建冲突订单状态。
- 事务覆盖订单、占用、容量/库存和历史；取消幂等，避免重复释放。
- 时间统一半开区间；价格采用成交快照；规则冲突需按确认后的单规则/叠加政策处理。
- 设计没有引入Redis、队列、微服务、真实支付、真实天气或AI。

## 4. 需要双方确认的18项

1. 接受 P0-A（住宿闭环）先于 P0-B（雪场/装备）的门禁。
2. 一期每张订单最多一个住宿资源。
3. 一张订单可同时包含住宿、票务、缆车和装备附加项。
4. 滑雪票和缆车时段是两种独立占用，票不自动占用每趟缆车。
5. 滑雪课程作为 P0-B，不阻塞住宿闭环。
6. 营地业务时区固定 `Asia/Shanghai`。
7. OVERNIGHT 用实际入住/退房时刻形成 `[start,end)`。
8. DAY_PASS 默认09:00–18:00，允许资源开放规则覆盖。
9. 待支付资源占用15分钟。
10. 报价令牌5分钟，创建订单仍重算。
11. 价格 v1 采用“最高优先级单规则胜出”，暂不叠加多个倍率。
12. 货币固定CNY，金额两位小数、API使用字符串。
13. 装备型号固定为 BULK 或 ASSET 其中一种追踪模式。
14. QR图形列为P1；P0用可核验文本码/哈希令牌完成真实核销。
15. 只设 CUSTOMER/ADMIN 两类系统角色。
16. JWT access token 30分钟；是否需要 refresh token（建议课程版可不做）。
17. `main/develop/feature/*` 分支和至少一人PR审查规则。
18. 仓库远端地址、两名成员姓名/学号及最终分工归属。

## 5. 下一阶段执行任务

确认上述项目后：创建 `develop`；初始化 Spring Boot/MyBatis/Flyway 和 React/Vite；建立 PostgreSQL Compose；实现最小 V1 用户/角色和 V2 资源/价格/订单基础迁移；以真实PostgreSQL迁移测试作为第一道门禁。不得同时启动全部业务模块。

