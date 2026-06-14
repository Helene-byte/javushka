package com.example.cdp.repository;

import com.example.cdp.model.StudentAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAddressRepository extends JpaRepository<StudentAddress, Long> {

    List<StudentAddress> findAllByOrderByIdAsc();

    // The DB trigger fn_student_address_immutable intercepts all UPDATEs on student_address,
    // writes to the audit log and returns NULL (cancelling the actual update).
    // Native query bypasses Hibernate's optimistic-lock row-count check (trigger returns 0 rows affected).
    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE student_address
            SET address_line1 = :addressLine1,
                address_line2 = :addressLine2,
                city          = :city,
                postal_code   = :postalCode,
                country       = :country,
                updated_datetime = CURRENT_TIMESTAMP
            WHERE id = :id
            """)
    int updateAddress(@Param("id") long id,
                      @Param("addressLine1") String addressLine1,
                      @Param("addressLine2") String addressLine2,
                      @Param("city") String city,
                      @Param("postalCode") String postalCode,
                      @Param("country") String country);
}
