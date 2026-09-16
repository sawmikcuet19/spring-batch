package com.sawmik.spring_batch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "server_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
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
