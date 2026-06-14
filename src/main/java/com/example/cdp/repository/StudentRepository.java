package com.example.cdp.repository;

import com.example.cdp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE s.name = :name ORDER BY s.surname, s.name, s.id")
    List<Student> findByName(@Param("name") String name);

    @Query("SELECT s FROM Student s WHERE s.surname ILIKE CONCAT('%', :surnamePart, '%') ORDER BY s.surname, s.name, s.id")
    List<Student> findBySurnameIgnoreCaseContaining(@Param("surnamePart") String surnamePart);

    @Modifying
    @Query("UPDATE Student s SET s.primarySkill = :skill WHERE s.id = :id")
    int updatePrimarySkill(@Param("id") long id, @Param("skill") String skill);
}
