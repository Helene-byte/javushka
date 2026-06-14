--liquibase formatted sql

--changeset cdp:001-schema
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    primary_skill VARCHAR(150) NOT NULL,
    created_datetime TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_datetime TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_students_name_special_chars CHECK (name !~ '[@#$]')
);

CREATE TABLE student_phones (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    phone_number VARCHAR(40) NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_student_phone UNIQUE (student_id, phone_number)
);

CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    subject_name VARCHAR(150) NOT NULL UNIQUE,
    tutor VARCHAR(150) NOT NULL,
    created_datetime TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_datetime TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE exam_results (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    mark SMALLINT NOT NULL CHECK (mark BETWEEN 1 AND 5),
    exam_datetime TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_exam_student_subject UNIQUE (student_id, subject_id)
);

CREATE TABLE student_address (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_datetime TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_datetime TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student_address_update_log (
    id BIGSERIAL PRIMARY KEY,
    original_address_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operation VARCHAR(20) NOT NULL DEFAULT 'UPDATE'
);

CREATE INDEX idx_students_name ON students(name);
CREATE INDEX idx_students_surname_trgm ON students USING gin (surname gin_trgm_ops);
CREATE INDEX idx_student_phones_phone_trgm ON student_phones USING gin (phone_number gin_trgm_ops);
CREATE INDEX idx_exam_results_student_id ON exam_results(student_id);
CREATE INDEX idx_exam_results_subject_id ON exam_results(subject_id);
CREATE INDEX idx_exam_results_mark ON exam_results(mark);
CREATE INDEX idx_student_address_student_id ON student_address(student_id);

CREATE MATERIALIZED VIEW student_exam_snapshot AS
SELECT
    s.name AS student_name,
    s.surname AS student_surname,
    sub.subject_name,
    er.mark
FROM exam_results er
JOIN students s ON s.id = er.student_id
JOIN subjects sub ON sub.id = er.subject_id;

