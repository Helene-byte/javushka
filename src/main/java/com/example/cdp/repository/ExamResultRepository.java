package com.example.cdp.repository;

import com.example.cdp.model.ExamResult;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long>, JpaSpecificationExecutor<ExamResult> {

    default List<ExamResult> findBySurnamePartial(String surnamePart) {
        return findAll((root, query, cb) -> {
            if (query.getResultType() != Long.class) {
                root.fetch("student");
                root.fetch("subject");
            }
            return ((HibernateCriteriaBuilder) cb).ilike(
                    root.get("student").get("surname"), "%" + surnamePart + "%");
        });
    }

    @Query(nativeQuery = true, value = "SELECT fn_average_mark_for_student(:name, :surname)")
    BigDecimal averageMarkForStudent(@Param("name") String name, @Param("surname") String surname);

    @Query(nativeQuery = true, value = "SELECT fn_average_mark_for_subject(:subjectName)")
    BigDecimal averageMarkForSubject(@Param("subjectName") String subjectName);

    @Query(nativeQuery = true, value = "SELECT student_id, name, surname, low_mark_count FROM fn_students_at_red_zone() ORDER BY surname, name")
    List<Object[]> findRedZoneStudents();

    @Query(nativeQuery = true, value = """
            SELECT student_name, student_surname, subject_name, mark
            FROM student_exam_snapshot
            ORDER BY student_surname, student_name, subject_name
            """)
    List<Object[]> readSnapshot();

    @Modifying
    @Query(nativeQuery = true, value = "REFRESH MATERIALIZED VIEW student_exam_snapshot")
    void refreshSnapshot();
}
