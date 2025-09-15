package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.AuditLog;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditLogService {
    
    // Audit Log CRUD Operations
    AuditLog createAuditLog(Long userId, Long resourceId, AuditLog.ActionType action, String details);
    Optional<AuditLog> getAuditLogById(Long auditLogId);
    List<AuditLog> getAllAuditLogs();
    void deleteAuditLog(Long auditLogId);
    
    // Query Operations by Entity
    List<AuditLog> getAuditLogsByUser(Long userId);
    List<AuditLog> getAuditLogsByResource(Long resourceId);
    List<AuditLog> getAuditLogsByAction(AuditLog.ActionType action);
    
    // Combined Query Operations
    List<AuditLog> getAuditLogsByUserAndAction(Long userId, AuditLog.ActionType action);
    List<AuditLog> getAuditLogsByResourceAndAction(Long resourceId, AuditLog.ActionType action);
    
    // Time-based Query Operations
    List<AuditLog> getAuditLogsBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> getAuditLogsByUserBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> getAuditLogsByResourceBetween(Long resourceId, LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> getRecentAuditLogs(int hours);
    List<AuditLog> getTodaysAuditLogs();
    
    // Search Operations
    List<AuditLog> searchAuditLogsByDetails(String keyword);
    List<AuditLog> getAuditLogsAfter(LocalDateTime date);
    List<AuditLog> getAuditLogsBefore(LocalDateTime date);
    
    // Statistics Operations
    long getAuditLogCount();
    long getAuditLogCountByUser(Long userId);
    long getAuditLogCountByResource(Long resourceId);
    long getAuditLogCountByAction(AuditLog.ActionType action);
    long getAuditLogCountBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Utility Operations
    void logUserAction(Long userId, AuditLog.ActionType action, String details);
    void logResourceAction(Long userId, Long resourceId, AuditLog.ActionType action, String details);
    void logPermissionAction(Long userId, Long resourceId, AuditLog.ActionType action, String details);
    void logAccessRequestAction(Long userId, Long resourceId, AuditLog.ActionType action, String details);
    
    // Cleanup Operations
    void deleteOldAuditLogs(int daysOld);
    void archiveOldAuditLogs(int daysOld);
    void cleanupAuditLogs();
    
    // Reporting Operations
    List<AuditLog> getUserActivityReport(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> getResourceActivityReport(Long resourceId, LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> getSystemActivityReport(LocalDateTime startDate, LocalDateTime endDate);
    List<AuditLog> getSecurityAuditReport(LocalDateTime startDate, LocalDateTime endDate);
}