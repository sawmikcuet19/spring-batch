package com.sawmik.spring_batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDTO {
    private Long id;
    private String city;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal windSpeed;
    private BigDecimal pressure;
    private LocalDateTime recordedAt;
    private String condition;
}
