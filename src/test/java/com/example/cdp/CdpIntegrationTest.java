package com.example.cdp;

import com.example.cdp.service.CdpService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.example.cdp.dto.AddressAuditDto;
import com.example.cdp.dto.AddressDto;
import com.example.cdp.dto.RedZoneStudentDto;
import com.example.cdp.dto.SnapshotDto;
import com.example.cdp.dto.StudentDto;
import com.example.cdp.dto.StudentMarkDto;
import com.example.cdp.dto.StudentPhoneDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest(classes = CdpApplication.class)
class CdpIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("cdp")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    CdpService service;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PlatformTransactionManager transactionManager;

    @PersistenceContext
    EntityManager entityManager;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("""
                TRUNCATE TABLE
                    student_address_update_log,
                    student_address,
                    exam_results,
                    student_phones,
                    subjects,
                    students
                RESTART IDENTITY CASCADE
                """);
        service.refreshSnapshot();
    }

    @Test
    @Sql("/test-data/main-scenario.sql")
    void shouldSupportSearchesFunctionsSnapshotAndTriggers() {
        service.refreshSnapshot();

        List<StudentDto> exactName = service.findStudentsByExactName("Alice");
        assertThat(exactName).hasSize(1);
        assertThat(exactName.getFirst().surname()).isEqualTo("Johnson");

        List<StudentDto> partialSurname = service.findStudentsBySurnamePartial("son");
        assertThat(partialSurname).extracting(StudentDto::name).containsExactlyInAnyOrder("Alice", "Bob", "Maria");

        List<StudentPhoneDto> phoneMatches = service.findStudentsByPhonePartial("600");
        assertThat(phoneMatches).extracting(StudentPhoneDto::name).containsExactlyInAnyOrder("Alice", "Bob");

        List<StudentMarkDto> marksBySurname = service.findStudentMarksBySurnamePartial("son");
        assertThat(marksBySurname).extracting(StudentMarkDto::subjectName).contains("Mathematics", "Databases", "Algorithms");

        assertThat(service.averageMarkForStudent("Alice", "Johnson")).isEqualByComparingTo("4.00");
        assertThat(service.averageMarkForSubject("Databases")).isEqualByComparingTo("4.00");

        List<RedZoneStudentDto> redZoneStudents = service.findRedZoneStudents();
        assertThat(redZoneStudents).hasSize(1);
        assertThat(redZoneStudents.getFirst().name()).isEqualTo("Bob");
        assertThat(redZoneStudents.getFirst().lowMarkCount()).isEqualTo(2L);

        List<SnapshotDto> snapshotBeforeUpdate = service.readSnapshot();
        assertThat(snapshotBeforeUpdate).hasSize(7);
        assertThat(snapshotBeforeUpdate)
                .extracting(SnapshotDto::studentName)
                .contains("Alice", "Bob", "Maria");

        Instant beforeUpdate = exactName.getFirst().updatedDatetime();
        int updatedRows = service.updateStudentPrimarySkill(1L, "spring boot");
        assertThat(updatedRows).isEqualTo(1);

        StudentDto updatedAlice = service.findStudentsByExactName("Alice").getFirst();
        assertThat(updatedAlice.primarySkill()).isEqualTo("spring boot");
        assertThat(updatedAlice.updatedDatetime()).isAfter(beforeUpdate);

        List<SnapshotDto> snapshotAfterUpdateWithoutRefresh = service.readSnapshot();
        assertThat(snapshotAfterUpdateWithoutRefresh).isEqualTo(snapshotBeforeUpdate);

        service.refreshSnapshot();
        List<SnapshotDto> refreshedSnapshot = service.readSnapshot();
        assertThat(refreshedSnapshot).hasSize(7);

        assertThatThrownBy(() -> service.insertStudent("A@lice", "Broken", java.time.LocalDate.of(2001, 1, 1), "java"))
                .isInstanceOf(Exception.class);

        List<AddressDto> addressesBefore = service.findAllStudentAddresses();
        assertThat(addressesBefore).hasSize(1);
        assertThat(addressesBefore.getFirst().city()).isEqualTo("Warsaw");

        int addressUpdateCount = service.updateStudentAddress(1L, "1 Main Street", "Flat 5", "Krakow", "30-001", "Poland");
        assertThat(addressUpdateCount).isZero();

        List<AddressDto> addressesAfter = service.findAllStudentAddresses();
        assertThat(addressesAfter).hasSize(1);
        assertThat(addressesAfter.getFirst().city()).isEqualTo("Warsaw");

        List<AddressAuditDto> addressLog = service.findAllAddressUpdateLog();
        assertThat(addressLog).hasSize(1);
        assertThat(addressLog.getFirst().city()).isEqualTo("Krakow");
        assertThat(addressLog.getFirst().addressLine2()).isEqualTo("Flat 5");
    }

    @Test
    @Sql("/test-data/isolation-scenario.sql")
    void shouldDemonstrateNonRepeatableReadUnderReadCommitted() throws Exception {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager, definition);

        CountDownLatch firstReadDone = new CountDownLatch(1);
        CountDownLatch updateCommitted = new CountDownLatch(1);
        AtomicReference<String> firstValue = new AtomicReference<>();
        AtomicReference<String> secondValue = new AtomicReference<>();

        try (var executor = Executors.newFixedThreadPool(2)) {
            var reader = executor.submit(() -> txTemplate.executeWithoutResult(status -> {
                firstValue.set(service.findStudentsByExactName("Ivy").getFirst().primarySkill());
                firstReadDone.countDown();
                try {
                    if (!updateCommitted.await(10, TimeUnit.SECONDS)) {
                        throw new AssertionError("Timed out waiting for writer to commit");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Clears the shared Hibernate Session of the reader transaction so the
                // second read re-queries the DB instead of returning the cached entity.
                entityManager.clear();
                secondValue.set(service.findStudentsByExactName("Ivy").getFirst().primarySkill());
            }));

            var writer = executor.submit(() -> {
                try {
                    if (!firstReadDone.await(10, TimeUnit.SECONDS)) {
                        throw new AssertionError("Timed out waiting for reader's first read");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                txTemplate.executeWithoutResult(status -> service.updateStudentPrimarySkill(1L, "spring"));
                updateCommitted.countDown();
            });

            reader.get();
            writer.get();
        }

        assertThat(firstValue.get()).isEqualTo("testing");
        assertThat(secondValue.get()).isEqualTo("spring");
    }
}
