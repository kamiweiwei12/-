---
name: test-evidence
description: Record reproducible Snowfield test and validation evidence with exact commands, environment, outputs, exit codes, and honest PASS/FAIL/NOT RUN/UNVERIFIED status.
---

# Test Evidence

## 适用范围

执行或报告任何测试、构建、lint、文档校验、迁移验证、并发或性能检查时必须使用。

## 状态定义

- `PASS`：命令已实际执行、退出码为0，且输出支持验收目标。
- `FAIL`：命令已执行但退出码非0，或输出违反验收目标。
- `NOT RUN`：未执行；必须写明原因，不能推断结果。
- `UNVERIFIED`：有部分输出或外部声明，但不足以证明验收目标。

## 执行步骤

1. 在运行前记录目标、环境、工作目录和命令；不得预填PASS。
2. 实际执行命令并保存关键原始输出、开始/结束时间和退出码。管道命令需说明哪个阶段的退出码被记录。
3. 将命令与它验证的验收条件关联；退出码0但未覆盖目标时标为 `UNVERIFIED`，不是PASS。
4. 未运行的业务、数据库、并发或性能测试逐类记为 `NOT RUN` 及原因。
5. 记录失败，不以重跑覆盖；后续重跑作为新条目追加。
6. 更新 `docs/audit/TEST_EVIDENCE.md`，并在操作日志和PR中引用证据ID。

## 禁止事项

- 不伪造、概括成不存在的输出或省略关键失败。
- 不把计划命令、静态阅读、Codex自检或第三方报告称为独立审计。
- 不把 `UNKNOWN`、`NOT RUN`、`UNVERIFIED` 改写为PASS。
- 不记录口令、令牌、私钥或敏感环境变量值。

## 验收标准

- 每条证据有唯一ID、日期时区、分支/HEAD、环境、命令、输出摘要、退出码和结论。
- 结论与输出和退出码一致，并说明验证边界。
- 失败/未运行项仍可见；复测以追加条目关联原证据。
