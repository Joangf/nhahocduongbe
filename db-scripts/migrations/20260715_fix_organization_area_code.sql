-- =============================================================================
-- Migration: Fix organization area_code còn sót sau merge 34 tỉnh
-- File: db-scripts/migrations/20260715_fix_organization_area_code.sql
-- Lý do: Một số tổ chức có area_code không tồn tại trong hệ 63 tỉnh cũ
--        (data dirty trước migration), không được cập nhật bởi migration trước.
--        Bây giờ common_area chỉ còn 34 tỉnh active (id >= 200),
--        nên mọi area_code của organization phải nằm trong '01'..'34'.
-- =============================================================================

SET search_path TO nhahocduong, public;

BEGIN;

-- =============================================================================
-- Kiểm tra trước: xem những area_code nào trong organization không hợp lệ
-- (không khớp với bất kỳ tỉnh active nào trong common_area)
-- =============================================================================
-- SELECT DISTINCT area_code, count(*) as so_truong
-- FROM nhahocduong_organization
-- WHERE area_code NOT IN (
--   SELECT code FROM common_area WHERE type = 1 AND active = 1
-- )
-- GROUP BY area_code
-- ORDER BY area_code;

-- =============================================================================
-- Fix: Với mỗi area_code bẩn, cần xác định tỉnh đúng và update thủ công.
-- Dựa vào screenshot: area_code = '09' nhưng address = 'Tinh Vinh Long'/'Tinh Ben Tre'
--   → Vĩnh Long + Bến Tre thuộc tỉnh mới '24' (Vĩnh Long)
-- =============================================================================

-- Uncomment sau khi xác nhận kết quả query kiểm tra:

-- UPDATE nhahocduong_organization
-- SET area_code = '24'  -- Vinh Long moi
-- WHERE area_code = '09'
--   AND address LIKE '%Vinh Long%';

-- UPDATE nhahocduong_organization
-- SET area_code = '24'  -- Vinh Long moi (Ben Tre da sap nhap vao)
-- WHERE area_code = '09'
--   AND address LIKE '%Ben Tre%';

-- =============================================================================
-- Sau khi fix, verify bang:
--   SELECT DISTINCT o.area_code, ca.name
--   FROM nhahocduong_organization o
--   LEFT JOIN common_area ca ON ca.code = o.area_code AND ca.active = 1 AND ca.type = 1
--   WHERE ca.id IS NULL;
--   → Ket qua rong = tat ca area_code da hop le
-- =============================================================================

COMMIT;
