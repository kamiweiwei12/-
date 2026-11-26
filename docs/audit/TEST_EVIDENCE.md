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

## TE-20261126-01：开始前Git状态

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 09:42:13 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `08ff87f1fbb7b511628da5ec22ca26ec873ae6c6` |
| 环境 | Windows Git Bash；工作目录 `D:/Will/SYSU/数据库系统原理/大作业` |
| 命令 | `git branch --show-current`；`git rev-parse HEAD`；`git status --short --branch`；`git remote -v`；`git log -6 --decorate --oneline --all --graph`；`git rev-list --left-right --count HEAD...origin/feature/collaboration-prep`；`git ls-remote --heads origin feature/collaboration-prep develop main` |
| 实际输出摘要 | 分支为 `feature/collaboration-prep`；HEAD `08ff87f`；工作区无变更；ahead/behind `0 0`；远端feature `08ff87f`，`develop`/`main` 同为 `9c9cd3c`；最近提交为 `08ff87f`、`df0e33c`、`131ec98`、`566e53b`、`0586c32`、`2882736` |
| 退出码 | 0（各命令） |
| 结论 | PASS：仅证明开始时分支正确、跟踪远端无分歧、工作区干净；不证明PR或Review状态 |

## TE-20261126-02：PR、Review、协作者与分支保护核查

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 09:43–09:45 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `08ff87f` |
| 命令 | `gh auth status`；`gh pr list --state all`；`gh pr view 1 --json <metadata>`；`gh pr view 1 --json reviews,reviewRequests,reviewDecision`；`gh api repos/kamiweiwei12/-/pulls/1/reviews`；`gh api repos/kamiweiwei12/-/issues/1/timeline`；`gh api repos/kamiweiwei12/-/collaborators`；`gh api repos/kamiweiwei12/-/invitations`；`gh api repos/kamiweiwei12/-/branches`；`gh pr checks 1` |
| 实际输出摘要 | 登录账号 `kamiweiwei12`，scope `gist,read:org,repo`。PR #1 `OPEN`，作者 `kamiweiwei12`，创建于 `2026-11-25T08:47:47Z`，原 `baseRefName=main`，`headRefOid=08ff87f`，28文件+985/−61，`body=""`，`isDraft=false`，`mergeable=UNKNOWN`。`reviews=[]`、`reviewRequests=[]`、`reviewDecision=""`；`pulls/1/reviews` 返回 `[]`；timeline 仅8条 `committed`，无 `review_requested`/`reviewed`/`commented`。协作者：`kamiweiwei12`(admin)、`Janovo2117`(write)；`invitations=[]`。三分支 `protected:false`。`gh pr checks` 输出 `no checks reported on the 'feature/collaboration-prep' branch` |
| 退出码 | 0（各命令）；`gh pr checks` 为1（无检查项，非CI失败） |
| 结论 | PASS：证明PR存在、原目标分支为`main`、无任何Review与CI；`Janovo2117` 具备write权限但无Review活动。**不证明**双人Review已完成——“Review完成”结论为 `UNVERIFIED` |

## TE-20261126-03：纠正PR目标分支

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 09:46 +08:00 |
| 命令 | `gh pr edit 1 --repo kamiweiwei12/- --base develop`；随后 `gh pr view 1 --json baseRefName,headRefName,state,headRefOid,baseRefOid,changedFiles,additions,deletions,reviews,reviewDecision` |
| 实际输出摘要 | 命令输出PR链接，退出码0；复核结果 `baseRefName=develop`、`headRefName=feature/collaboration-prep`、`state=OPEN`、`baseRefOid=9c9cd3c`、`headRefOid=08ff87f`、28文件+985/−61、`reviews=[]`、`reviewDecision=""` |
| 退出码 | 0 |
| 结论 | PASS：证明PR目标分支已由`main`纠正为`develop`，未新增提交、未改写历史、未合并、未代替他人Review |

## TE-20261126-04：文档静态检查

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 09:47 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `08ff87f`（未提交工作区） |
| 命令 | `rg` 交叉检索 `occupancy_status`、`resource_type_id IS NULL`、`v1.0-rc`；Markdown内部链接检查；`git diff --check`；按文件分组 `git diff` |
| 实际输出摘要 | `occupancy_status` 在DATABASE_CONTRACT出现1处、ENUMS无对应枚举，证实R5-05；`price_rule` 定义中 `resource_type_id FK NULL` 与 `stay_mode NULL` 并存且无组合CHECK，证实R5-04；`v1.0-rc` 状态在冻结记录/三份契约/README一致；内部链接通过；`git diff --check` 无空白错误 |
| 退出码 | 0 |
| 结论 | PASS：证明本轮文档编辑的链接与空白格式正确，且R5-04/R5-05为文档可见的真实缺口（尚待Reviewer判定，未擅自修改契约条文） |

## TE-20261126-05：业务与运行时测试

| 类别 | 状态 | 原因 |
|---|---|---|
| Spring Boot/JUnit | NOT RUN | 仓库无后端工程，本轮未获授权初始化 |
| React lint/build | NOT RUN | 仓库无前端工程，本轮未获授权初始化 |
| PostgreSQL/Flyway | NOT RUN | 未获授权创建迁移或启动数据库 |
| D-001规则匹配/冲突测试 | NOT RUN | 仅候选契约，无实现 |
| D-002调度/时钟/并发测试 | NOT RUN | 无调度器实现 |
| 并发/性能测试 | NOT RUN | 无可运行系统 |

## TE-20261126-06：提交与推送

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 09:49 +08:00 |
| 命令 | `git add <6 files>`；`git diff --cached --check`；`git commit -m "docs(audit): record PR inspection and correct target branch"`；`git push origin feature/collaboration-prep`；`git rev-list --left-right --count HEAD...origin/feature/collaboration-prep`；`git ls-remote --heads origin feature/collaboration-prep develop main` |
| 实际输出摘要 | cached diff check退出码0；创建提交 `16e194e9daa1e78d45baa676518f0b0666a2fc45`（6文件，+134/−1）；推送 `08ff87f..16e194e`；ahead/behind `0 0`；远端feature为 `16e194e`，`develop`/`main` 仍为 `9c9cd3c` |
| 退出码 | 0（各命令） |
| 结论 | PASS：证明审计记录与目标分支纠正已推送到授权feature分支，且未推进main/develop；不证明PR已Review或合并 |

## TE-20261126-07：契约术语交叉检索与链接检查

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 10:20 +08:00 |
| 分支/HEAD | `feature/collaboration-prep` / `f3612db`（未提交工作区） |
| 命令 | `grep -rn "idempotency_key" docs/`；`grep -n "Occupan\|occupan" docs/ENUMS.md`；`grep -n "PENDING_PAYMENT" docs/DATABASE_CONTRACT.md`；Python 脚本检查 Markdown 内部链接；`git diff --check` |
| 实际输出摘要 | `idempotency_key` 命中 4 个文件：DATABASE_CONTRACT×9、DATABASE_DESIGN×3（均为复合候选码）、DECISION_LOG×1、ISSUES×1，无“全局唯一”表述残留；`ENUMS.md` 第20行出现 `OccupancyStatus`、第54行起三条说明；`DATABASE_CONTRACT` 中 `PENDING_PAYMENT` 仅剩第129行解释性表述，排斥谓词已为 `HELD`；链接检查输出 `broken: none`；`git diff --check` 无空白错误 |
| 退出码 | 0（各命令）；链接脚本 0 |
| 结论 | PASS：证明6处契约编辑的术语、链接与空白格式一致；不证明运行时DDL正确性（无数据库，NOT RUN） |

## TE-20261126-08：项目 Skill 结构校验（含一次失败）

| 尝试 | 命令/环境 | 实际输出摘要 | 退出码 | 结论 |
|---|---|---|---|---|
| 1 | `PYTHONUTF8=1 py quick_validate.py .agents/skills/contract-change`（`py` = Python 3.14.3） | `ModuleNotFoundError: No module named 'yaml'` | 1（三个Skill均同） | FAIL：解释器缺少 PyYAML，未执行到结构校验 |
| 2 | `py -m pip install pyyaml` | 提示 `Requirement already satisfied ... pythoncore-3.14-64`，说明存在另一个 3.14 安装 | 0 | 仅诊断，非校验结果 |
| 3 | 改用 `C:/Users/willz/AppData/Local/Python/pythoncore-3.14-64/python.exe` 执行同一脚本 | `contract-change`、`git-pr`、`test-evidence` 均输出 `Skill is valid!` | 0（各） | PASS：仅证明frontmatter、命名与脚手架结构通过官方快速校验，不证明规则设计正确 |

## TE-20261126-09：业务与运行时测试

| 类别 | 状态 | 原因 |
|---|---|---|
| PostgreSQL 排斥约束 DDL 验证 | NOT RUN | 未获授权创建 Flyway 或启动数据库 |
| 核销并发双写（部分唯一索引） | NOT RUN | 无实现，本轮仅改候选契约 |
| 占用状态流转测试 | NOT RUN | 无实现 |
| Spring Boot/Java 编译与 JUnit | NOT RUN | 仓库无后端工程 |
| React lint/build | NOT RUN | 仓库无前端工程 |

## TE-20261126-10：S1 工程骨架真实构建（含一次失败后修复）

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 22:21–22:22 +08:00（Asia/Shanghai） |
| 分支/HEAD | `feature/database-core` / `3404221` |
| 环境 | Windows Git Bash；JDK 21.0.12；Maven Wrapper 3.9.16（`distributionType=only-script`） |
| 命令 | `./mvnw -B test`（backend 目录） |
| 第一次输出摘要 | Spring Boot 4.1.3 上下文加载成功（`Started SnowfieldApplicationTests`）；`SnowfieldApplicationTests`/`ApiResponseTest`/`GlobalExceptionHandlerTest` 通过；`ArchitectureTest` 4 条分层规则（R5/R7/R8/R9）因 S1 无 `domain/api/infrastructure.persistence` 包触发 `failOnEmptyShould` 报错 |
| 第一次退出码 | 1（surefire 测试失败，非编译失败） |
| 修复 | 对 4 条分层规则加 `.allowEmptyShould(true)`（S1 尚无可匹配类；业务模块创建后自动成为真约束） |
| 第二次输出摘要 | `Tests run: 14, Failures: 0, Errors: 0, Skipped: 0`；`BUILD SUCCESS`；`Total time: 8.629 s` |
| 第二次退出码 | 0 |
| 结论 | PASS：证明 Spring Boot 4.1.3 + webmvc + validation + ArchUnit 在 Java 21 下真实编译与测试通过；不代表 MyBatis/Flyway/PostgreSQL 已接入（见 TE-20261126-11） |

## TE-20261126-11：尚未运行

| 类别 | 状态 | 原因 |
|---|---|---|
| MyBatis 4.1.0 集成 | NOT RUN | S3 才接入，尚未加入 pom |
| Flyway/PostgreSQL 迁移 | NOT RUN | 契约冻结（PR #2）未完成，S3 才建迁移 |
| Spring Security/JWT | NOT RUN | S4 才接入 |
| 并发/性能测试 | NOT RUN | 无可运行业务 |

## TE-20261126-12：治理 Skill 建立（文档任务）

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 22:5x +08:00（Asia/Shanghai） |
| 分支/HEAD | `feature/database-core` / `2bce281` |
| 命令 | `git diff --check` |
| 输出摘要 | 无空白/冲突错误输出 |
| 退出码 | 0 |
| 结论 | PASS：本轮仅新增/修改文档与 Skill，无业务、数据库、并发测试需求（NOT NEEDED） |

## TE-20261126-13：S2 依赖解析（MyBatis 4.1.0 / Flyway / PostgreSQL 驱动）

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 23:09–23:10 +08:00（Asia/Shanghai） |
| 分支/HEAD | `feature/database-core` / `eb67a1d` |
| 环境 | Windows；JDK 21.0.12；Maven 3.9.16（Wrapper 缓存）；Maven Central 联网 |
| 命令 | `<maven>/mvn.cmd -B dependency:tree`（backend 目录） |
| 输出摘要 | `mybatis-spring-boot-starter:4.1.0`、`mybatis:3.5.19`、`mybatis-spring:4.1.0`、`postgresql:42.7.14`、`spring-boot-starter-flyway:4.1.3`、`flyway-core:12.4.0`、`flyway-database-postgresql:12.4.0`；`BUILD SUCCESS` |
| 退出码 | 0 |
| 结论 | PASS：依赖可从 Maven Central 真实解析，版本与 `docs/ARCHITECTURE.md` §7 版本矩阵一致。仅证明「可解析」，不证明运行时兼容，后者见 TE-20261126-16 |

## TE-20261126-14：首次运行数据库集成测试失败（保留失败记录）

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 23:11 +08:00（Asia/Shanghai） |
| 分支/HEAD | `feature/database-core` |
| 命令 | `mvn -B -Pdb verify` |
| 失败摘要 | `FlywaySqlUnableToConnectToDbException` → `PSQLException`，`SQL State: 28P01`（password authentication failed for user "snowfield"）；`AppUserMapperIT` 6 errors、`FlywayMigrationIT` 9 errors |
| 退出码 | 非 0 |
| 根因（已定位） | 本机存在原生 PostgreSQL 18 服务（`postgresql-x64-18`，`D:\PostgreSQL`，PID 11152）监听 `0.0.0.0:5432`，与 Docker 容器端口映射冲突。Java 的 JDBC 连接实际到达本机 PG 18 实例而非容器，故密码必然不匹配。另发现 `localhost` 在 Windows 先解析为 `::1`，会命中 `pg_hba` 的 `scram-sha-256` 规则而非 `127.0.0.1` 的 `trust` 规则，加剧不一致 |
| 处置 | 将容器端口由 5432 改为 5433（`${POSTGRES_PORT:-5433}`），JDBC URL 显式使用 `127.0.0.1:5433`；经原生 JDBC 探针确认两库均可连接。未修改、未停止本机原生 PostgreSQL 18 服务 |
| 结论 | FAIL（保留），复测见 TE-20261126-16 |

## TE-20261126-15：真实 PostgreSQL 连通性探针（根因确认）

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 23:21 +08:00（Asia/Shanghai） |
| 命令 | `java -cp postgresql-42.7.14.jar T3.java`（JDK 21 单文件源码模式） |
| 输出摘要 | `OK jdbc:postgresql://127.0.0.1:5433/snowfield_test db=snowfield_test`；`OK jdbc:postgresql://127.0.0.1:5433/snowfield db=snowfield` |
| 退出码 | 0 |
| 结论 | PASS：5433 端口下两个库均以 `snowfield` 账号 + `.env.example` 占位密码连接成功，证明 TE-20261126-14 的 28P01 由端口冲突而非代码或配置引起 |

## TE-20261126-16：S2 数据库集成测试复测（Flyway + MyBatis + 约束；含默认构建）

| 字段 | 内容 |
|---|---|
| 日期 | 2026-11-26 23:22–23:24 +08:00（Asia/Shanghai） |
| 分支/HEAD | `feature/database-core` |
| 环境 | Windows；JDK 21.0.12；Maven 3.9.16；Docker 29.6.1；容器 `snowfield-db`（`postgres:16-alpine`，PostgreSQL 16.16，healthy，映射 `0.0.0.0:5433->5432`）；测试库 `snowfield_test` 由 `scripts/test-db-init.ps1` 创建 |
| 命令 A | `mvn -B -Pdb test`（`db` profile 才包含 `*IT`） |
| 命令 A 输出摘要 | `Migrating schema "public" to version "1 - baseline"`；`Successfully applied 1 migration to schema "public", now at version v1 (execution time 00:00.088s)`；`ArchitectureTest` 8、`ApiResponseTest` 2、`GlobalExceptionHandlerTest` 3、`AppUserMapperIT` 6、`FlywayMigrationIT` 9；合计 `Tests run: 28, Failures: 0, Errors: 0, Skipped: 0`；`BUILD SUCCESS` |
| 命令 A 退出码 | 0 |
| 命令 B | `mvn -B test`（默认构建，不连数据库） |
| 命令 B 输出摘要 | `Tests run: 14, Failures: 0, Errors: 0, Skipped: 0`；`BUILD SUCCESS` |
| 命令 B 退出码 | 0 |
| 隔离性核对 | `snowfield`（开发库）`public` schema 表数 = `0`；`snowfield_test` 的 `flyway_schema_history` = `1|baseline|t` |
| 结论 | PASS：证明 MyBatis 4.1.0 + Boot 4.1.3 + PostgreSQL 16 + Flyway 12.4.0 在 Java 21 下真实可用；V1 迁移在空测试库成功执行；唯一约束 `uq_app_user_email`、检查约束 `ck_app_user_email_lower`/`ck_app_user_status`、外键 `fk_user_role_user`、复合主键 `pk_user_role` 均能拒绝非法数据；失败语句不残留行；`#{}` 参数绑定生效。测试库与开发库隔离成立 |
| 验证边界 | 本证据只覆盖 `app_user`/`role`/`user_role` 三张表与连通链路；营地/资源/价格/订单/库存等契约表 **NOT RUN**；并发、幂等、性能测试 **NOT RUN**（S3/S4 范围） |

## TE-20261126-17：本任务未运行的测试

| 类别 | 状态 | 原因 |
|---|---|---|
| 并发预订 / 超卖 | NOT RUN | 无业务表与业务用例 |
| 订单幂等 / 支付 | NOT RUN | S4 范围 |
| Spring Security / JWT | NOT RUN | S4 范围 |
| 前端 lint/build | NOT RUN | 本次未触碰 `frontend/` |
| JMeter / EXPLAIN 性能 | NOT RUN | 无真实业务数据 |
