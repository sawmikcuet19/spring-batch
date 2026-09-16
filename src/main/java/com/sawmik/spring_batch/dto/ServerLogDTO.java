package com.sawmik.spring_batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerLogDTO {
    private Long id;
    private LocalDateTime timestamp;
    private String ip;
    private String method;
    private String path;
    private Integer status;
    private Long size;
    private Long responseTime;
    private String userAgent;
    private String referer;
}
