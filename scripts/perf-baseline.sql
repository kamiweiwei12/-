\set ON_ERROR_STOP on
\timing on

-- PERF-01 首批可重复执行计划基线。
-- 只允许对隔离测试库 snowfield_test 执行；所有造数均在事务中并最终 ROLLBACK。
DO $$
BEGIN
    IF current_database() <> 'snowfield_test' THEN
        RAISE EXCEPTION 'perf-baseline.sql may only run against snowfield_test';
    END IF;
END
$$;

BEGIN;
SET LOCAL synchronous_commit = off;

TRUNCATE TABLE app_user, campground RESTART IDENTITY CASCADE;

INSERT INTO app_user (email, password_hash, display_name)
VALUES ('perf-baseline@example.com', 'not-a-real-password-hash', '性能基线用户');

INSERT INTO campground (code, name, timezone, currency)
VALUES ('perf', '性能基线营地', 'Asia/Shanghai', 'CNY');

INSERT INTO resource_type (campground_id, code, name, kind, base_capacity, base_price)
VALUES (1, 'PERF_CABIN', '性能基线木屋', 'CABIN', 4, 1280.00);

INSERT INTO resource_type_stay_mode (resource_type_id, stay_mode)
VALUES (1, 'OVERNIGHT');

INSERT INTO resource_unit (resource_type_id, code, name, capacity)
SELECT 1, 'PERF-' || lpad(g::text, 5, '0'), '性能资源-' || g, 4
FROM generate_series(1, 10000) AS g;

INSERT INTO customer_order
    (order_no, user_id, campground_id, status, total_amount,
     create_idempotency_key, create_request_fingerprint, expires_at)
SELECT 'PERF-ORDER-' || lpad(g::text, 5, '0'), 1, 1, 'PENDING_PAYMENT', 1280.00,
       'create-' || g, 'fingerprint-' || g,
       CASE WHEN g > 9900 THEN now() - interval '1 minute' ELSE now() + interval '1 day' END
FROM generate_series(1, 10000) AS g;

INSERT INTO order_item
    (order_id, item_type, item_name_snapshot, quantity, unit_price, subtotal, price_explanation)
SELECT g, 'LODGING', '性能资源-' || g, 1, 1280.00, 1280.00, '{}'::jsonb
FROM generate_series(1, 10000) AS g;

INSERT INTO lodging_reservation
    (order_item_id, resource_unit_id, stay_mode, stay_period, guest_count, occupancy_status)
SELECT g, g, 'OVERNIGHT',
       tstzrange('2027-12-20 15:00+08'::timestamptz,
                 '2027-12-21 12:00+08'::timestamptz, '[)'),
       2, 'HELD'
FROM generate_series(1, 10000) AS g;

INSERT INTO payment
    (order_id, payment_no, status, amount, method, idempotency_key, request_fingerprint)
SELECT g, 'PERF-PAY-' || lpad(g::text, 5, '0'), 'FAILED', 1280.00, 'SIMULATED',
       'payment-' || g, 'payment-fingerprint-' || g
FROM generate_series(1, 10000) AS g;

ANALYZE customer_order;
ANALYZE lodging_reservation;
ANALYZE payment;

\echo 'PLAN expiry_scan'
EXPLAIN (ANALYZE, BUFFERS)
SELECT order_id
FROM customer_order
WHERE status = 'PENDING_PAYMENT' AND expires_at <= now()
ORDER BY order_id
LIMIT 100
FOR UPDATE SKIP LOCKED;

\echo 'PLAN lodging_overlap'
EXPLAIN (ANALYZE, BUFFERS)
SELECT reservation_id
FROM lodging_reservation
WHERE resource_unit_id = 5000::bigint
  AND stay_period && tstzrange('2027-12-20 16:00+08'::timestamptz,
                               '2027-12-20 17:00+08'::timestamptz, '[)')
  AND occupancy_status IN ('HELD', 'CONFIRMED', 'IN_USE');

\echo 'PLAN lodging_overlap_index_diagnostic'
SET LOCAL enable_seqscan = off;
EXPLAIN (ANALYZE, BUFFERS)
SELECT reservation_id
FROM lodging_reservation
WHERE resource_unit_id = 5000::bigint
  AND stay_period && tstzrange('2027-12-20 16:00+08'::timestamptz,
                               '2027-12-20 17:00+08'::timestamptz, '[)')
  AND occupancy_status IN ('HELD', 'CONFIRMED', 'IN_USE');
SET LOCAL enable_seqscan = on;

\echo 'PLAN payment_idempotency'
EXPLAIN (ANALYZE, BUFFERS)
SELECT payment_id, payment_no, order_id, status, amount, request_fingerprint, paid_at
FROM payment
WHERE order_id = 5000 AND idempotency_key = 'payment-5000';

ROLLBACK;
