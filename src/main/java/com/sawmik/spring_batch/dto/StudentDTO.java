package com.sawmik.spring_batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    private Long id;
    private String name;
    private String email;
    private String course;
    private String grade;
    private BigDecimal gpa;
    private LocalDate enrollmentDate;
    private String department;
    private Integer year;
}
