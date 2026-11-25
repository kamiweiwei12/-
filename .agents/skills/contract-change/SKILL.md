---
name: contract-change
description: Review and change Snowfield public database, API, enum, or business-flow contracts while preserving history, version status, approval boundaries, and cross-document consistency.
---

# Contract Change

## 适用范围

修改 `docs/DATABASE_CONTRACT.md`、`docs/API_CONTRACT.md`、`docs/ENUMS.md`、`docs/design/BUSINESS_FLOWS.md`、冻结记录或其消费者前必须使用。只做普通错别字且不改变语义时，可记录“不适用”及理由。

## 执行步骤

1. 读取 `AGENTS.md`、四份公共契约/流程、`CONTRACT_FREEZE.md`、`PHASE2_CONFIRMATIONS.md`、任务板及相关决策日志。
2. 执行 `git status --short --branch`，确认授权分支和工作区；发现分歧或他人未明改动时停止。
3. 用 `git show`/`git log` 找到被修改规则的历史来源，区分原始提案、用户确认、候选设计和已批准版本。
4. 变更前写明原因、范围、兼容性、版本、审批状态、数据库/API/ENUM/流程/测试影响。
5. 同步检查四份事实源；不得只修改DTO而忽略数据库关系，也不得创建第二套用户、订单、库存、价格或支付体系。
6. 有两个合理方案且没有批准证据时，登记决策ID、选项和影响，保持 `PENDING/UNKNOWN`，暂停对应实现。
7. 候选版本使用 `CANDIDATE/rc`；只有真实另一成员PR批准后才能标为正式冻结。
8. 更新 `CHANGELOG.md`、`docs/audit/OPERATION_LOG.md`、`TEST_EVIDENCE.md`、`SKILL_COMPLIANCE.md`、`ISSUES.md`，并记录实际命令和退出码。
9. 检查完整diff、版本状态、内部链接和 `git diff --check`。

## 禁止事项

- 不覆盖或删除历史提案；确认结果采用追加式记录。
- 不把审计建议当作已批准业务决定。
- 不将 `UNKNOWN`、未运行测试或候选状态写成PASS/正式冻结。
- 不在没有授权时创建代码、迁移、依赖或外部系统。

## 验收标准

- 变更记录包含原因、兼容性、版本、审批状态、影响和验证方式。
- 数据库、API、ENUM与业务流程不存在已知未登记矛盾。
- 待决项具有稳定ID且能在决策/问题日志中追踪。
- diff检查和实际证据已记录；正式状态有可核验的PR批准依据。
