-- =============================================================================
-- Migration: Update 63 tỉnh cũ → 34 tỉnh mới (Sáp nhập 2025)
-- File: db-scripts/migrations/20260713_update_provinces_34.sql
-- =============================================================================

SET search_path TO nhahocduong, public;

BEGIN;

-- =============================================================================
-- BƯỚC 1: INSERT 34 tỉnh/thành mới (id bắt đầu từ 200 để không xung đột)
-- Code mới từ vn-geo (01-34), type=1 (tỉnh/tp TW), active=1
-- =============================================================================

INSERT INTO common_area (id, code, name, type, active, parent_area_id, ancestor) VALUES
  (200, '01', 'Thành phố Hà Nội',        1, 1, null, null),
  (201, '02', 'Tỉnh Hưng Yên',           1, 1, null, null),
  (202, '03', 'Tỉnh Quảng Trị',          1, 1, null, null),
  (203, '04', 'Thành phố Huế',           1, 1, null, null),
  (204, '05', 'Thành phố Hải Phòng',     1, 1, null, null),
  (205, '06', 'Tỉnh Phú Thọ',            1, 1, null, null),
  (206, '07', 'Tỉnh Thanh Hoá',          1, 1, null, null),
  (207, '08', 'Tỉnh Quảng Ninh',         1, 1, null, null),
  (208, '09', 'Tỉnh Lào Cai',            1, 1, null, null),
  (209, '10', 'Tỉnh Bắc Ninh',           1, 1, null, null),
  (210, '11', 'Tỉnh Nghệ An',            1, 1, null, null),
  (211, '12', 'Thành phố Đà Nẵng',       1, 1, null, null),
  (212, '13', 'Tỉnh Ninh Bình',          1, 1, null, null),
  (213, '14', 'Tỉnh Khánh Hoà',          1, 1, null, null),
  (214, '15', 'Tỉnh Tây Ninh',           1, 1, null, null),
  (215, '16', 'Tỉnh Đồng Tháp',          1, 1, null, null),
  (216, '17', 'Tỉnh Hà Tĩnh',            1, 1, null, null),
  (217, '18', 'Tỉnh An Giang',           1, 1, null, null),
  (218, '19', 'Tỉnh Thái Nguyên',        1, 1, null, null),
  (219, '20', 'Tỉnh Lạng Sơn',           1, 1, null, null),
  (220, '21', 'Tỉnh Điện Biên',          1, 1, null, null),
  (221, '22', 'Tỉnh Đồng Nai',           1, 1, null, null),
  (222, '23', 'Tỉnh Quảng Ngãi',         1, 1, null, null),
  (223, '24', 'Tỉnh Vĩnh Long',          1, 1, null, null),
  (224, '25', 'Tỉnh Cao Bằng',           1, 1, null, null),
  (225, '26', 'Tỉnh Lai Châu',           1, 1, null, null),
  (226, '27', 'Tỉnh Đắk Lắk',           1, 1, null, null),
  (227, '28', 'Tỉnh Gia Lai',            1, 1, null, null),
  (228, '29', 'Tỉnh Lâm Đồng',           1, 1, null, null),
  (229, '30', 'Thành phố Hồ Chí Minh',   1, 1, null, null),
  (230, '31', 'Tỉnh Sơn La',             1, 1, null, null),
  (231, '32', 'Thành phố Cần Thơ',       1, 1, null, null),
  (232, '33', 'Tỉnh Cà Mau',             1, 1, null, null),
  (233, '34', 'Tỉnh Tuyên Quang',        1, 1, null, null)
;

-- =============================================================================
-- BƯỚC 2: Đánh dấu 63 tỉnh cũ là inactive (active=0)
-- Chỉ update id < 200 để không đụng tỉnh mới vừa insert
-- =============================================================================

UPDATE common_area
SET active = 0
WHERE type = 1
  AND id < 200;

-- =============================================================================
-- BƯỚC 3: Update area_code trong nhahocduong_organization
-- Mapping: code cũ → code mới
-- QUAN TRỌNG: Thứ tự quan trọng vì một số code cũ = code mới của tỉnh khác
-- =============================================================================

-- --- Nhóm Đông Nam Bộ & HCM ---
-- Hồ Chí Minh (code mới '30'): HCM + Bình Dương + Bà Rịa-Vũng Tàu cũ
UPDATE nhahocduong_organization SET area_code = '30' WHERE area_code IN ('74','77','79');

-- Đồng Nai (code mới '22'): Đồng Nai + Bình Phước cũ
UPDATE nhahocduong_organization SET area_code = '22' WHERE area_code IN ('70','75');

-- Tây Ninh (code mới '15'): Tây Ninh + Long An cũ
UPDATE nhahocduong_organization SET area_code = '15' WHERE area_code IN ('72','80');

-- --- Nhóm Đồng bằng sông Cửu Long ---
-- Vĩnh Long (code mới '24'): Vĩnh Long + Bến Tre + Trà Vinh cũ
UPDATE nhahocduong_organization SET area_code = '24' WHERE area_code IN ('83','84','86');

-- Đồng Tháp (code mới '16'): Đồng Tháp + Tiền Giang cũ
UPDATE nhahocduong_organization SET area_code = '16' WHERE area_code IN ('82','87');

-- An Giang (code mới '18'): An Giang + Kiên Giang cũ
UPDATE nhahocduong_organization SET area_code = '18' WHERE area_code IN ('89','91');

-- Cần Thơ (code mới '32'): Cần Thơ + Hậu Giang + Sóc Trăng cũ
UPDATE nhahocduong_organization SET area_code = '32' WHERE area_code IN ('92','93','94');

-- Cà Mau (code mới '33'): Cà Mau + Bạc Liêu cũ
UPDATE nhahocduong_organization SET area_code = '33' WHERE area_code IN ('95','96');

-- --- Nhóm Tây Nguyên ---
-- Lâm Đồng (code mới '29'): Lâm Đồng + Đắk Nông + Bình Thuận cũ
UPDATE nhahocduong_organization SET area_code = '29' WHERE area_code IN ('60','67','68');

-- Đắk Lắk (code mới '27'): Đắk Lắk + Phú Yên cũ
UPDATE nhahocduong_organization SET area_code = '27' WHERE area_code IN ('54','66');

-- Gia Lai (code mới '28'): Gia Lai + Bình Định cũ
UPDATE nhahocduong_organization SET area_code = '28' WHERE area_code IN ('52','64');

-- Quảng Ngãi (code mới '23'): Quảng Ngãi + Kon Tum cũ
UPDATE nhahocduong_organization SET area_code = '23' WHERE area_code IN ('51','62');

-- Khánh Hoà (code mới '14'): Khánh Hoà + Ninh Thuận cũ
UPDATE nhahocduong_organization SET area_code = '14' WHERE area_code IN ('56','58');

-- --- Nhóm Miền Trung ---
-- Đà Nẵng (code mới '12'): Đà Nẵng + Quảng Nam cũ
UPDATE nhahocduong_organization SET area_code = '12' WHERE area_code IN ('48','49');

-- Huế (code mới '04'): Thừa Thiên Huế cũ
UPDATE nhahocduong_organization SET area_code = '04' WHERE area_code = '46';

-- Quảng Trị (code mới '03'): Quảng Trị + Quảng Bình cũ
UPDATE nhahocduong_organization SET area_code = '03' WHERE area_code IN ('44','45');

-- Hà Tĩnh (code mới '17')
UPDATE nhahocduong_organization SET area_code = '17' WHERE area_code = '42';

-- Nghệ An (code mới '11')
UPDATE nhahocduong_organization SET area_code = '11' WHERE area_code = '40';

-- Thanh Hoá (code mới '07')
UPDATE nhahocduong_organization SET area_code = '07' WHERE area_code = '38';

-- --- Nhóm Đồng bằng Bắc Bộ ---
-- Ninh Bình (code mới '13'): Ninh Bình + Hà Nam + Nam Định cũ
UPDATE nhahocduong_organization SET area_code = '13' WHERE area_code IN ('35','36','37');

-- Hưng Yên (code mới '02'): Hưng Yên + Thái Bình cũ
UPDATE nhahocduong_organization SET area_code = '02' WHERE area_code IN ('33','34');

-- Hải Phòng (code mới '05'): Hải Phòng + Hải Dương cũ
UPDATE nhahocduong_organization SET area_code = '05' WHERE area_code IN ('30','31');

-- Bắc Ninh (code mới '10'): Bắc Ninh + Bắc Giang cũ
UPDATE nhahocduong_organization SET area_code = '10' WHERE area_code IN ('24','27');

-- Quảng Ninh (code mới '08')
UPDATE nhahocduong_organization SET area_code = '08' WHERE area_code = '22';

-- Lạng Sơn (code mới '20'): code cũ '20' = code mới '20' → KHÔNG cần update

-- --- Nhóm Miền núi phía Bắc ---
-- Thái Nguyên (code mới '19'): Thái Nguyên + Bắc Kạn cũ
UPDATE nhahocduong_organization SET area_code = '19' WHERE area_code IN ('06','19');

-- Phú Thọ (code mới '06'): Phú Thọ + Vĩnh Phúc + Hoà Bình cũ
UPDATE nhahocduong_organization SET area_code = '06' WHERE area_code IN ('17','25','26');

-- Lào Cai (code mới '09'): Lào Cai + Yên Bái cũ
UPDATE nhahocduong_organization SET area_code = '09' WHERE area_code IN ('10','15');

-- Điện Biên (code mới '21')
UPDATE nhahocduong_organization SET area_code = '21' WHERE area_code = '11';

-- Lai Châu (code mới '26')
UPDATE nhahocduong_organization SET area_code = '26' WHERE area_code = '12';

-- Sơn La (code mới '31')
UPDATE nhahocduong_organization SET area_code = '31' WHERE area_code = '14';

-- Tuyên Quang (code mới '34'): Tuyên Quang + Hà Giang cũ
UPDATE nhahocduong_organization SET area_code = '34' WHERE area_code IN ('02','08');

-- Cao Bằng (code mới '25')
UPDATE nhahocduong_organization SET area_code = '25' WHERE area_code = '04';

-- Hà Nội (code mới '01'): code cũ '01' = code mới '01' → KHÔNG cần update

COMMIT;

-- =============================================================================
-- VERIFICATION QUERIES (chạy sau khi commit để kiểm tra)
-- =============================================================================
-- Kiểm tra số lượng tỉnh active/inactive:
-- SET search_path TO nhahocduong, public;
--   SELECT active, count(*) FROM common_area WHERE type=1 GROUP BY active;
--   → active=1: 34 | active=0: 63
--
-- Kiểm tra 34 tỉnh mới:
--   SELECT id, code, name FROM common_area WHERE type=1 AND active=1 ORDER BY code;
--
-- Kiểm tra area_code còn code cũ không (không được có kết quả):
--   SELECT DISTINCT area_code FROM nhahocduong_organization
--   WHERE area_code NOT IN (
--     '01','02','03','04','05','06','07','08','09','10',
--     '11','12','13','14','15','16','17','18','19','20',
--     '21','22','23','24','25','26','27','28','29','30',
--     '31','32','33','34'
--   );
