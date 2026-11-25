# Ski Resort Database 研究

仓库：https://github.com/sdeery14/ski-resort-database  
核查提交：`145f289bbeb1d4e08c1132d6bd3edff655c5d1cd`（2024-06-28）  
许可证：MIT，LICENSE 明确存在。

## 实际核查范围

读取 README、LICENSE、三份 SQL 脚本、目录和场景测试。项目是 MSSQL 数据库作业，不含可运行Web前后端。

## 源码确认内容

- 内部模式只有 `skiers`、`ticket_types`、`tickets`、`rentals` 四表。
- `t_ticket_datetimes` 触发器按 AM/PM/多日/季票计算票的起止时刻。
- `p_sign_up`、`p_sell_ticket`、`p_sell_rental`、`p_activate_rental`、`p_deactivate_rental` 使用事务和异常回滚。
- `v_attendant_tickets`、`v_attendant_rentals` 为柜台/租赁查询；`v_lift` 只返回当前有效期内票。
- `ski-resort-test-griffins.sql` 是顺序执行的家庭购票、租赁、取还和检票场景，不是自动断言测试。

## 可借鉴

- 用具体岗位视图和存储过程表达主办公室、租赁店、闸机流程。
- 票的有效区间和核销查询应是数据库可验证语义。
- 用完整场景脚本支持演示。

## 不采用/改造

- MSSQL语法、`money` 和硬编码 UTC+5 不移植；我们用 PostgreSQL、`numeric(12,2)` 和带时区时刻。
- 原租赁没有装备型号、资产、库存、维修或时间容量，不足以防超卖。
- `v_lift` 只是查询有效票，没有单次核销、防重或缆车容量。
- 触发器按单行 `inserted` 变量编写，不适合作为批量安全模型。
- 没有并发、性能或自动化测试证据。

## 本项目结论

采用“有效期票+业务视图+事务过程”的思想，重新设计为统一订单、票务场次、核销令牌/事件和缆车时段容量；不复制SQL。

