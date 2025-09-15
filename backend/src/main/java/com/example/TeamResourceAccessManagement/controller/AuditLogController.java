package com.example.TeamResourceAccessManagement.controller;

import com.example.TeamResourceAccessManagement.domain.AuditLog;
import com.example.TeamResourceAccessManagement.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<AuditLog> createAuditLog(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long resourceId,
            @RequestParam AuditLog.ActionType action,
            @RequestParam String details) {
        AuditLog auditLog = auditLogService.createAuditLog(userId, resourceId, action, details);
        return new ResponseEntity<>(auditLog, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        Optional<AuditLog> auditLog = auditLogService.getAuditLogById(id);
        return auditLog.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        List<AuditLog> auditLogs = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(auditLogs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable Long id) {
        auditLogService.deleteAuditLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUser(@PathVariable Long userId) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByUser(userId);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByResource(@PathVariable Long resourceId) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByResource(resourceId);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(@PathVariable AuditLog.ActionType action) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByAction(action);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/user/{userId}/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUserAndAction(@PathVariable Long userId, @PathVariable AuditLog.ActionType action) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByUserAndAction(userId, action);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/resource/{resourceId}/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByResourceAndAction(@PathVariable Long resourceId, @PathVariable AuditLog.ActionType action) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByResourceAndAction(resourceId, action);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/between")
    public ResponseEntity<List<AuditLog>> getAuditLogsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsBetween(startDate, endDate);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/user/{userId}/between")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUserBetween(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByUserBetween(userId, startDate, endDate);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/resource/{resourceId}/between")
    public ResponseEntity<List<AuditLog>> getAuditLogsByResourceBetween(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByResourceBetween(resourceId, startDate, endDate);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLog>> getRecentAuditLogs(@RequestParam(defaultValue = "24") int hours) {
        List<AuditLog> auditLogs = auditLogService.getRecentAuditLogs(hours);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/today")
    public ResponseEntity<List<AuditLog>> getTodaysAuditLogs() {
        List<AuditLog> auditLogs = auditLogService.getTodaysAuditLogs();
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuditLog>> searchAuditLogsByDetails(@RequestParam String keyword) {
        List<AuditLog> auditLogs = auditLogService.searchAuditLogsByDetails(keyword);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/after")
    public ResponseEntity<List<AuditLog>> getAuditLogsAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsAfter(date);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/before")
    public ResponseEntity<List<AuditLog>> getAuditLogsBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsBefore(date);
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getAuditLogCount() {
        long count = auditLogService.getAuditLogCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> getAuditLogCountByUser(@PathVariable Long userId) {
        long count = auditLogService.getAuditLogCountByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/resource/{resourceId}")
    public ResponseEntity<Long> getAuditLogCountByResource(@PathVariable Long resourceId) {
        long count = auditLogService.getAuditLogCountByResource(resourceId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/action/{action}")
    public ResponseEntity<Long> getAuditLogCountByAction(@PathVariable AuditLog.ActionType action) {
        long count = auditLogService.getAuditLogCountByAction(action);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/log-user-action")
    public ResponseEntity<Void> logUserAction(
            @RequestParam Long userId,
            @RequestParam AuditLog.ActionType action,
            @RequestParam String details) {
        auditLogService.logUserAction(userId, action, details);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/log-resource-action")
    public ResponseEntity<Void> logResourceAction(
            @RequestParam Long userId,
            @RequestParam Long resourceId,
            @RequestParam AuditLog.ActionType action,
            @RequestParam String details) {
        auditLogService.logResourceAction(userId, resourceId, action, details);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> cleanupAuditLogs() {
        auditLogService.cleanupAuditLogs();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete-old")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteOldAuditLogs(@RequestParam int daysOld) {
        auditLogService.deleteOldAuditLogs(daysOld);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports/user/{userId}")
    public ResponseEntity<List<AuditLog>> getUserActivityReport(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> report = auditLogService.getUserActivityReport(userId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/resource/{resourceId}")
    public ResponseEntity<List<AuditLog>> getResourceActivityReport(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> report = auditLogService.getResourceActivityReport(resourceId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/system")
    public ResponseEntity<List<AuditLog>> getSystemActivityReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> report = auditLogService.getSystemActivityReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/security")
    public ResponseEntity<List<AuditLog>> getSecurityAuditReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> report = auditLogService.getSecurityAuditReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }
}