-- =============================================================
-- Migration: Refresh token support for authentication
-- Date: 2026-07-22
-- =============================================================
BEGIN;

DROP TABLE IF EXISTS nhahocduong.auth_refresh_token CASCADE;

CREATE TABLE nhahocduong.auth_refresh_token (
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(255) NOT NULL UNIQUE,
    user_id     BIGINT NOT NULL REFERENCES nhahocduong.user_user(id) ON DELETE CASCADE,
    expires_at  TIMESTAMP NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_auth_refresh_token_user_id
    ON nhahocduong.auth_refresh_token(user_id);

CREATE INDEX idx_auth_refresh_token_token
    ON nhahocduong.auth_refresh_token(token);

COMMIT;