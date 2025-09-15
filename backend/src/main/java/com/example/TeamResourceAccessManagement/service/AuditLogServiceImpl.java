package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.AuditLog;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.repository.AuditLogRepository;
import com.example.TeamResourceAccessManagement.repository.UserRepository;
import com.example.TeamResourceAccessManagement.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Override
    public AuditLog createAuditLog(Long userId, Long resourceId, AuditLog.ActionType action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now());
        
        if (userId != null) {
            userRepository.findById(userId).ifPresent(auditLog::setUser);
        }
        if (resourceId != null) {
            resourceRepository.findById(resourceId).ifPresent(auditLog::setResource);
        }
        
        return auditLogRepository.save(auditLog);
    }
    
    @Override
    public Optional<AuditLog> getAuditLogById(Long auditLogId) {
        return auditLogRepository.findById(auditLogId);
    }
    
    @Override
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
    
    @Override
    public void deleteAuditLog(Long auditLogId) {
        auditLogRepository.deleteById(auditLogId);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByResource(Long resourceId) {
        return auditLogRepository.findByResourceId(resourceId);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByAction(AuditLog.ActionType action) {
        return auditLogRepository.findByAction(action);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByUserAndAction(Long userId, AuditLog.ActionType action) {
        return auditLogRepository.findByUserIdAndAction(userId, action);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByResourceAndAction(Long resourceId, AuditLog.ActionType action) {
        return auditLogRepository.findByResourceIdAndAction(resourceId, action);
    }
    
    @Override
    public List<AuditLog> getAuditLogsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByUserBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByUserIdAndTimestampBetween(userId, startDate, endDate);
    }
    
    @Override
    public List<AuditLog> getAuditLogsByResourceBetween(Long resourceId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByResourceIdAndTimestampBetween(resourceId, startDate, endDate);
    }
    
    @Override
    public List<AuditLog> getRecentAuditLogs(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findByTimestampBetween(since, LocalDateTime.now());
    }
    
    @Override
    public List<AuditLog> getTodaysAuditLogs() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return auditLogRepository.findByTimestampBetween(startOfDay, LocalDateTime.now());
    }
    
    @Override
    public List<AuditLog> searchAuditLogsByDetails(String keyword) {
        return auditLogRepository.findByDetailsContaining(keyword);
    }
    
    @Override
    public List<AuditLog> getAuditLogsAfter(LocalDateTime date) {
        return auditLogRepository.findByTimestampBetween(date, LocalDateTime.now());
    }
    
    @Override
    public List<AuditLog> getAuditLogsBefore(LocalDateTime date) {
        return auditLogRepository.findByTimestampBetween(LocalDateTime.of(2000, 1, 1, 0, 0), date);
    }
    
    @Override
    public long getAuditLogCount() {
        return auditLogRepository.count();
    }
    
    @Override
    public long getAuditLogCountByUser(Long userId) {
        return auditLogRepository.countByUserId(userId);
    }
    
    @Override
    public long getAuditLogCountByResource(Long resourceId) {
        return auditLogRepository.countByResourceId(resourceId);
    }
    
    @Override
    public long getAuditLogCountByAction(AuditLog.ActionType action) {
        return auditLogRepository.countByAction(action);
    }
    
    @Override
    public long getAuditLogCountBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate).size();
    }
    
    @Override
    public void logUserAction(Long userId, AuditLog.ActionType action, String details) {
        createAuditLog(userId, null, action, details);
    }
    
    @Override
    public void logResourceAction(Long userId, Long resourceId, AuditLog.ActionType action, String details) {
        createAuditLog(userId, resourceId, action, details);
    }
    
    @Override
    public void logPermissionAction(Long userId, Long resourceId, AuditLog.ActionType action, String details) {
        createAuditLog(userId, resourceId, action, details);
    }
    
    @Override
    public void logAccessRequestAction(Long userId, Long resourceId, AuditLog.ActionType action, String details) {
        createAuditLog(userId, resourceId, action, details);
    }
    
    @Override
    public void deleteOldAuditLogs(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<AuditLog> oldLogs = auditLogRepository.findByTimestampBetween(LocalDateTime.of(2000, 1, 1, 0, 0), cutoffDate);
        auditLogRepository.deleteAll(oldLogs);
    }
    
    @Override
    public void archiveOldAuditLogs(int daysOld) {
        // Implementation would depend on archiving strategy
        deleteOldAuditLogs(daysOld);
    }
    
    @Override
    public void cleanupAuditLogs() {
        deleteOldAuditLogs(365); // Delete logs older than 1 year
    }
    
    @Override
    public List<AuditLog> getUserActivityReport(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByUserIdAndTimestampBetween(userId, startDate, endDate);
    }
    
    @Override
    public List<AuditLog> getResourceActivityReport(Long resourceId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByResourceIdAndTimestampBetween(resourceId, startDate, endDate);
    }
    
    @Override
    public List<AuditLog> getSystemActivityReport(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }
    
    @Override
    public List<AuditLog> getSecurityAuditReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> allLogs = auditLogRepository.findByTimestampBetween(startDate, endDate);
        return allLogs.stream()
            .filter(log -> log.getAction() == AuditLog.ActionType.USER_LOGIN || 
                          log.getAction() == AuditLog.ActionType.USER_LOGOUT ||
                          log.getAction() == AuditLog.ActionType.ACCESS_GRANTED ||
                          log.getAction() == AuditLog.ActionType.ACCESS_REVOKED)
            .collect(Collectors.toList());
    }
}