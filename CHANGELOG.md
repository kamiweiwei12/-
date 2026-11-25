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

### Changed

- 冻结数据库、API和枚举契约为v1.0。
- 订单调整为支持多间住宿、套餐与单选项混合购物车，并统一一次整单支付。
- 移除quoteToken，创建订单由服务端重算并使用 `PRICE_CHANGED` 处理报价变化。
- 价格规则统一为“基础价 + 单一最高优先级胜出规则”。

### Known limitations

- 尚未创建 Spring Boot、React 工程和正式 Flyway 业务迁移。
- 尚无可运行系统，因此没有功能、并发或性能测试结果。
