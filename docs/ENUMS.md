# 公共 ENUM 契约

状态：`FROZEN v1.0`。枚举值在数据库、Java DTO、OpenAPI和TypeScript中必须完全一致。数据库优先使用 `varchar + CHECK`，便于 Flyway 演进；不自行创建同义值。

| 枚举 | 值 |
|---|---|
| UserRole | `CUSTOMER`, `ADMIN` |
| UserStatus | `ACTIVE`, `LOCKED`, `DISABLED` |
| StayMode | `OVERNIGHT`, `DAY_PASS` |
| ResourceKind | `GRASS_SITE`, `PLATFORM_SITE`, `RV_SITE`, `HOMESTAY_ROOM`, `CABIN` |
| ResourceStatus | `ACTIVE`, `MAINTENANCE`, `INACTIVE`, `RETIRED` |
| OrderStatus | `PENDING_PAYMENT`, `CONFIRMED`, `IN_USE`, `COMPLETED`, `CANCELLED`, `EXPIRED` |
| OrderItemStatus | `HELD`, `CONFIRMED`, `IN_USE`, `FULFILLED`, `CANCELLED`, `EXPIRED` |
| OrderItemType | `LODGING`, `SKI_TICKET`, `SKI_LESSON`, `LIFT_SLOT`, `EQUIPMENT_RENTAL` |
| PaymentStatus | `PENDING`, `SUCCEEDED`, `FAILED`, `REFUNDED` |
| PriceRuleType | `WEEKEND`, `SEASON`, `HOLIDAY`, `SPECIAL_DATE`, `MANUAL_OVERRIDE` |
| PriceOperation | `FIXED`, `MULTIPLIER`, `ADDITION` |
| PackagePricingMode | `FIXED_TOTAL`, `DISCOUNT_AMOUNT`, `DISCOUNT_RATE` |
| CapacityStatus | `OPEN`, `CLOSED`, `CANCELLED` |
| VerificationStatus | `UNUSED`, `USED`, `VOID` |
| EquipmentTrackingMode | `BULK`, `ASSET` |
| EquipmentAssetStatus | `AVAILABLE`, `RESERVED`, `RENTED`, `MAINTENANCE`, `RETIRED` |
| RentalStatus | `RESERVED`, `PICKED_UP`, `RETURNED`, `CANCELLED`, `OVERDUE` |
| InventoryMovementType | `RESERVE`, `RELEASE`, `PICK_UP`, `RETURN`, `MAINTENANCE_IN`, `MAINTENANCE_OUT`, `ADJUSTMENT` |
| MaintenanceStatus | `OPEN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |

## 订单状态转换

```text
PENDING_PAYMENT -> CONFIRMED | CANCELLED | EXPIRED
CONFIRMED       -> IN_USE | CANCELLED
IN_USE          -> COMPLETED
COMPLETED       -> (terminal)
CANCELLED       -> (terminal)
EXPIRED         -> (terminal)
```

退款是支付状态，不新增“REFUNDED订单状态”。订单取消后若已付款，支付记录转为 `REFUNDED`。

订单项独立履约；整单 `IN_USE` 表示至少一个订单项处于 `IN_USE`，只有全部非取消项均为 `FULFILLED` 时整单进入 `COMPLETED`。v1只允许整单取消，不提供部分取消状态。

## 资源与资产状态说明

- `ResourceStatus` 是运营可售状态；真实可用性还要结合开放规则和订单时间占用。
- `EquipmentAssetStatus` 用于当前快速展示；资产历史以租赁占用、维修记录和库存流水为准。
- 状态改变必须由后端用例完成，前端不得直接提交任意目标状态。
