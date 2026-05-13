package com.JaimeAmuedoJAH.backend.service;

import com.JaimeAmuedoJAH.backend.entity.ErrorLogEntity;
import com.JaimeAmuedoJAH.backend.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ErrorTrackingService {

    private final ErrorLogRepository errorLogRepository;

    /**
     * Register an error in the database for audit purposes
     */
    public ErrorLogEntity trackError(
            HttpStatus status,
            String errorType,
            String message,
            HttpServletRequest request,
            String userEmail,
            Throwable throwable) {

        ErrorLogEntity.ErrorSeverity severity = determineSeverity(status);

        String stackTrace = null;
        if (throwable != null) {
            stackTrace = getStackTrace(throwable);
        }

        ErrorLogEntity errorLog = ErrorLogEntity.builder()
                .timestamp(LocalDateTime.now())
                .httpStatus(status.value())
                .errorType(errorType)
                .message(message)
                .endpoint(request.getRequestURI())
                .httpMethod(request.getMethod())
                .userAgent(request.getHeader("User-Agent"))
                .ipAddress(getClientIpAddress(request))
                .requestId(generateRequestId())
                .stackTrace(stackTrace)
                .userEmail(userEmail)
                .severity(severity)
                .build();

        ErrorLogEntity saved = errorLogRepository.save(errorLog);

        // Log based on severity
        if (severity == ErrorLogEntity.ErrorSeverity.HIGH) {
            log.error("Critical error tracked: {} - {} - {}", errorType, status, message);
        } else if (severity == ErrorLogEntity.ErrorSeverity.MEDIUM) {
            log.warn("Medium severity error tracked: {} - {} - {}", errorType, status, message);
        } else {
            log.debug("Error tracked: {} - {} - {}", errorType, status, message);
        }

        return saved;
    }

    /**
     * Get recent errors
     */
    @Transactional(readOnly = true)
    public List<ErrorLogEntity> getRecentErrors(int limit) {
        return errorLogRepository.findAll().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(limit)
                .toList();
    }

    /**
     * Get errors by user
     */
    @Transactional(readOnly = true)
    public List<ErrorLogEntity> getErrorsByUser(String userEmail) {
        return errorLogRepository.findByUserEmail(userEmail);
    }

    /**
     * Get critical errors
     */
    @Transactional(readOnly = true)
    public List<ErrorLogEntity> getCriticalErrors() {
        return errorLogRepository.findCriticalErrors();
    }

    /**
     * Determine severity level based on HTTP status
     */
    private ErrorLogEntity.ErrorSeverity determineSeverity(HttpStatus status) {
        if (status.is5xxServerError()) {
            return ErrorLogEntity.ErrorSeverity.HIGH;
        } else if (status.value() == 409 || status.value() == 401 || status.value() == 402) {
            return ErrorLogEntity.ErrorSeverity.MEDIUM;
        } else {
            return ErrorLogEntity.ErrorSeverity.LOW;
        }
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Generate unique request ID
     */
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 10000);
    }

    /**
     * Convert exception stack trace to string
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        if (throwable.getCause() != null) {
            sb.append("Caused by: ").append(getStackTrace(throwable.getCause()));
        }
        return sb.toString();
    }
}
