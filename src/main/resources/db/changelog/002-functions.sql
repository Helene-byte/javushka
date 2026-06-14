--liquibase formatted sql

--changeset cdp:002-functions-student-average splitStatements:false
CREATE OR REPLACE FUNCTION fn_average_mark_for_student(p_name TEXT, p_surname TEXT)
RETURNS NUMERIC(5,2)
LANGUAGE SQL
STABLE
AS $$
    SELECT COALESCE(ROUND(AVG(er.mark)::NUMERIC, 2), 0::NUMERIC)
    FROM students s
    JOIN exam_results er ON er.student_id = s.id
    WHERE s.name = p_name
      AND s.surname = p_surname;
$$;

--changeset cdp:002-functions-subject-average splitStatements:false
CREATE OR REPLACE FUNCTION fn_average_mark_for_subject(p_subject_name TEXT)
RETURNS NUMERIC(5,2)
LANGUAGE SQL
STABLE
AS $$
    SELECT COALESCE(ROUND(AVG(er.mark)::NUMERIC, 2), 0::NUMERIC)
    FROM exam_results er
    JOIN subjects sub ON sub.id = er.subject_id
    WHERE sub.subject_name = p_subject_name;
$$;

--changeset cdp:002-functions-red-zone splitStatements:false
CREATE OR REPLACE FUNCTION fn_students_at_red_zone()
RETURNS TABLE (
    student_id BIGINT,
    name VARCHAR,
    surname VARCHAR,
    low_mark_count BIGINT
)
LANGUAGE SQL
STABLE
AS $$
    SELECT
        s.id AS student_id,
        s.name,
        s.surname,
        COUNT(*) FILTER (WHERE er.mark <= 3) AS low_mark_count
    FROM students s
    JOIN exam_results er ON er.student_id = s.id
    GROUP BY s.id, s.name, s.surname
    HAVING COUNT(*) FILTER (WHERE er.mark <= 3) >= 2;
$$;

