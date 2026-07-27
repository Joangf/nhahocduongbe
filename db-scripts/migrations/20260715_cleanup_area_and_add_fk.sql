-- =============================================================================
-- Migration: Option B — Dọn dẹp common_area + thêm UNIQUE + FK
-- File: db-scripts/migrations/20260715_cleanup_area_and_add_fk.sql
-- Steps:
--   1. Xóa districts/communes con của 63 tỉnh cũ (active=0)
--   2. Xóa 63 tỉnh cũ (active=0, id < 200)
--   3. Xóa cột active (không còn cần thiết sau khi chỉ còn 34 tỉnh)
--   4. Thêm UNIQUE constraint trên common_area(code)
--   5. Thêm FK nhahocduong_organization(area_code) → common_area(code)
-- =============================================================================

SET search_path TO nhahocduong, public;

BEGIN;

-- =============================================================================
-- SAFETY CHECK trước khi chạy:
-- Đảm bảo tất cả area_code trong organization đều hợp lệ
-- (phải trả về 0 rows — nếu có rows thì DỪNG LẠI và fix trước)
-- =============================================================================
DO $$
DECLARE
  invalid_count INT;
BEGIN
  SELECT COUNT(DISTINCT area_code) INTO invalid_count
  FROM nhahocduong_organization
  WHERE area_code IS NOT NULL
    AND area_code NOT IN (
      SELECT code FROM common_area WHERE type = 1 AND active = 1
    );

  IF invalid_count > 0 THEN
    RAISE EXCEPTION
      'ABORT: Còn % area_code trong organization không hợp lệ. Fix data trước khi chạy migration này.',
      invalid_count;
  END IF;
END $$;

-- =============================================================================
-- BƯỚC 1: Xóa level 3 (communes/wards) con của các tỉnh cũ
--   parent_area_id trỏ đến district, district trỏ đến province cũ
-- =============================================================================
DELETE FROM common_area
WHERE type = 3
  AND parent_area_id IN (
    SELECT id FROM common_area
    WHERE type = 2
      AND parent_area_id IN (
        SELECT id FROM common_area WHERE type = 1 AND active = 0
      )
  );

-- =============================================================================
-- BƯỚC 2: Xóa level 2 (districts) con của các tỉnh cũ
-- =============================================================================
DELETE FROM common_area
WHERE type = 2
  AND parent_area_id IN (
    SELECT id FROM common_area WHERE type = 1 AND active = 0
  );

-- =============================================================================
-- BƯỚC 3: Xóa 63 tỉnh cũ (type=1, active=0, id < 200)
-- =============================================================================
DELETE FROM common_area
WHERE type = 1
  AND active = 0
  AND id < 200;

-- =============================================================================
-- BƯỚC 4: Xóa cột active khỏi common_area
--   (không còn cần thiết, mọi province còn lại đều active)
-- =============================================================================
ALTER TABLE common_area DROP COLUMN IF EXISTS active;

-- =============================================================================
-- BƯỚC 5: Thêm UNIQUE constraint trên common_area(code)
--   Sau khi xóa tỉnh cũ, mỗi code là duy nhất trong toàn bảng
-- =============================================================================
ALTER TABLE common_area
  ADD CONSTRAINT uq_common_area_code UNIQUE (code);

-- =============================================================================
-- BƯỚC 6: Thêm FK nhahocduong_organization(area_code) → common_area(code)
--   NOT VALID: kiểm tra constraint chỉ với dữ liệu mới,
--              không lock toàn bảng khi alter (tốt cho production)
-- =============================================================================
ALTER TABLE nhahocduong_organization
  ADD CONSTRAINT fk_organization_area_code
  FOREIGN KEY (area_code)
  REFERENCES common_area(code)
  ON UPDATE CASCADE  -- nếu code tỉnh thay đổi, tự động cập nhật organization
  ON DELETE RESTRICT -- không cho xóa tỉnh nếu còn organization dùng
  NOT VALID;

-- Validate FK (chạy riêng, không block writes):
ALTER TABLE nhahocduong_organization
  VALIDATE CONSTRAINT fk_organization_area_code;

COMMIT;

-- =============================================================================
-- VERIFICATION sau commit:
-- =============================================================================
-- 1. Kiểm tra common_area chỉ còn 34 tỉnh:
--    SELECT count(*) FROM common_area WHERE type = 1;
--    → 34

-- 2. Kiểm tra UNIQUE và FK tồn tại:
--    SELECT constraint_name, constraint_type
--    FROM information_schema.table_constraints
--    WHERE table_name IN ('common_area', 'nhahocduong_organization')
--      AND constraint_type IN ('UNIQUE', 'FOREIGN KEY');

-- 3. Kiểm tra AreaRepository không còn dùng getByCodeAndActive:
--    (sau migration này, cột active đã bị xóa → cần update Java code luôn)
