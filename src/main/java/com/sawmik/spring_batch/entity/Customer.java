package com.sawmik.spring_batch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String company;

    private String city;

    private String country;

    private String phone;

    @Column(nullable = false)
    private String email;

    private LocalDate subscriptionDate;

    private String website;
}
