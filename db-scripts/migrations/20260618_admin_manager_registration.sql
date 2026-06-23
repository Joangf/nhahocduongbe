BEGIN;

-- Keep the existing role ID while giving the role a stable code for authorization.
UPDATE nhahocduong.user_role
SET code = 'MANAGER',
    name = 'Quản lý',
    description = 'Tài khoản quản lý đơn vị hoặc trường học',
    status = TRUE,
    updated_date = NOW()
WHERE code = 'QL';

INSERT INTO nhahocduong.user_role (
    code,
    name,
    status,
    description,
    created_date,
    updated_date
)
VALUES (
    'ADMIN',
    'Quản trị viên',
    TRUE,
    'Quản trị hệ thống và phê duyệt đơn đăng ký tài khoản quản lý',
    NOW(),
    NOW()
)
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name,
    status = TRUE,
    description = EXCLUDED.description,
    updated_date = NOW();

INSERT INTO nhahocduong.user_role (
    code,
    name,
    status,
    description,
    created_date,
    updated_date
)
VALUES (
    'MANAGER',
    'Quản lý',
    TRUE,
    'Tài khoản quản lý đơn vị hoặc trường học',
    NOW(),
    NOW()
)
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name,
    status = TRUE,
    description = EXCLUDED.description,
    updated_date = NOW();

ALTER TABLE nhahocduong.user_role_mapping
    ALTER COLUMN user_id SET NOT NULL,
    ALTER COLUMN role_id SET NOT NULL;

DELETE FROM nhahocduong.user_role_mapping a
USING nhahocduong.user_role_mapping b
WHERE a.ctid < b.ctid
  AND a.user_id = b.user_id
  AND a.role_id = b.role_id;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'nhahocduong.user_role_mapping'::regclass
          AND contype = 'p'
    ) THEN
        ALTER TABLE nhahocduong.user_role_mapping
            ADD CONSTRAINT user_role_mapping_pkey PRIMARY KEY (user_id, role_id);
    END IF;
END $$;

INSERT INTO nhahocduong.user_role_mapping (user_id, role_id)
SELECT u.id, r.id
FROM nhahocduong.user_user u
JOIN nhahocduong.user_role r ON r.code = 'ADMIN'
WHERE LOWER(u.username) = 'admin'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO nhahocduong.user_role_mapping (user_id, role_id)
SELECT u.id, r.id
FROM nhahocduong.user_user u
JOIN nhahocduong.user_role r ON r.code = 'MANAGER'
WHERE LOWER(u.username) <> 'admin'
ON CONFLICT (user_id, role_id) DO NOTHING;

CREATE TABLE IF NOT EXISTS nhahocduong.user_registration_request (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    phone_number VARCHAR,
    birthdate DATE,
    organization_id BIGINT REFERENCES nhahocduong.nhahocduong_organization(id),
    requested_role_code VARCHAR NOT NULL DEFAULT 'MANAGER'
        REFERENCES nhahocduong.user_role(code),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    reviewed_at TIMESTAMP,
    reviewed_by BIGINT REFERENCES nhahocduong.user_user(id),
    review_note TEXT,
    approved_user_id BIGINT REFERENCES nhahocduong.user_user(id),
    CONSTRAINT user_registration_request_status_check
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT user_registration_request_manager_role_check
        CHECK (requested_role_code = 'MANAGER'),
    CONSTRAINT user_registration_request_review_check
        CHECK (
            (status = 'PENDING' AND reviewed_at IS NULL AND reviewed_by IS NULL)
            OR status = 'CANCELLED'
            OR (status IN ('APPROVED', 'REJECTED') AND reviewed_at IS NOT NULL AND reviewed_by IS NOT NULL)
        ),
    CONSTRAINT user_registration_request_approved_user_check
        CHECK (
            (status = 'APPROVED' AND approved_user_id IS NOT NULL)
            OR (status <> 'APPROVED' AND approved_user_id IS NULL)
        )
);

CREATE UNIQUE INDEX IF NOT EXISTS user_registration_request_pending_username_key
    ON nhahocduong.user_registration_request (LOWER(username))
    WHERE status = 'PENDING';

CREATE UNIQUE INDEX IF NOT EXISTS user_registration_request_pending_email_key
    ON nhahocduong.user_registration_request (LOWER(email))
    WHERE status = 'PENDING';

CREATE UNIQUE INDEX IF NOT EXISTS user_registration_request_pending_phone_key
    ON nhahocduong.user_registration_request (phone_number)
    WHERE status = 'PENDING' AND phone_number IS NOT NULL;

CREATE INDEX IF NOT EXISTS user_registration_request_status_submitted_idx
    ON nhahocduong.user_registration_request (status, submitted_at DESC);

CREATE OR REPLACE FUNCTION nhahocduong.validate_registration_request()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF EXISTS (
            SELECT 1
            FROM nhahocduong.user_user u
            WHERE LOWER(u.username) = LOWER(NEW.username)
               OR LOWER(u.email) = LOWER(NEW.email)
               OR (
                    NEW.phone_number IS NOT NULL
                    AND u.phone_number = NEW.phone_number
               )
        ) THEN
            RAISE EXCEPTION 'Username, email, or phone number already exists';
        END IF;

        RETURN NEW;
    END IF;

    IF OLD.status IN ('APPROVED', 'REJECTED', 'CANCELLED')
       AND NEW.status <> OLD.status THEN
        RAISE EXCEPTION 'A completed registration request cannot change status';
    END IF;

    IF NEW.status IN ('APPROVED', 'REJECTED')
       AND NEW.status <> OLD.status THEN
        IF NEW.reviewed_by IS NULL OR NOT EXISTS (
            SELECT 1
            FROM nhahocduong.user_role_mapping urm
            JOIN nhahocduong.user_role r ON r.id = urm.role_id
            WHERE urm.user_id = NEW.reviewed_by
              AND r.code = 'ADMIN'
              AND r.status = TRUE
        ) THEN
            RAISE EXCEPTION 'Only an active ADMIN can review registration requests';
        END IF;

        NEW.reviewed_at := COALESCE(NEW.reviewed_at, NOW());
    END IF;

    RETURN NEW;
END $$;

DROP TRIGGER IF EXISTS user_registration_request_validation
    ON nhahocduong.user_registration_request;

CREATE TRIGGER user_registration_request_validation
BEFORE INSERT OR UPDATE
ON nhahocduong.user_registration_request
FOR EACH ROW
EXECUTE FUNCTION nhahocduong.validate_registration_request();

COMMIT;
