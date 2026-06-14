package com.example.cdp.repository;

import com.example.cdp.model.StudentPhone;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentPhoneRepository extends JpaRepository<StudentPhone, Long>, JpaSpecificationExecutor<StudentPhone> {

    default List<StudentPhone> findByPhoneNumberContainingIgnoreCase(String phonePart) {
        return findAll((root, query, cb) -> {
            if (query.getResultType() != Long.class) {
                root.fetch("student");
            }
            return ((HibernateCriteriaBuilder) cb).ilike(root.get("phoneNumber"), "%" + phonePart + "%");
        });
    }
}
