package com.JaimeAmuedoJAH.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "http_status", nullable = false)
    private Integer httpStatus;

    @Column(name = "error_type", nullable = false, length = 100)
    private String errorType;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "endpoint", length = 255)
    private String endpoint;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Lob
    @Column(name = "stack_trace")
    private String stackTrace;

    @Column(name = "user_email", length = 100)
    private String userEmail;

    @Column(name = "severity")
    @Enumerated(EnumType.STRING)
    private ErrorSeverity severity;

    public enum ErrorSeverity {
        LOW,      // 4xx errors
        MEDIUM,   // Specific business errors
        HIGH      // 5xx errors
    }
}
