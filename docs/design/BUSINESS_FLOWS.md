# 主要业务流程

## 1. 住宿/日营预订

```mermaid
sequenceDiagram
    actor C as 顾客
    participant UI as React
    participant API as BookingService
    participant DB as PostgreSQL
    C->>UI: 选择模式、时段、人数
    UI->>API: 查询可用资源/报价
    API->>DB: 开放规则+维修+有效占用+价格规则
    DB-->>API: 可用资源和逐日价格
    API-->>UI: quoteToken+价格解释
    C->>UI: 确认下单
    UI->>API: POST /orders
    API->>DB: BEGIN; 重算价格; 插入订单/明细/占用
    DB-->>API: 排斥约束成功或冲突
    API->>DB: COMMIT
    API-->>UI: 待支付订单/过期时间
```

并发最终防线是数据库排斥约束，不依赖“查询时显示可用”。

## 2. 模拟支付

```mermaid
flowchart TD
    A[收到支付请求+Idempotency-Key] --> B{已有相同幂等结果?}
    B -- 是 --> C[返回原结果]
    B -- 否 --> D[锁定订单]
    D --> E{PENDING_PAYMENT且未过期?}
    E -- 否 --> F[业务错误]
    E -- 是 --> G[写模拟支付记录]
    G --> H{模拟结果}
    H -- 成功 --> I[支付SUCCEEDED/订单CONFIRMED]
    H -- 失败 --> J[支付FAILED/订单保持待支付]
```

## 3. 取消与释放

```mermaid
flowchart TD
    A[锁订单] --> B{可取消?}
    B -- 否 --> X[INVALID_STATE_TRANSITION]
    B -- 是 --> C[状态改CANCELLED]
    C --> D[释放住宿/容量/装备占用]
    D --> E[写状态历史和库存流水]
    E --> F{已支付?}
    F -- 是 --> G[模拟退款记录]
    F -- 否 --> H[提交]
    G --> H
```

重复取消读取到终态并返回相同结果，不再次递减容量。

## 4. 票务/缆车容量预约

```text
BEGIN
SELECT session/slot ... FOR UPDATE
校验 OPEN、时间、capacity - reserved_quantity >= request_quantity
插入 order_item 与 ski_booking/lift_booking
reserved_quantity += request_quantity
COMMIT
```

锁的顺序固定为订单→场次/时段（按ID升序），减少死锁。

## 5. 装备取还和维修

```mermaid
stateDiagram-v2
    [*] --> RESERVED
    RESERVED --> PICKED_UP: 核对资产/写PICK_UP流水
    PICKED_UP --> RETURNED: 归还/写RETURN流水
    RESERVED --> CANCELLED: 取消/RELEASE
    PICKED_UP --> OVERDUE: 超过应还时间
    RETURNED --> [*]
```

归还检查发现损坏时，在归还事务后创建维修记录，资产进入 MAINTENANCE；维修完成后写 `MAINTENANCE_OUT` 并恢复 AVAILABLE。维修区间与租赁区间都参与资产可用性判断。

## 6. 核销

读取二维码/文本码 → 哈希查找令牌 → 锁令牌 → 检查有效期、状态和适用时段 → 写核销事件 → 状态改 USED。唯一约束和行锁保证重复扫描只能一次成功。
