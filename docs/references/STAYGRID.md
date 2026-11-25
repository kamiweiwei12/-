# StayGrid 研究

仓库：https://github.com/smitroy4/StayGrid  
核查提交：`5ce03543ba6b15e1bd30e8c3dcddf5e4c687c0a3`（2026-09-24）  
许可证：MIT。

## 实际核查范围

读取 README、LICENSE、Maven配置、实体、InventoryRepository、BookingService、定价策略、配置和测试目录。README中的功能与源码进行了交叉检查。

## 源码确认内容

- Java 21/Spring Boot/PostgreSQL，使用 Spring Data JPA/Hibernate，不是 MyBatis/Flyway。
- `Inventory` 为每房型/日期的库存行，包含 `totalCount/bookedCount/reservedCount/surgeFactor/price`。
- `InventoryRepository` 使用 `PESSIMISTIC_WRITE` 锁定日期范围库存；BookingService 有事务边界。
- 定价用 Decorator 链：Base → Surge → Occupancy → Urgency → Holiday。
- README描述待支付过期、支付Webhook、管理报表和统一响应。
- 测试目录只有一个 Spring context 测试，未发现并发或定价断言测试。
- 未发现 Flyway/Liquibase 迁移，配置建议 `spring.jpa.hibernate.ddl-auto=update`。

## 重要源码问题

- `HolidayPricingStrategy` 中 `isTodayHoliday = true` 为硬编码，不能当作已实现节假日定价。
- `OccupancyPricingStrategy` 用 `bookedCount / reservedCount`，分母为0且业务公式不应如此；正确口径应基于可售总量。
- README所称“production-grade”和并发安全没有配套并发测试证据。

## 可借鉴

- 锁定每日库存行的容量模型和事务边界。
- 策略链体现价格来源顺序，报价可展示组成。
- `HotelMinPrice` 类似汇总投影的搜索优化思想，但需要真实性能证据。

## 不采用/改造

- 不使用JPA自动建表、Stripe、Gemini或定时重算未来一年全量价格。
- 课程版采用配置化规则、确定优先级和订单价格快照；初期不做占用率动态定价。
- 住宿具体资源优先采用 PostgreSQL range 排斥，而票务/批量容量采用行锁。

