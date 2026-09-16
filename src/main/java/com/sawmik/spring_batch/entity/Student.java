package com.sawmik.spring_batch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String course;

    private String grade;

    @Column(precision = 3, scale = 2)
    private BigDecimal gpa;

    private LocalDate enrollmentDate;

    private String department;

    private Integer year;
}
