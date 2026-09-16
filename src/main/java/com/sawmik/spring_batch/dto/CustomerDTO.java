package com.sawmik.spring_batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String company;
    private String city;
    private String country;
    private String phone;
    private String email;
    private LocalDate subscriptionDate;
    private String website;
}
