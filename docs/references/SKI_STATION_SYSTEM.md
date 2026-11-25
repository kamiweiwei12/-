# Ski Station System 研究

仓库：https://github.com/TAB-Sekcja9-Projekt/ski-station-system  
核查提交：`1336d101b0ccdce44732d38917a387a6cbf466d4`（2025-03-12）  
许可证：仓库根目录未发现 LICENSE/COPYING/NOTICE，默认不可复制源码。

## 实际核查范围

读取 README、客户端/服务端目录、全部服务端模型/控制器/DbContext、EF迁移和测试文件清单。

## README声称

README描述 React + TypeScript、ASP.NET Core/EF Core/PostgreSQL，并声称售票、缆车使用、报表、锁票和管理能力。

## 源码实际确认

- 服务端只有 `Ticket` 单一模型，字段主要是 `Id/Name/IssueDate`。
- `TicketsController` 只有列表和创建接口。
- 初始 EF migration 只创建 `Tickets` 表；SeedData 插入三张示例票。
- 仍保留模板 `WeatherForecast` 控制器。
- 未发现 lift、passage/validation、report、block 等实际业务模型或实现。
- 未发现服务端自动测试；未发现许可证文件。

## 可借鉴

只能把“客户端/服务端/PostgreSQL/迁移”的目录概念作为非常有限的参考，不能引用其声称的滑雪运营能力作为已实现证据。

## 不采用/结论

- 不复制任何源码，既因许可证缺失，也因业务实现不足。
- 不采用EF迁移；使用Flyway。
- 我们的票务、缆车容量、核销和统计必须由自己的关系模式、事务和测试证明。

