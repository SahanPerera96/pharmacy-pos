-- V1__create_branches.sql
-- ============================================================
-- Branches: physical pharmacy locations
-- ============================================================

CREATE TABLE branches (
    id               BIGSERIAL       PRIMARY KEY,
    name             VARCHAR(100)    NOT NULL,
    code             VARCHAR(10)     NOT NULL UNIQUE,    -- e.g. BR001
    address          TEXT            NOT NULL,
    city             VARCHAR(50)     NOT NULL,
    phone            VARCHAR(20),
    email            VARCHAR(100),
    license_number   VARCHAR(50),
    is_headquarters  BOOLEAN         NOT NULL DEFAULT FALSE,
    is_active        BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100)
);

-- Seed the first (headquarters) branch
INSERT INTO branches (name, code, address, city, is_headquarters, is_active, created_by, updated_by)
VALUES ('Main Branch', 'BR001', '123 Main Street', 'Colombo', TRUE, TRUE, 'SYSTEM', 'SYSTEM');
