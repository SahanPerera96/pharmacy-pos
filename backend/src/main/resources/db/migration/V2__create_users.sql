-- V2__create_users.sql
-- ============================================================
-- Users: all staff accounts across all branches
-- ============================================================

CREATE TABLE users (
    id                    BIGSERIAL       PRIMARY KEY,
    first_name            VARCHAR(50)     NOT NULL,
    last_name             VARCHAR(50)     NOT NULL,
    email                 VARCHAR(100)    NOT NULL UNIQUE,
    password_hash         VARCHAR(255)    NOT NULL,
    phone                 VARCHAR(20),
    role                  VARCHAR(20)     NOT NULL,   -- OWNER, ADMIN, MANAGER, etc.
    branch_id             BIGINT          REFERENCES branches(id),
    is_locked             BOOLEAN         NOT NULL DEFAULT FALSE,
    must_change_password  BOOLEAN         NOT NULL DEFAULT FALSE,
    is_active             BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by            VARCHAR(100),
    updated_by            VARCHAR(100)
);

CREATE INDEX idx_users_email     ON users(email);
CREATE INDEX idx_users_branch_id ON users(branch_id);
CREATE INDEX idx_users_role      ON users(role);

-- Seed an OWNER account
-- Password: Admin@1234  (BCrypt hash — CHANGE THIS in production!)
INSERT INTO users (
    first_name, last_name, email, password_hash, role,
    branch_id, is_active, must_change_password, created_by, updated_by
) VALUES (
    'System', 'Owner',
    'owner@pharmacy.com',
    '$2a$12$8xLPJuSC2mBZH0tSILfYlusDfvaxDMBwSSmKC44sHnb6InJ9.fZcq',
    'OWNER',
    NULL,       -- OWNER has no branch restriction
    TRUE,
    TRUE,       -- Force password change on first login
    'SYSTEM',
    'SYSTEM'
);
