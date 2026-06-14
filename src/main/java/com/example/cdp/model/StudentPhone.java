package com.example.cdp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_phones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "phone_number"})
})
@Getter
@Setter
@NoArgsConstructor
public class StudentPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "phone_number", nullable = false, length = 40)
    private String phoneNumber;

    @Column(name = "is_primary", nullable = false)
    private boolean primaryPhone;

}
