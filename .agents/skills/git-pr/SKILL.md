---
name: git-pr
description: Apply the Snowfield repository branch, commit, pull-request, review, and merge controls for any Git or PR operation.
---

# Git PR

## 适用范围

执行分支创建、暂存、提交、推送、PR准备、Review或合并前必须使用。

## 执行步骤

1. 读取 `AGENTS.md` 和 `docs/TEAM_WORKFLOW.md`，确认任务授权分支及目标分支。
2. 记录 `git branch --show-current`、`git rev-parse HEAD`、`git status --short --branch`、`git remote -v`；需要同步时先比较ahead/behind。
3. 工作前说明目标、修改范围、契约影响和预计检查；发现远端分歧、未知改动或错误分支时停止。
4. 只暂存明确文件；提交前检查完整 `git diff`、`git diff --check`、测试证据和敏感信息。
5. 提交信息使用 `type(scope): summary`，每个提交保持可审查。
6. PR必须写明目标、修改范围、契约/迁移影响、实际测试证据、风险和回滚方式；日常PR目标为 `develop`。
7. 公共契约需要另一成员明确批准；记录PR链接、reviewer、批准时间和合并SHA后才可升级正式状态。
8. 完成后记录分支、HEAD、工作区、提交/推送结果和回滚方式。

## 禁止事项

- 不直接向 `main` 推送开发内容，不绕过 `develop` 集成流程。
- 未经人工批准不合并PR、不修改分支保护、不接受队友邀请、不冒用owner或队友身份。
- 不使用 `reset --hard`、force push、rebase改写共享历史，不修改全局Git配置。
- 不在远端分歧或工作区来源不明时强行同步或覆盖。

## 验收标准

- 操作发生在授权分支，提交范围与任务一致。
- PR说明包含六类必需信息并有真实测试/未测试状态。
- 公共变更的批准者不是提交者本人，且批准证据可复核。
- 日志中的SHA、退出码、推送和工作区状态与Git实际输出一致。
