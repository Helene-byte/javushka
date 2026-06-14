package com.example.cdp.service;

import com.example.cdp.model.*;
import com.example.cdp.repository.*;
import com.example.cdp.dto.AddressAuditDto;
import com.example.cdp.dto.AddressDto;
import com.example.cdp.dto.RedZoneStudentDto;
import com.example.cdp.dto.SnapshotDto;
import com.example.cdp.dto.StudentDto;
import com.example.cdp.dto.StudentMarkDto;
import com.example.cdp.dto.StudentPhoneDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CdpService {

    private final StudentRepository studentRepo;
    private final StudentPhoneRepository phoneRepo;
    private final ExamResultRepository examResultRepo;
    private final StudentAddressRepository addressRepo;
    private final StudentAddressUpdateLogRepository auditLogRepo;

    public long insertStudent(String name, String surname, LocalDate dateOfBirth, String primarySkill) {
        return studentRepo.save(new Student(name, surname, dateOfBirth, primarySkill)).getId();
    }

    public int updateStudentPrimarySkill(long studentId, String primarySkill) {
        return studentRepo.updatePrimarySkill(studentId, primarySkill);
    }

    public int updateStudentAddress(long addressId, String addressLine1, String addressLine2,
                                    String city, String postalCode, String country) {
        return addressRepo.updateAddress(addressId, addressLine1, addressLine2, city, postalCode, country);
    }

    @Transactional(readOnly = true)
    public List<StudentDto> findStudentsByExactName(String name) {
        return studentRepo.findByName(name).stream()
                .map(StudentDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentDto> findStudentsBySurnamePartial(String surnamePart) {
        return studentRepo.findBySurnameIgnoreCaseContaining(surnamePart).stream()
                .map(StudentDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentPhoneDto> findStudentsByPhonePartial(String phonePart) {
        return phoneRepo.findByPhoneNumberContainingIgnoreCase(phonePart).stream()
                .map(sp -> new StudentPhoneDto(
                        sp.getStudent().getId(),
                        sp.getStudent().getName(),
                        sp.getStudent().getSurname(),
                        sp.getPhoneNumber(),
                        sp.isPrimaryPhone()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentMarkDto> findStudentMarksBySurnamePartial(String surnamePart) {
        return examResultRepo.findBySurnamePartial(surnamePart).stream()
                .map(er -> new StudentMarkDto(
                        er.getStudent().getId(),
                        er.getStudent().getName(),
                        er.getStudent().getSurname(),
                        er.getSubject().getSubjectName(),
                        er.getMark()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal averageMarkForStudent(String name, String surname) {
        return examResultRepo.averageMarkForStudent(name, surname);
    }

    @Transactional(readOnly = true)
    public BigDecimal averageMarkForSubject(String subjectName) {
        return examResultRepo.averageMarkForSubject(subjectName);
    }

    @Transactional(readOnly = true)
    public List<RedZoneStudentDto> findRedZoneStudents() {
        return examResultRepo.findRedZoneStudents().stream()
                .map(r -> new RedZoneStudentDto(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (String) r[2],
                        ((Number) r[3]).longValue()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SnapshotDto> readSnapshot() {
        return examResultRepo.readSnapshot().stream()
                .map(r -> new SnapshotDto(
                        (String) r[0],
                        (String) r[1],
                        (String) r[2],
                        ((Number) r[3]).intValue()
                ))
                .toList();
    }

    public void refreshSnapshot() {
        examResultRepo.refreshSnapshot();
    }

    @Transactional(readOnly = true)
    public List<AddressDto> findAllStudentAddresses() {
        return addressRepo.findAllByOrderByIdAsc().stream()
                .map(a -> new AddressDto(
                        a.getId(),
                        a.getStudent().getId(),
                        a.getAddressLine1(),
                        a.getAddressLine2(),
                        a.getCity(),
                        a.getPostalCode(),
                        a.getCountry(),
                        a.getCreatedDatetime(),
                        a.getUpdatedDatetime()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AddressAuditDto> findAllAddressUpdateLog() {
        return auditLogRepo.findAllByOrderByIdAsc().stream()
                .map(l -> new AddressAuditDto(
                        l.getId(),
                        l.getOriginalAddressId(),
                        l.getStudentId(),
                        l.getAddressLine1(),
                        l.getAddressLine2(),
                        l.getCity(),
                        l.getPostalCode(),
                        l.getCountry(),
                        l.getChangedAt(),
                        l.getOperation()
                ))
                .toList();
    }

}
