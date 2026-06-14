INSERT INTO students (name, surname, date_of_birth, primary_skill) VALUES
    ('Alice', 'Johnson', '2001-01-10', 'java'),
    ('Bob',   'Johnson', '2000-02-11', 'databases'),
    ('Maria', 'Johnson', '2002-03-12', 'analytics');

INSERT INTO student_phones (student_id, phone_number, is_primary) VALUES
    (1, '+48 600 111 222', true),
    (1, '+48 777 888 999', false),
    (2, '+48 600 123 456', true),
    (3, '+48 555 444 333', true);

INSERT INTO subjects (subject_name, tutor) VALUES
    ('Mathematics', 'Dr. Euler'),
    ('Databases',   'Dr. Codd'),
    ('Algorithms',  'Dr. Knuth');

INSERT INTO exam_results (student_id, subject_id, mark) VALUES
    (1, 1, 5),
    (1, 2, 4),
    (1, 3, 3),
    (2, 1, 2),
    (2, 2, 3),
    (3, 1, 4),
    (3, 2, 5);

INSERT INTO student_address (student_id, address_line1, address_line2, city, postal_code, country) VALUES
    (1, '1 Main Street', 'Flat 2', 'Warsaw', '00-001', 'Poland');
