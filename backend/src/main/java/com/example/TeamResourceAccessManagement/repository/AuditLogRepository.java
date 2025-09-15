package com.example.TeamResourceAccessManagement.repository;

import com.example.TeamResourceAccessManagement.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByResourceId(Long resourceId);
    List<AuditLog> findByAction(AuditLog.ActionType action);
    List<AuditLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = :userId AND a.action = :action")
    List<AuditLog> findByUserIdAndAction(@Param("userId") Long userId, @Param("action") AuditLog.ActionType action);
    
    @Query("SELECT a FROM AuditLog a WHERE a.resource.id = :resourceId AND a.action = :action")
    List<AuditLog> findByResourceIdAndAction(@Param("resourceId") Long resourceId, @Param("action") AuditLog.ActionType action);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = :userId AND a.timestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByUserIdAndTimestampBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditLog a WHERE a.resource.id = :resourceId AND a.timestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByResourceIdAndTimestampBetween(@Param("resourceId") Long resourceId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditLog a WHERE a.details LIKE %:keyword%")
    List<AuditLog> findByDetailsContaining(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.resource.id = :resourceId")
    long countByResourceId(@Param("resourceId") Long resourceId);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action")
    long countByAction(@Param("action") AuditLog.ActionType action);
}