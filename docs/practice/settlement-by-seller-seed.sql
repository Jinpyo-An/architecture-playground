-- ============================================================
-- 판매자별 정산 검증용 시드 데이터
--
-- 시나리오: 오늘(CURRENT_DATE) 기준 PAID+미정산 주문 4건을 3명의 판매자에 분배
--   Seller A : 주문 2건 (gross 10000 + 20000)
--   Seller B : 주문 1건 (gross 50000)
--   Seller C : 주문 1건 (gross 7000)
--
-- 정산 실행 시 → 배치 3건이 판매자별로 생성되어야 한다.
--
-- 실행 순서
--   1) ./gradlew bootRun  (서버 1회 기동으로 settlement_batch.seller_id 컬럼 자동 추가)
--   2) psql로 본 스크립트 실행 (또는 IDE DB 콘솔)
--   3) POST /api/v1/settlements
--      body: { "settlementDate": "<오늘 YYYY-MM-DD>", "actorId": "<임의 UUID>" }
--   4) 응답이 길이 3 배열인지, 각 원소가 sellerId/total_*을 가지는지 확인
--   5) 본 파일 하단의 검증 SELECT (A) ~ (D) 실행
--
-- 재실행 안전 : 모든 INSERT는 ON CONFLICT (id) DO NOTHING
-- ============================================================

BEGIN;

-- 1) 판매자 3명 -------------------------------------------------
INSERT INTO public."seller"
  (id, email, "name", business_number, status, reg_id, reg_dt, modify_id, modify_dt)
VALUES
  ('11111111-1111-1111-1111-111111111111',
   'seller-a@example.com', '판매자 A', '100-00-00001', 'ACTIVE',
   '11111111-1111-1111-1111-111111111111', NOW(),
   '11111111-1111-1111-1111-111111111111', NOW()),
  ('22222222-2222-2222-2222-222222222222',
   'seller-b@example.com', '판매자 B', '100-00-00002', 'ACTIVE',
   '22222222-2222-2222-2222-222222222222', NOW(),
   '22222222-2222-2222-2222-222222222222', NOW()),
  ('33333333-3333-3333-3333-333333333333',
   'seller-c@example.com', '판매자 C', '100-00-00003', 'ACTIVE',
   '33333333-3333-3333-3333-333333333333', NOW(),
   '33333333-3333-3333-3333-333333333333', NOW())
ON CONFLICT (id) DO NOTHING;

-- 2) 상품 3개 (판매자별 1개) ------------------------------------
INSERT INTO public."product"
  (id, seller_id, "name", description, price, stock, status,
   reg_id, reg_dt, modify_id, modify_dt)
VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   '11111111-1111-1111-1111-111111111111',
   '상품 A', 'A 판매자 상품', 10000, 100, 'ACTIVE',
   '11111111-1111-1111-1111-111111111111', NOW(),
   '11111111-1111-1111-1111-111111111111', NOW()),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
   '22222222-2222-2222-2222-222222222222',
   '상품 B', 'B 판매자 상품', 50000, 100, 'ACTIVE',
   '22222222-2222-2222-2222-222222222222', NOW(),
   '22222222-2222-2222-2222-222222222222', NOW()),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc',
   '33333333-3333-3333-3333-333333333333',
   '상품 C', 'C 판매자 상품', 7000, 100, 'ACTIVE',
   '33333333-3333-3333-3333-333333333333', NOW(),
   '33333333-3333-3333-3333-333333333333', NOW())
ON CONFLICT (id) DO NOTHING;

-- 3) 주문 4건 -- 모두 오늘 paid_at, status=PAID, settled=false ---
--    paid_at = CURRENT_DATE + 시각 → settlementDate=CURRENT_DATE 호출 시 매칭
INSERT INTO public."order"
  (id, order_no, buyer_id, seller_id, product_id, quantity,
   gross_amount, fee_amount, refund_amount, net_amount,
   status, paid_at, settled, settlement_batch_id,
   reg_id, reg_dt, modify_id, modify_dt)
VALUES
  -- Seller A 주문 1
  ('a1111111-1111-1111-1111-111111111111',
   'ORD-SELLER-A-001',
   '99999999-9999-9999-9999-999999999991',
   '11111111-1111-1111-1111-111111111111',
   'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   1, 10000, 500, 0, 9500,
   'PAID', CURRENT_DATE + TIME '09:00:00', FALSE, NULL,
   '11111111-1111-1111-1111-111111111111', NOW(),
   '11111111-1111-1111-1111-111111111111', NOW()),

  -- Seller A 주문 2
  ('a2222222-2222-2222-2222-222222222222',
   'ORD-SELLER-A-002',
   '99999999-9999-9999-9999-999999999992',
   '11111111-1111-1111-1111-111111111111',
   'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
   2, 20000, 1000, 500, 18500,
   'PAID', CURRENT_DATE + TIME '10:30:00', FALSE, NULL,
   '11111111-1111-1111-1111-111111111111', NOW(),
   '11111111-1111-1111-1111-111111111111', NOW()),

  -- Seller B 주문 1
  ('b1111111-1111-1111-1111-111111111111',
   'ORD-SELLER-B-001',
   '99999999-9999-9999-9999-999999999993',
   '22222222-2222-2222-2222-222222222222',
   'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
   1, 50000, 2500, 0, 47500,
   'PAID', CURRENT_DATE + TIME '11:00:00', FALSE, NULL,
   '22222222-2222-2222-2222-222222222222', NOW(),
   '22222222-2222-2222-2222-222222222222', NOW()),

  -- Seller C 주문 1
  ('c1111111-1111-1111-1111-111111111111',
   'ORD-SELLER-C-001',
   '99999999-9999-9999-9999-999999999994',
   '33333333-3333-3333-3333-333333333333',
   'cccccccc-cccc-cccc-cccc-cccccccccccc',
   1, 7000, 350, 1000, 5650,
   'PAID', CURRENT_DATE + TIME '13:15:00', FALSE, NULL,
   '33333333-3333-3333-3333-333333333333', NOW(),
   '33333333-3333-3333-3333-333333333333', NOW())
ON CONFLICT (id) DO NOTHING;

COMMIT;


-- ============================================================
-- 검증 쿼리
-- ============================================================

-- (A) 정산 실행 전 -- 후보 주문 4건 노출 / settled=false 확인
SELECT order_no, seller_id, gross_amount, fee_amount, refund_amount, net_amount,
       status, settled, paid_at
FROM public."order"
WHERE status = 'PAID'
  AND settled = FALSE
  AND paid_at >= CURRENT_DATE
  AND paid_at <  CURRENT_DATE + INTERVAL '1 day'
ORDER BY paid_at;


-- (B) 정산 실행 후 -- 판매자별 배치 3건 생성 + 총액 검증
SELECT id,
       seller_id,
       settlement_date,
       total_gross_amount,
       total_fee_amount,
       total_refund_amount,
       total_settlement_amount
FROM public."settlement_batch"
WHERE settlement_date = CURRENT_DATE
ORDER BY seller_id;
-- 예상:
--   seller_id 1111... : gross 30000, fee 1500, refund  500, settlement 28000
--   seller_id 2222... : gross 50000, fee 2500, refund    0, settlement 47500
--   seller_id 3333... : gross  7000, fee  350, refund 1000, settlement  5650


-- (C) 정산 실행 후 -- 각 배치의 sellerId == 그 안 아이템의 sellerId
SELECT b.seller_id  AS batch_seller,
       i.seller_id  AS item_seller,
       i.order_no,
       i.settlement_amount
FROM public."settlement_batch" b
JOIN public."settlement_item"  i ON i.settlement_batch_id = b.id
WHERE b.settlement_date = CURRENT_DATE
ORDER BY b.seller_id, i.order_no;
-- 모든 row 에서 batch_seller = item_seller 여야 함


-- (D) 정산 실행 후 -- 주문이 settled=true 로 갱신되고 배치 ID 매핑
SELECT order_no, seller_id, settled, settlement_batch_id
FROM public."order"
WHERE id IN (
  'a1111111-1111-1111-1111-111111111111',
  'a2222222-2222-2222-2222-222222222222',
  'b1111111-1111-1111-1111-111111111111',
  'c1111111-1111-1111-1111-111111111111'
)
ORDER BY order_no;
-- 4건 모두 settled=TRUE, settlement_batch_id 가 해당 seller 의 배치 ID 와 일치


-- ============================================================
-- 재실험 (cleanup) -- 같은 시드로 다시 정산을 돌려보고 싶을 때만
-- ============================================================
-- BEGIN;
-- UPDATE public."order"
-- SET settled = FALSE,
--     settlement_batch_id = NULL,
--     modify_dt = NOW()
-- WHERE id IN (
--   'a1111111-1111-1111-1111-111111111111',
--   'a2222222-2222-2222-2222-222222222222',
--   'b1111111-1111-1111-1111-111111111111',
--   'c1111111-1111-1111-1111-111111111111'
-- );
-- DELETE FROM public."settlement_item"
--  WHERE settlement_batch_id IN (
--    SELECT id FROM public."settlement_batch" WHERE settlement_date = CURRENT_DATE
--  );
-- DELETE FROM public."settlement_batch" WHERE settlement_date = CURRENT_DATE;
-- COMMIT;
