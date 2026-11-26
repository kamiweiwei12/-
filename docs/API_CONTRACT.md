# REST API 公共契约

状态：`CANDIDATE v1.0-rc`，等待另一成员真实 PR Review。基础路径 `/api/v1`，媒体类型 `application/json`。

## 1. 通用格式

成功：

```json
{
  "success": true,
  "data": {},
  "traceId": "01J...",
  "timestamp": "2026-11-25T10:00:00+08:00"
}
```

失败：

```json
{
  "success": false,
  "error": {
    "code": "BOOKING_CONFLICT",
    "message": "所选资源已被预订",
    "fieldErrors": [{"field": "startAt", "message": "必须早于结束时间"}],
    "details": {}
  },
  "traceId": "01J...",
  "timestamp": "2026-11-25T10:00:00+08:00"
}
```

- ID 为字符串；金额为两位十进制字符串；日期 `YYYY-MM-DD`；时刻为带时区 ISO-8601。
- 分页 `page` 从0开始，`size` 默认20且最大100。
- 认证：`Authorization: Bearer <accessToken>`。
- 创建订单、创建支付、取消、核销必须支持 `Idempotency-Key` 请求头。

## 2. 核心 DTO

```text
ResourceAvailabilityQuery
  campgroundId:string, stayMode:enum, startAt:datetime, endAt:datetime,
  guestCount:int, resourceKind?:enum, page:int, size:int

QuoteRequest
  lodgingSelections:LodgingSelection[], packageSelections:PackageSelection[],
  skiSelections:SkiSelection[], liftSelections:LiftSelection[],
  equipmentSelections:EquipmentSelection[]

CreateOrderRequest
  expectedTotal:string, contact:{name,phone}, remark?:string,
  lodgingSelections:LodgingSelection[], packageSelections:PackageSelection[],
  skiSelections:SkiSelection[], liftSelections:LiftSelection[],
  equipmentSelections:EquipmentSelection[]

OrderResponse
  id:string, orderNo:string, status:enum, currency:"CNY",
  totalAmount:string, expiresAt?:datetime,
  packages:OrderPackageResponse[], items:OrderItemResponse[]

LodgingSelection
  resourceUnitId:string, stayMode:StayMode, startAt:datetime, endAt:datetime,
  guestCount:int

SkiSelection
  skiSessionId:string, quantity:int

LiftSelection
  liftSlotId:string, quantity:int

EquipmentSelection
  equipmentModelId:string, startAt:datetime, endAt:datetime, quantity:int

PackageSelection
  packageOfferId:string, components:PackageComponentSelection[]

PackageComponentSelection
  packageComponentId:string, itemType:OrderItemType, quantity:int,
  resourceUnitId?:string, stayMode?:StayMode, startAt?:datetime, endAt?:datetime,
  guestCount?:int, skiSessionId?:string, liftSlotId?:string,
  equipmentModelId?:string
```

`LodgingSelection` 每项指定一个不同的具体 `resourceUnitId`、住宿模式、起止时刻和人数，因此一次订单可预订多间房。`PackageComponentSelection` 是按 `itemType` 判别的结构，只允许对应目标字段非空；同一 `packageComponentId` 可出现多行以选择多个具体资源，其数量合计必须等于目录组件要求。`LodgingSelection` **不提供 `quantity` 字段**，每一行固定生成一个数量为1的订单项，房间数量由数组元素个数表达；装备的具体ASSET由服务端分配，客户端只选择型号。套餐和单选数组可同时非空，独立数组只表达套餐之外的单选项。一个订单内全部项目必须属于同一营地。

系统不使用 `quoteToken`。创建订单必须重算全部价格和库存，`expectedTotal` 只用于发现用户看到的报价是否变化，不能覆盖服务端金额。若不同，返回409 `PRICE_CHANGED`，`error.details`包含新的报价，且不创建订单。

混合购物车在单个事务内原子创建：任何住宿冲突、容量不足或装备不足都会使整单失败。

## 3. 端点初稿

### 认证与用户

| Method | Path | Auth | 说明 |
|---|---|---|---|
| POST | `/auth/register` | Public | 注册 |
| POST | `/auth/login` | Public | 登录 |
| GET | `/users/me` | Customer/Admin | 当前用户 |

### 公共目录、可用性与报价

| Method | Path | Auth | 说明 |
|---|---|---|---|
| GET | `/campgrounds` | Public | 营地列表 |
| GET | `/resource-types` | Public | 资源类型 |
| GET | `/resources/{id}` | Public | 资源详情 |
| GET | `/availability/resources` | Public | 真实空闲资源分页查询 |
| POST | `/quotes` | Public/Customer | 生成可解释报价 |
| GET | `/packages` | Public | 可购买套餐及组件要求 |
| GET | `/price-calendar` | Public | 给定类型与月份的每日起价 |

### 订单与支付

| Method | Path | Auth | 说明 |
|---|---|---|---|
| POST | `/orders` | Customer | 事务化创建订单和占用 |
| GET | `/orders/me` | Customer | 我的订单 |
| GET | `/orders/{id}` | Owner/Admin | 订单详情 |
| POST | `/orders/{id}/payments/simulate` | Owner | 模拟成功/失败 |
| POST | `/orders/{id}/cancel` | Owner/Admin | 幂等取消和释放 |

v1只支持整单模拟支付和整单取消；没有订单项部分支付、部分取消或部分退款接口。一张订单只产生一次应付总额，但订单项分别记录履约状态。只有全部订单项尚未开始履约时允许顾客取消。

模拟支付请求与响应：

```text
SimulatePaymentRequest
  desiredOutcome:"SUCCESS"|"FAILURE"

PaymentResponse
  id:string, paymentNo:string, orderId:string, status:PaymentStatus,
  amount:string, method:"SIMULATED", paidAt?:datetime
```

订单和支付的 `Idempotency-Key` 均由调用方生成，但作用域独立：创建订单按“已认证用户+键”，模拟支付按“订单+键”。同键同规范化请求返回原结果；同键不同请求返回409 `IDEMPOTENCY_CONFLICT`。支付失败后可使用新键重试；一旦成功，后续新键请求返回既有成功支付，不新建记录。

### 滑雪、课程和缆车

| Method | Path | Auth | 说明 |
|---|---|---|---|
| GET | `/ski/products` | Public | 票种/课程产品 |
| GET | `/ski/sessions` | Public | 可用课程/票务场次 |
| GET | `/lifts/slots` | Public | 缆车时段及剩余容量 |
| POST | `/verifications` | Admin | 核销票/预约码 |

核销契约：

```text
VerificationRequest
  tokenCode:string, liftSlotId?:string

VerificationResponse
  verificationEventId:string, tokenId:string, orderItemId:string,
  status:"USED", result:"ACCEPTED", verifiedAt:datetime,
  liftSlotId?:string, operatorId:string
```

调用方必须提供 `Idempotency-Key`。作用域为“已认证管理员+键”；服务端保存请求指纹。同键同请求返回原成功响应，同键不同请求返回 `IDEMPOTENCY_CONFLICT`。不同键重复核销同一令牌返回 `ALREADY_VERIFIED`；令牌行锁、状态更新和成功事件写入必须在同一事务中完成。

### 装备

| Method | Path | Auth | 说明 |
|---|---|---|---|
| GET | `/equipment/models` | Public | 型号和时段可用量 |
| POST | `/admin/equipment/rentals/{id}/pickup` | Admin | 取件 |
| POST | `/admin/equipment/rentals/{id}/return` | Admin | 归还 |
| POST | `/admin/equipment/assets/{id}/maintenance` | Admin | 建维修记录 |

装备租赁响应中的 `status` 使用公共 `RentalStatus`。`OVERDUE` 由后端定时任务写入，不由客户端提交目标状态，也不提供“手工标记逾期”接口；逾期不会释放库存，实际归还后响应才变为 `RETURNED`。调度间隔内读取到的 `PICKED_UP` 可能尚未完成状态投影更新，权威逾期事实仍由 `rental_period` 截止时刻判断。

### 管理端

管理资源、价格、场次、时段、装备和订单统一放在 `/admin/**`，CRUD 使用复数名词；删除有历史的主数据改为停用。

`/admin/price-rules` 允许 `resourceTypeId` 为空而 `stayMode` 非空，以表达全营地住宿模式规则；当两者均非空时，发布服务必须确认该组合存在于 `resource_type_stay_mode`。报价端再次按资源类型支持模式过滤，并继续执行 `PRICE_RULE_CONFLICT` 检查。

```text
/admin/resources
/admin/resource-open-rules
/admin/price-rules
/admin/orders
/admin/ski-sessions
/admin/lift-slots
/admin/equipment/models
/admin/equipment/assets
/admin/statistics/overview
```

## 4. HTTP状态和业务错误

| HTTP | 典型错误码 |
|---|---|
| 400 | `VALIDATION_FAILED`, `INVALID_TIME_RANGE`, `INVALID_STATE_TRANSITION` |
| 401 | `AUTH_REQUIRED`, `TOKEN_INVALID`, `TOKEN_EXPIRED` |
| 403 | `ACCESS_DENIED` |
| 404 | `RESOURCE_NOT_FOUND`, `ORDER_NOT_FOUND` |
| 409 | `BOOKING_CONFLICT`, `CAPACITY_EXCEEDED`, `INVENTORY_INSUFFICIENT`, `ALREADY_VERIFIED`, `IDEMPOTENCY_CONFLICT`, `PRICE_CHANGED`, `PRICE_RULE_CONFLICT` |
| 422 | `ORDER_EXPIRED`, `RESOURCE_NOT_OPEN`, `PACKAGE_NOT_AVAILABLE` |
| 500 | `INTERNAL_ERROR` |

数据库唯一/排斥约束必须映射成稳定业务错误，不能把SQL细节直接返回前端。

## 5. 认证契约

- 登录成功返回短期 access token 和用户摘要；第一版不实现复杂第三方登录。
- 密码不出现在响应或日志。
- `/admin/**` 要求 ADMIN；资源所有权在服务层二次校验。
- CORS 只允许配置的前端源。

## 6. 候选认证和时效

- access token有效期30分钟，v1不实现refresh token。
- 游客可查询和报价，创建订单必须登录。
- 待支付订单及其资源占用15分钟后过期。
