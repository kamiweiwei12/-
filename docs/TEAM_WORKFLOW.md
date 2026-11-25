# 两人协作与公共契约流程

## 分支模型

```text
main ─────────────── 可提交/演示版本
  └─ develop ─────── 日常集成
       ├─ feature/database-core
       ├─ feature/booking-pricing
       ├─ feature/frontend
       ├─ feature/ski-lift
       └─ feature/equipment
```

当前仓库从空目录初始化。第一份基线提交先落在 `main`，随后创建 `develop`；后续开发不得直接提交 `main`。

## 职责矩阵

| 领域 | A | B | 必须共同确认 |
|---|---|---|---|
| 数据库总体/迁移 | 主责 | 审查 | ER、公共表、迁移版本 |
| 用户/权限/订单/价格 | 主责 | 联调 | DTO、状态机 |
| 前端公共层 | 审查 | 主责 | API类型、错误处理 |
| 滑雪票/课程/缆车 | 公共表审查 | 主责 | 容量事务 |
| 装备/取还/维修 | 公共表审查 | 主责 | 库存流水与订单关系 |
| 测试/报告/演示 | 共同 | 共同 | 全部证据 |

## Flyway 版本占用

- 每个 PR 开始前在任务看板声明版本范围。
- 建议 A 使用奇数版本、B 使用偶数版本仅作为冲突预防；合并前仍按依赖重排未合并文件。
- 已合并迁移永久不可编辑。
- 公共表变更只能由 `feature/database-core` 或经双方书面确认的 PR 完成。

## PR 门禁

PR 描述必须包含：目标、契约影响、迁移、测试命令与结果、截图/日志、风险、回滚方式。至少一名另一成员审查；公共契约改动必须显式批准。

日常feature PR合并到 `develop`；只有完成里程碑并通过回归后，才从 `develop` 向 `main` 发版本PR。不得直接push共享分支，仓库权限允许后应在GitHub启用分支保护和“至少1个批准”。

## 契约变更

发现契约不足时先提交文档变更，不允许前端先猜字段或后端静默改名。兼容性变更同步更新数据库、MyBatis、DTO、OpenAPI、前端类型、测试和 CHANGELOG。

## 冲突避免

- A、B 不同时修改同一个 Flyway 文件。
- `API_CONTRACT.md`、`DATABASE_CONTRACT.md`、`ENUMS.md` 由变更发起者修改，另一人审查。
- 大型页面和后端模块按目录归属；共享组件用小 PR 提前合并。

## Definition of Done

实现与契约一致；迁移可从空库执行；自动测试通过；并发相关功能有 PostgreSQL 真实测试；文档和 CHANGELOG 更新；Diff 无秘密、生成物或无关改动；PR 已审查。

## 队友首次接入

```bash
git clone https://github.com/kamiweiwei12/-.git snowfield-resort-system
cd snowfield-resort-system
git switch develop
git pull --ff-only
git switch -c feature/<task-name>
```

仓库当前名称为 `-`，因此建议克隆时显式指定本地目录名。若所有者以后在GitHub改名，成员应执行 `git remote set-url origin <new-url>`，不得重新初始化仓库。

## 每次开发开始

```bash
git fetch origin
git switch develop
git pull --ff-only
git switch -c feature/<task-name>
```

开始编码前阅读 `AGENTS.md`、任务涉及的三份公共契约和 `TEAM_TASK_BOARD.md`。Mock和本地DTO不能偏离契约。

## 提交与PR

```bash
git status
git diff --check
git add <明确文件>
git commit -m "type(scope): summary"
git push -u origin feature/<task-name>
```

在GitHub创建目标为 `develop` 的PR。另一成员检查契约、迁移、测试证据和Diff；合并后删除远端feature分支。出现冲突时由实际了解两侧语义的成员在feature分支解决，不使用强推覆盖共享历史。

## 双Codex协作检查

1. 开始任务时把当前分支、目标任务ID和适用契约告知Codex。
2. Codex必须先读取仓库 `AGENTS.md`，不能仅依赖聊天中的旧设计。
3. 数据库变化只通过新的Flyway文件；不得修改已合并迁移。
4. 契约变化先做文档PR，再实现或在同一有序PR中同步全部消费者。
5. 发现另一分支已经实现同一概念时停止新增，先共同选择一个模型。
