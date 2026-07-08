-- =============================================================
-- Migration: Week 4 - Academic Year Management System
-- Description:
--   1. Quản lý năm học (AcademicYear)
--   2. Tách Class thành entity riêng
--   3. Lịch sử học tập (Student_Class_Affiliation)
--   4. System log cho rollback
--   5. FK academic_year_id cho Exam và ExamCampaign
-- Date: 2026-07-07
-- =============================================================
BEGIN;

-- =============================================================
-- 1. TẠO BẢNG ACADEMIC_YEAR — Trục thời gian cho toàn hệ thống
-- =============================================================
DROP TABLE IF EXISTS nhahocduong.academic_year CASCADE;
CREATE TABLE nhahocduong.academic_year (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR NOT NULL,                        -- VD: "2025-2026"
    start_date      DATE,                                   -- Ngày bắt đầu dự kiến
    end_date        DATE,                                   -- Ngày kết thúc dự kiến
    status          VARCHAR NOT NULL DEFAULT 'UPCOMING',    -- UPCOMING | CURRENT | COMPLETED
    created_date    TIMESTAMP DEFAULT NOW(),
    updated_date    TIMESTAMP,
    created_by      VARCHAR,
    updated_by      VARCHAR,

    CONSTRAINT chk_academic_year_status
        CHECK (status IN ('UPCOMING', 'CURRENT', 'COMPLETED'))
);

-- Ràng buộc: chỉ DUY NHẤT 1 năm học CURRENT tại mọi thời điểm
CREATE UNIQUE INDEX only_one_current_academic_year
    ON nhahocduong.academic_year (status)
    WHERE status = 'CURRENT';

-- Sequence
ALTER SEQUENCE nhahocduong.academic_year_id_seq RESTART 100;


-- =============================================================
-- 2. TẠO BẢNG CLASS — Lớp học chính thức (thay vì varchar thô)
-- =============================================================
DROP TABLE IF EXISTS nhahocduong.class CASCADE;
CREATE TABLE nhahocduong.class (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR NOT NULL,                               -- VD: "1A", "2A3"
    grade               VARCHAR NOT NULL,                               -- Khối: "1" → "12"
    room                VARCHAR NOT NULL,                               -- Phòng: "A", "B", "A1"
    school_id           BIGINT NOT NULL REFERENCES nhahocduong.nhahocduong_organization(id),
    academic_year_id    BIGINT NOT NULL REFERENCES nhahocduong.academic_year(id),
    status              BOOLEAN NOT NULL DEFAULT TRUE,
    created_date        TIMESTAMP DEFAULT NOW(),
    updated_date        TIMESTAMP,
    created_by          VARCHAR,
    updated_by          VARCHAR,

    -- Mỗi lớp là duy nhất trong 1 trường, 1 năm học
    CONSTRAINT uq_class_name_school_year
        UNIQUE (name, school_id, academic_year_id),

    CONSTRAINT chk_class_grade
        CHECK (grade IN ('1','2','3','4','5','6','7','8','9','10','11','12'))
);

ALTER SEQUENCE nhahocduong.class_id_seq RESTART 100;


-- =============================================================
-- 3. TẠO BẢNG STUDENT_CLASS_AFFILIATION — Lịch sử học tập
--    (KHÔNG update trực tiếp Patient.school_class nữa)
-- =============================================================
DROP TABLE IF EXISTS nhahocduong.student_class_affiliation CASCADE;
CREATE TABLE nhahocduong.student_class_affiliation (
    id                  BIGSERIAL PRIMARY KEY,
    student_id          BIGINT NOT NULL REFERENCES nhahocduong.nhahocduong_patient(id),
    class_id            BIGINT NOT NULL REFERENCES nhahocduong.class(id),
    academic_year_id    BIGINT NOT NULL REFERENCES nhahocduong.academic_year(id),
    status              VARCHAR NOT NULL DEFAULT 'STUDYING',      -- STUDYING | GRADUATED | TRANSFERRED | DROPPED_OUT
    created_date        TIMESTAMP DEFAULT NOW(),
    updated_date        TIMESTAMP,
    created_by          VARCHAR,
    updated_by          VARCHAR,

    -- Mỗi học sinh chỉ có 1 affiliation trong 1 năm học
    CONSTRAINT uq_student_academic_year
        UNIQUE (student_id, academic_year_id),

    CONSTRAINT chk_affiliation_status
        CHECK (status IN ('STUDYING', 'GRADUATED', 'TRANSFERRED', 'DROPPED_OUT'))
);

-- Index cho các query thường dùng
CREATE INDEX idx_affiliation_class ON nhahocduong.student_class_affiliation(class_id);
CREATE INDEX idx_affiliation_year ON nhahocduong.student_class_affiliation(academic_year_id);
CREATE INDEX idx_affiliation_status ON nhahocduong.student_class_affiliation(status);

ALTER SEQUENCE nhahocduong.student_class_affiliation_id_seq RESTART 100;


-- =============================================================
-- 4. TẠO BẢNG SYSTEM_LOG — Audit trail cho Rollback/Undo
-- =============================================================
DROP TABLE IF EXISTS nhahocduong.system_log CASCADE;
CREATE TABLE nhahocduong.system_log (
    id              BIGSERIAL PRIMARY KEY,
    session_id      VARCHAR NOT NULL,            -- UUID định danh phiên (VD: chuyển năm học)
    action          VARCHAR NOT NULL,            -- YEAR_TRANSITION | ROLLBACK | etc.
    entity_type     VARCHAR NOT NULL,            -- ACADEMIC_YEAR | STUDENT_AFFILIATION | CLASS
    entity_id       BIGINT NOT NULL,             -- ID của bản ghi bị ảnh hưởng
    old_value       JSONB,                       -- Giá trị trước thay đổi
    new_value       JSONB,                       -- Giá trị sau thay đổi
    created_date    TIMESTAMP DEFAULT NOW(),
    created_by      VARCHAR,

    CONSTRAINT chk_system_log_action
        CHECK (action IN ('YEAR_TRANSITION', 'ROLLBACK', 'CREATE', 'UPDATE', 'DELETE'))
);

-- Index để tra nhanh theo session_id (cho rollback)
CREATE INDEX idx_system_log_session ON nhahocduong.system_log(session_id);
CREATE INDEX idx_system_log_entity ON nhahocduong.system_log(entity_type, entity_id);

ALTER SEQUENCE nhahocduong.system_log_id_seq RESTART 100;


-- =============================================================
-- 5. SỬA BẢNG HIỆN TẠI: Thêm FK academic_year_id
-- =============================================================

-- 5a. Thêm academic_year_id vào Exam
ALTER TABLE nhahocduong.nhahocduong_exam
    ADD COLUMN IF NOT EXISTS academic_year_id BIGINT
        REFERENCES nhahocduong.academic_year(id);

-- Index cho việc lọc exam theo năm học
CREATE INDEX IF NOT EXISTS idx_exam_academic_year
    ON nhahocduong.nhahocduong_exam(academic_year_id);

-- 5b. Thêm academic_year_id vào ExamCampaign
ALTER TABLE nhahocduong.nhahocduong_exam_campaign
    ADD COLUMN IF NOT EXISTS academic_year_id BIGINT
        REFERENCES nhahocduong.academic_year(id);

CREATE INDEX IF NOT EXISTS idx_campaign_academic_year
    ON nhahocduong.nhahocduong_exam_campaign(academic_year_id);


-- =============================================================
-- 6. SEED DATA: Tạo năm học mặc định CURRENT
-- =============================================================
INSERT INTO nhahocduong.academic_year (name, start_date, end_date, status, created_by)
VALUES ('2025-2026', '2025-09-01', '2026-05-31', 'CURRENT', 'migration')
ON CONFLICT DO NOTHING;


-- =============================================================
-- 7. DATA MIGRATION: Organization.classes (JSONB) → bảng class
--    (chỉ chạy cho năm học CURRENT)
-- =============================================================
DO $$
DECLARE
    org_record     RECORD;
    grade_key      TEXT;
    room_value     TEXT;
    v_academic_id  BIGINT;
    v_class_name   TEXT;
    v_class_id     BIGINT;
BEGIN
    -- Lấy academic_year_id hiện tại
    SELECT id INTO v_academic_id
    FROM nhahocduong.academic_year
    WHERE status = 'CURRENT'
    LIMIT 1;

    IF v_academic_id IS NULL THEN
        RAISE NOTICE 'Không tìm thấy năm học CURRENT. Bỏ qua data migration.';
        RETURN;
    END IF;

    -- Duyệt từng trường có classes JSONB
    FOR org_record IN
        SELECT id AS school_id, classes
        FROM nhahocduong.nhahocduong_organization
        WHERE classes IS NOT NULL
          AND type = 1  -- chỉ xử lý trường học (type=1)
    LOOP
        -- Duyệt từng grade trong JSONB: {"1": ["A","B"], "2": ["A","C"]}
        FOR grade_key IN
            SELECT jsonb_object_keys(org_record.classes)
        LOOP
            -- Duyệt từng room trong mảng của grade
            FOR room_value IN
                SELECT jsonb_array_elements_text(org_record.classes -> grade_key)
            LOOP
                v_class_name := grade_key || room_value;  -- VD: "1A", "2A3"

                -- Chỉ insert nếu chưa tồn tại
                INSERT INTO nhahocduong.class (name, grade, room, school_id, academic_year_id, created_by)
                VALUES (v_class_name, grade_key, room_value, org_record.school_id, v_academic_id, 'migration')
                ON CONFLICT (name, school_id, academic_year_id) DO NOTHING;
            END LOOP;
        END LOOP;
    END LOOP;

    RAISE NOTICE 'Migration Organization.classes → class: COMPLETED';
END $$;


-- =============================================================
-- 8. DATA MIGRATION: Patient.school_class → student_class_affiliation
--    (chỉ migrate học sinh đang có school_class)
-- =============================================================
DO $$
DECLARE
    patient_rec    RECORD;
    v_academic_id  BIGINT;
    v_class_id     BIGINT;
BEGIN
    SELECT id INTO v_academic_id
    FROM nhahocduong.academic_year
    WHERE status = 'CURRENT'
    LIMIT 1;

    IF v_academic_id IS NULL THEN
        RAISE NOTICE 'Không tìm thấy năm học CURRENT. Bỏ qua migrate patient.';
        RETURN;
    END IF;

    -- Duyệt từng học sinh có school_class và organization
    FOR patient_rec IN
        SELECT p.id AS patient_id, p.school_class, p.organization, p.status
        FROM nhahocduong.nhahocduong_patient p
        WHERE p.school_class IS NOT NULL
          AND p.school_class != ''
          AND p.organization IS NOT NULL
    LOOP
        -- Tìm class tương ứng
        SELECT c.id INTO v_class_id
        FROM nhahocduong.class c
        WHERE c.name = patient_rec.school_class
          AND c.school_id = patient_rec.organization
          AND c.academic_year_id = v_academic_id
        LIMIT 1;

        -- Nếu tìm thấy class, tạo affiliation
        IF v_class_id IS NOT NULL THEN
            INSERT INTO nhahocduong.student_class_affiliation
                (student_id, class_id, academic_year_id, status, created_by)
            VALUES (
                patient_rec.patient_id,
                v_class_id,
                v_academic_id,
                CASE WHEN patient_rec.status = TRUE THEN 'STUDYING' ELSE 'DROPPED_OUT' END,
                'migration'
            )
            ON CONFLICT (student_id, academic_year_id) DO NOTHING;
        END IF;
    END LOOP;

    RAISE NOTICE 'Migration Patient → student_class_affiliation: COMPLETED';
END $$;


-- =============================================================
-- 9. DATA MIGRATION: Exam.year (varchar) → Exam.academic_year_id
-- =============================================================
DO $$
DECLARE
    exam_years     RECORD;
    v_academic_id  BIGINT;
BEGIN
    -- Duyệt các năm duy nhất từ Exam.year
    FOR exam_years IN
        SELECT DISTINCT year
        FROM nhahocduong.nhahocduong_exam
        WHERE year IS NOT NULL
          AND academic_year_id IS NULL
    LOOP
        -- Tìm academic_year có name chứa năm đó
        -- VD: Exam.year = "2023" → AcademicYear.name LIKE "%2023%"
        SELECT id INTO v_academic_id
        FROM nhahocduong.academic_year
        WHERE name LIKE '%' || exam_years.year || '%'
        LIMIT 1;

        -- Nếu không tìm thấy, tạo academic_year mới với status COMPLETED
        IF v_academic_id IS NULL THEN
            INSERT INTO nhahocduong.academic_year (name, status, created_by)
            VALUES (
                exam_years.year || '-' || (exam_years.year::INT + 1),
                'COMPLETED',
                'migration-from-exam'
            )
            RETURNING id INTO v_academic_id;
        END IF;

        -- Update các exam thuộc năm này
        UPDATE nhahocduong.nhahocduong_exam
        SET academic_year_id = v_academic_id
        WHERE year = exam_years.year
          AND academic_year_id IS NULL;
    END LOOP;

    RAISE NOTICE 'Migration Exam.year → Exam.academic_year_id: COMPLETED';
END $$;


COMMIT;
