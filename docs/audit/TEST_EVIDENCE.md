# 测试与检查证据

## 证据状态

- `PASS`：实际执行、退出码0且覆盖目标。
- `FAIL`：实际执行失败或输出违反目标。
- `NOT RUN`：未执行并说明原因。
- `UNVERIFIED`：现有证据不足以覆盖目标。

## TE-20261125-01：开始前Git状态

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-25 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `288273692bdd48eb597b163f72c7c8e0af90ea9d` |
| 环境 | Windows PowerShell；工作目录 `D:/Will/SYSU/数据库系统原理/大作业` |
| 命令 | `git status --short --branch`；`git rev-list --left-right --count HEAD...origin/feature/collaboration-prep` |
| 实际输出摘要 | 工作区干净；ahead/behind为 `0 0` |
| 退出码 | 0 |
| 结论 | PASS：仅证明开始时分支正确、工作区干净且跟踪分支无分歧 |

## TE-20261125-02：历史来源读取

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-25 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `288273692bdd` |
| 命令 | `git show 2882736^:docs/PHASE1_REVIEW.md`；`git show 2882736^:docs/PHASE2_DECISIONS.md`；`git diff 2882736^ 2882736 -- <contract files>` |
| 实际输出摘要 | 取回 `0cd6bae` 中18项原始提案、PROPOSED状态和集中待回复问题；确认 `2882736` 将其改写为确认结果并标记FROZEN v1.0 |
| 退出码 | 0 |
| 结论 | PASS：证明本轮历史恢复基于Git对象而非记忆；不证明业务决定已获双人批准 |

## 未运行测试

| 类别 | 状态 | 原因 |
|---|---|---|
| Spring Boot/JUnit | NOT RUN | 本轮禁止初始化或开发业务代码，仓库无后端工程 |
| React lint/build | NOT RUN | 本轮禁止初始化前端工程，仓库无前端工程 |
| PostgreSQL/Flyway | NOT RUN | 本轮禁止初始化数据库及创建迁移 |
| 并发/性能测试 | NOT RUN | 无可运行系统，不能产生真实业务结果 |

## TE-20261125-03：项目Skill结构验证

| 尝试 | 命令/环境 | 实际输出摘要 | 退出码 | 结论 |
|---|---|---|---|---|
| 1 | `python quick_validate.py <skill>` | WindowsApps占位解释器不可用，三个Skill均返回9009 | 9009（各调用） | FAIL；未执行到Skill内容验证 |
| 2 | `py quick_validate.py <skill>` | Python 3.14按GBK读取UTF-8中文，三个Skill均抛出 `UnicodeDecodeError` | 1（各调用） | FAIL；编码环境不兼容 |
| 3 | 当前进程设置 `PYTHONUTF8=1` 后执行 `py quick_validate.py <skill>` | contract-change、git-pr、test-evidence均输出 `Skill is valid!` | 0（各调用） | PASS；仅证明frontmatter、命名和脚手架结构通过官方快速校验，不证明规则设计正确 |

## TE-20261125-04：文档与契约静态检查

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-25 16:29 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `288273692bdd`（未提交工作区） |
| 命令 | `git diff 0cd6bae --`历史文件；`rg`版本/契约术语；PowerShell Markdown内部链接检查；`rg`凭据特征；`git diff --check` |
| 实际输出摘要 | 历史文件相对0cd6bae只新增顶部历史说明和末尾确认引用；候选状态命中符合预期；八项术语在对应事实源/Issue中出现；内部链接OK；未发现GitHub token/私钥特征；diff check无错误，仅CRLF转换警告 |
| 退出码 | 0（组合检查命令） |
| 结论 | PASS：覆盖文档结构、链接、已登记术语和空白错误；不证明待决业务方案、运行时行为或独立审计结论 |

## TE-20261125-05：完整diff人工自检

| 字段 | 内容 |
|---|---|
| 命令 | 按文件组执行 `git diff --no-ext-diff -- <changed tracked files>`；新文件使用 `Get-Content -Raw` 完整读取 |
| 实际输出摘要 | 已检查状态文档、三份契约、业务流程、历史恢复、审计文件及三个Skill；发现并补充 `customer_order.create_request_fingerprint` 后继续检查 |
| 退出码 | 0 |
| 结论 | PASS：证明本轮自检覆盖全部变更文本；这是Codex自检，不是独立审计或双人Review |

## TE-20261125-06：主整改提交

| 字段 | 内容 |
|---|---|
| 命令 | `git commit -m "docs(audit): remediate R-3 and R-4 findings"`；`git rev-parse HEAD`；`git status --short --branch` |
| 实际输出摘要 | 创建提交 `0586c32`，26个文件、508行新增、51行删除；完整SHA为 `0586c32a457e8063534ecb753ba323cbe5b764a9`；提交后ahead 1且工作区干净 |
| 退出码 | 0 |
| 结论 | PASS：证明主整改已提交到授权feature分支；不代表已推送、已Review或已合并 |

## TE-20261125-07：推送和远端核对

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-25 16:32:13 +08:00 |
| 命令 | `git push origin feature/collaboration-prep`；`git rev-list --left-right --count HEAD...origin/feature/collaboration-prep`；`git ls-remote --heads origin feature/collaboration-prep develop main` |
| 实际输出摘要 | 推送 `2882736..566e53b`；ahead/behind为 `0 0`；远端feature为 `566e53b84ff2a3d4223bb144013471f0bd25bbfd`；main/develop仍为 `9c9cd3c8bbc7391da9638140a47bd6d92175ef57` |
| 退出码 | 0 |
| 结论 | PASS：证明两项整改提交已推送且未修改main/develop；不代表PR已Review或合并 |

## TE-20261125-08：批准同步开始状态

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-25 17:05:49 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `131ec982be0af8f2afe8e272a66086505430e6a7` |
| 命令 | `git status --short --branch`；`git rev-parse HEAD`；`git rev-list --left-right --count HEAD...origin/feature/collaboration-prep` |
| 实际输出摘要 | 开始工作区干净；跟踪授权feature分支；ahead/behind为 `0 0` |
| 退出码 | 0 |
| 结论 | PASS：仅证明本次同步开始时Git前置状态符合要求 |

## TE-20261125-09：批准决策文档静态检查

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-25 17:08:46 +08:00 |
| 环境 | Windows PowerShell；工作目录 `D:/Will/SYSU/数据库系统原理/大作业`；未提交工作区 |
| 命令 | `rg`决策状态/跨契约术语；PowerShell Markdown内部链接检查；`rg`凭据特征；分组 `git diff --no-ext-diff`；`git diff --check` |
| 实际输出摘要 | D-001/B和D-002/A在DB/API/ENUM/需求/流程/治理文档中可追踪；内部链接输出 `Markdown internal links: OK`；凭据扫描输出 `no matches`；diff check无空白错误，仅显示预期LF→CRLF警告 |
| 退出码 | 0（各次可见重跑） |
| 结论 | PASS：证明文档链接、登记术语和diff格式通过静态检查；完整diff为Codex自检，不是独立审计或人工PR Review |

## TE-20261125-10：业务与运行时测试

| 类别 | 状态 | 原因 |
|---|---|---|
| D-001 DDL/发布/报价测试 | NOT RUN | 本轮禁止创建Flyway和业务代码，当前只有候选契约 |
| D-002调度/时钟/并发测试 | NOT RUN | 本轮禁止初始化Spring Boot或实现调度器 |
| PostgreSQL事务与库存测试 | NOT RUN | 无数据库工程或迁移，本次批准不等于运行时验证 |

## TE-20261125-11：批准同步提交与推送

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-25 +08:00 |
| 命令 | `git diff --cached --check`；`git commit -m "docs(contract): approve D-001 and D-002"`；`git push origin feature/collaboration-prep`；`git rev-list --left-right --count HEAD...origin/feature/collaboration-prep`；`git ls-remote --heads origin feature/collaboration-prep develop main` |
| 实际输出摘要 | cached diff check无错误；创建 `df0e33cbc67bafcef546692334f6b47145f6d6a7`；推送 `131ec98..df0e33c`；ahead/behind `0 0`；远端feature指向df0e33c，main/develop仍指向9c9cd3c |
| 退出码 | 0 |
| 结论 | PASS：证明批准同步主提交已推送到授权feature分支且未推进main/develop；不证明PR已创建、Review或合并 |
