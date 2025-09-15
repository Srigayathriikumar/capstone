package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PermissionService {
    
    // Permission CRUD Operations
    Permission grantPermission(Long userId, Long resourceId, Permission.AccessLevel accessLevel, LocalDateTime expiresAt);
    Optional<Permission> getPermissionById(Long permissionId);
    List<Permission> getAllPermissions();
    Permission updatePermission(Long permissionId, Permission.AccessLevel accessLevel, LocalDateTime expiresAt);
    void deletePermission(Long permissionId);
    
    // Permission Management
    void revokePermission(Long permissionId);
    void activatePermission(Long permissionId);
    void deactivatePermission(Long permissionId);
    void extendPermission(Long permissionId, LocalDateTime newExpiryDate);
    void makePermissionPermanent(Long permissionId);
    
    // User-Resource Permission Operations
    Optional<Permission> getUserResourcePermission(Long userId, Long resourceId);
    List<Permission> getUserPermissions(Long userId);
    List<Permission> getResourcePermissions(Long resourceId);
    List<Permission> getActivePermissions();
    List<Permission> getExpiredPermissions();
    List<Permission> getExpiringPermissions(LocalDateTime beforeDate);
    
    // Permission Queries
    boolean hasPermission(Long userId, Long resourceId);
    boolean hasPermissionLevel(Long userId, Long resourceId, Permission.AccessLevel requiredLevel);
    Permission.AccessLevel getUserAccessLevel(Long userId, Long resourceId);
    List<User> getUsersWithAccessToResource(Long resourceId);
    List<Resource> getResourcesAccessibleByUser(Long userId);
    
    // Project-based Permission Operations
    List<Permission> getProjectPermissions(Long projectId);
    List<Permission> getUserPermissionsInProject(Long userId, Long projectId);
    void revokeAllUserPermissionsInProject(Long userId, Long projectId);
    void revokeAllPermissionsForResource(Long resourceId);
    
    // Time-bound Permission Operations
    List<Permission> getTimeBoundPermissions();
    void cleanupExpiredPermissions();
    void cleanupUserExpiredPermissions(Long userId);
    List<Permission> getPermissionsExpiringIn(int days);
    void notifyExpiringPermissions(int daysBeforeExpiry);
    
    // Bulk Permission Operations
    void grantMultiplePermissions(List<Long> userIds, Long resourceId, Permission.AccessLevel accessLevel, LocalDateTime expiresAt);
    void revokeMultiplePermissions(List<Long> permissionIds);
    void updateMultiplePermissionLevels(List<Long> permissionIds, Permission.AccessLevel newAccessLevel);
    
    // Permission Statistics
    long getPermissionCount();
    long getActivePermissionCount();
    long getExpiredPermissionCount();
    int getUserPermissionCount(Long userId);
    int getResourcePermissionCount(Long resourceId);
    
    // Permission Validation
    boolean isPermissionActive(Long permissionId);
    boolean isPermissionExpired(Long permissionId);
    boolean canUserAccessResource(Long userId, Long resourceId);
    boolean permissionExists(Long userId, Long resourceId);
    
    // Manager Operations
    void revokeUserResourceAccess(Long userId, Long resourceId);
}