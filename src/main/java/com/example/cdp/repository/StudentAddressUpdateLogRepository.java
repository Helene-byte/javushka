package com.example.cdp.repository;

import com.example.cdp.model.StudentAddressUpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAddressUpdateLogRepository extends JpaRepository<StudentAddressUpdateLog, Long> {

    List<StudentAddressUpdateLog> findAllByOrderByIdAsc();
}
