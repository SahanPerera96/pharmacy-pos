-- V3__create_audit_tables.sql
-- ============================================================
-- Hibernate Envers audit tables
-- These are auto-queried by Envers — do not rename columns.
-- ============================================================

-- Revision metadata (one row per transaction that touches an @Audited entity)
CREATE TABLE revinfo (
    rev       BIGSERIAL   PRIMARY KEY,
    revtstmp  BIGINT      NOT NULL    -- epoch milliseconds
);

-- Audit log for branches
CREATE TABLE branches_audit_log (
    id               BIGINT,
    rev              BIGINT      NOT NULL REFERENCES revinfo(rev),
    revtype          SMALLINT    NOT NULL,   -- 0=INSERT 1=UPDATE 2=DELETE
    name             VARCHAR(100),
    code             VARCHAR(10),
    address          TEXT,
    city             VARCHAR(50),
    phone            VARCHAR(20),
    email            VARCHAR(100),
    is_headquarters  BOOLEAN,
    is_active        BOOLEAN,
    PRIMARY KEY (id, rev)
);

-- Audit log for users
CREATE TABLE users_audit_log (
    id                    BIGINT,
    rev                   BIGINT      NOT NULL REFERENCES revinfo(rev),
    revtype               SMALLINT    NOT NULL,
    first_name            VARCHAR(50),
    last_name             VARCHAR(50),
    email                 VARCHAR(100),
    role                  VARCHAR(20),
    branch_id             BIGINT,
    is_locked             BOOLEAN,
    must_change_password  BOOLEAN,
    is_active             BOOLEAN,
    PRIMARY KEY (id, rev)
);
