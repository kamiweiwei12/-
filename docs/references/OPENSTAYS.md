# OpenStays 研究

仓库：https://github.com/murdawkmedia/openstays
核查提交：`d77156c7f72aa0798aaa6e789a5a58e417ee6b0f`（2026-11-14）
许可证：MIT。

## 实际核查范围

读取 README、LICENSE、Convex schema、booking/availability/API测试和价格共享代码。它使用 Convex + React/Vite，不使用关系数据库。

## 源码确认内容

- 属性类型覆盖 campground/cabin/glamping/yurt/geodome，具体 `units` 属于 `unitTypes`。
- `ratePlans` 包含基础夜价、季节日期、最短/最长入住、提前期、周转缓冲、定金和取消规则。
- `bookings` 保存状态历史；`unitNights` 是按具体资源+夜晚派生的占用表，用于冲突与日历。
- README和测试明确使用半开住宿夜语义；冲突 hold 返回409，超人数返回400。
- 有 hold、确认、取消、API授权、退款、外部日历、运营等大量自动测试，也有 adversarial tests 记录缺陷。

## 可借鉴

- 住宿按本地业务日期计费、入住区间半开。
- 活跃订单派生资源夜占用，取消释放，日历和冲突使用同一事实。
- 价格、定金、取消规则需要快照/可解释性。
- 对“支付、折扣、取消、双订”写对抗性测试，而不仅是快乐路径。

## 不采用/改造

- Convex 是非关系型后端；其事务/索引实现不能直接证明 PostgreSQL 方案。
- schema中季节和取消政策以嵌套数组保存；本课程为3NF需拆为关系表。
- iCal、真实支付、渠道、邮件、长租、复杂前台运营均超范围。
- PostgreSQL版使用 `tstzrange`/排斥约束而不是复制 `unitNights` 作为唯一防冲突机制；可按需生成日历视图。
