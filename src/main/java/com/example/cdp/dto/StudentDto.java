package com.example.cdp.dto;

import com.example.cdp.model.Student;

import java.time.Instant;
import java.time.LocalDate;

public record StudentDto(
        Long id,
        String name,
        String surname,
        LocalDate dateOfBirth,
        String primarySkill,
        Instant createdDatetime,
        Instant updatedDatetime
) {
    public static StudentDto from(Student s) {
        return new StudentDto(s.getId(), s.getName(), s.getSurname(),
                s.getDateOfBirth(), s.getPrimarySkill(),
                s.getCreatedDatetime(), s.getUpdatedDatetime());
    }
}
