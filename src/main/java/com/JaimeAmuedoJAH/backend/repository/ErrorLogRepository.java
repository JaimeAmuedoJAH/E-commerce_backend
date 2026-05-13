package com.JaimeAmuedoJAH.backend.repository;

import com.JaimeAmuedoJAH.backend.entity.ErrorLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLogEntity, Long> {

    List<ErrorLogEntity> findByUserEmail(String userEmail);

    List<ErrorLogEntity> findByHttpStatus(Integer httpStatus);

    List<ErrorLogEntity> findBySeverity(ErrorLogEntity.ErrorSeverity severity);

    @Query("SELECT e FROM ErrorLogEntity e WHERE e.timestamp BETWEEN :startTime AND :endTime ORDER BY e.timestamp DESC")
    List<ErrorLogEntity> findErrorsBetween(LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT e FROM ErrorLogEntity e WHERE e.severity = 'HIGH' ORDER BY e.timestamp DESC")
    List<ErrorLogEntity> findCriticalErrors();
}
