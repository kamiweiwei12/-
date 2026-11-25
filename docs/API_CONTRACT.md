# REST API 公共契约

状态：`DRAFT v0.1`。基础路径 `/api/v1`，媒体类型 `application/json`。

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
- 创建支付、取消、核销支持 `Idempotency-Key` 请求头。

## 2. 核心 DTO

```text
ResourceAvailabilityQuery
  campgroundId:string, stayMode:enum, startAt:datetime, endAt:datetime,
  guestCount:int, resourceKind?:enum, page:int, size:int

QuoteRequest
  resourceUnitId:string, stayMode:enum, startAt:datetime, endAt:datetime,
  guestCount:int, skiSelections:[], liftSelections:[], equipmentSelections:[]

CreateOrderRequest
  quoteToken:string, contact:{name,phone}, remark?:string

OrderResponse
  id:string, orderNo:string, status:enum, currency:"CNY",
  totalAmount:string, expiresAt?:datetime, items:OrderItemResponse[]
```

`quoteToken` 是服务端签名/短期报价标识；创建订单仍重新校验价格和库存，不把前端金额作为输入。

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
| GET | `/price-calendar` | Public | 给定类型与月份的每日起价 |

### 订单与支付

| Method | Path | Auth | 说明 |
|---|---|---|---|
| POST | `/orders` | Customer | 事务化创建订单和占用 |
| GET | `/orders/me` | Customer | 我的订单 |
| GET | `/orders/{id}` | Owner/Admin | 订单详情 |
| POST | `/orders/{id}/payments/simulate` | Owner | 模拟成功/失败 |
| POST | `/orders/{id}/cancel` | Owner/Admin | 幂等取消和释放 |

### 滑雪、课程和缆车

| Method | Path | Auth | 说明 |
|---|---|---|---|
| GET | `/ski/products` | Public | 票种/课程产品 |
| GET | `/ski/sessions` | Public | 可用课程/票务场次 |
| GET | `/lifts/slots` | Public | 缆车时段及剩余容量 |
| POST | `/verifications` | Admin | 核销票/预约码 |

### 装备

| Method | Path | Auth | 说明 |
|---|---|---|---|
| GET | `/equipment/models` | Public | 型号和时段可用量 |
| POST | `/admin/equipment/rentals/{id}/pickup` | Admin | 取件 |
| POST | `/admin/equipment/rentals/{id}/return` | Admin | 归还 |
| POST | `/admin/equipment/assets/{id}/maintenance` | Admin | 建维修记录 |

### 管理端

管理资源、价格、场次、时段、装备和订单统一放在 `/admin/**`，CRUD 使用复数名词；删除有历史的主数据改为停用。

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
| 409 | `BOOKING_CONFLICT`, `CAPACITY_EXCEEDED`, `INVENTORY_INSUFFICIENT`, `ALREADY_VERIFIED`, `IDEMPOTENCY_CONFLICT` |
| 422 | `PRICE_RULE_CONFLICT`, `ORDER_EXPIRED`, `RESOURCE_NOT_OPEN` |
| 500 | `INTERNAL_ERROR` |

数据库唯一/排斥约束必须映射成稳定业务错误，不能把SQL细节直接返回前端。

## 5. 认证契约

- 登录成功返回短期 access token 和用户摘要；第一版不实现复杂第三方登录。
- 密码不出现在响应或日志。
- `/admin/**` 要求 ADMIN；资源所有权在服务层二次校验。
- CORS 只允许配置的前端源。

## 6. 需要确认

- access token 有效期（建议30分钟）以及是否实现refresh token。
- 报价令牌有效期（建议5分钟）和订单占用期（建议15分钟）。
- 是否允许游客报价、登录后下单（建议允许）。

