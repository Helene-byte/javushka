--liquibase formatted sql

--changeset cdp:003-trigger-students-updated splitStatements:false
CREATE OR REPLACE FUNCTION fn_set_students_updated_datetime()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_datetime := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_students_updated_datetime
BEFORE UPDATE ON students
FOR EACH ROW
EXECUTE FUNCTION fn_set_students_updated_datetime();

--changeset cdp:003-trigger-student-address-immutable splitStatements:false
CREATE OR REPLACE FUNCTION fn_student_address_immutable()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO student_address_update_log (
        original_address_id,
        student_id,
        address_line1,
        address_line2,
        city,
        postal_code,
        country,
        changed_at,
        operation
    )
    VALUES (
        OLD.id,
        COALESCE(NEW.student_id, OLD.student_id),
        COALESCE(NEW.address_line1, OLD.address_line1),
        COALESCE(NEW.address_line2, OLD.address_line2),
        COALESCE(NEW.city, OLD.city),
        COALESCE(NEW.postal_code, OLD.postal_code),
        COALESCE(NEW.country, OLD.country),
        CURRENT_TIMESTAMP,
        'UPDATE'
    );

    RETURN NULL;
END;
$$;

CREATE TRIGGER trg_student_address_immutable
BEFORE UPDATE ON student_address
FOR EACH ROW
EXECUTE FUNCTION fn_student_address_immutable();

