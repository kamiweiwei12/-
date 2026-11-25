# Louez 研究

仓库：https://github.com/Synapsr/Louez
核查提交：`f7b78f9c69c9c149873e495f5efaee4b018e945c`（2026-11-21）
许可证：AGPL-3.0。NOTICE/README 说明 Nucleo 图标不在仓库许可证内，另有 `rembg`/模型许可。

## 实际核查范围

读取 README、LICENSE/NOTICE、monorepo目录、MySQL/Drizzle schema、迁移、可用性查询、库存/检查相关测试和管理页面目录。项目规模远大于课程设计。

## 源码确认内容

- Next.js/TypeScript monorepo，Drizzle ORM + MySQL，不是 PostgreSQL/Spring Boot。
- 产品既支持批量数量，也支持具体 `productUnits`；`reservationItemUnits` 连接预订明细和单件资产。
- `unit-availability.ts` 以 `start < otherEnd AND end > otherStart` 判断半开区间重叠，并区分 pending/confirmed/ongoing 阻塞状态。
- `productUnitDowntimes` 将维护/维修停机区间排除出可租资产；资产有生命周期事件。
- 可用性服务同时计算批量库存、组合库存、具体资产和周转缓冲。
- 有 departure/return 检查模板与检查记录，快照模板以保留历史。
- 存在大量数据库/组件测试、迁移和日历/库存管理页面。

## 可借鉴

- “型号/产品 + 单件资产 + 停机区间 + 分配表”的装备模型。
- 批量库存和资产追踪是两种模式，不强行混成一个数字。
- 取件/归还检查和模板快照思路。
- 半开区间、阻塞状态集合和周转缓冲应统一定义。

## 不采用/改造

- AGPL网络传播条款和第三方资产限制使直接复制风险高；本项目不复制其代码、样式或图标。
- MySQL/Drizzle、Next.js全栈和多租户SaaS结构与冻结技术栈不符。
- 项目包含支付、AI、短信、多租户、市场等大量超范围模块，全部舍弃。
- 我们用 PostgreSQL 排斥约束/行锁做不可绕过防线，而不是只复刻应用查询。

## 本项目结论

采纳概念模型：`equipment_model`、`equipment_asset`、维修区间、租赁资产分配、取还/维修流水；在自己的 Spring/MyBatis/PostgreSQL 架构中从零实现。
