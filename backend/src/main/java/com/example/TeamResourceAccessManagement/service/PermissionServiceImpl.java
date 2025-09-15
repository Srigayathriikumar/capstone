package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.repository.PermissionRepository;
import com.example.TeamResourceAccessManagement.repository.UserRepository;
import com.example.TeamResourceAccessManagement.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Override
    public Permission grantPermission(Long userId, Long resourceId, Permission.AccessLevel accessLevel, LocalDateTime expiresAt) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        
        if (userOpt.isPresent() && resourceOpt.isPresent()) {
            Permission permission = new Permission(userOpt.get(), resourceOpt.get(), accessLevel, expiresAt);
            return permissionRepository.save(permission);
        }
        return null;
    }
    
    @Override
    public Optional<Permission> getPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId);
    }
    
    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
    
    @Override
    public Permission updatePermission(Long permissionId, Permission.AccessLevel accessLevel, LocalDateTime expiresAt) {
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            permission.setAccessLevel(accessLevel);
            permission.setExpiresAt(expiresAt);
            return permissionRepository.save(permission);
        }
        return null;
    }
    
    @Override
    public void deletePermission(Long permissionId) {
        permissionRepository.deleteById(permissionId);
    }
    
    @Override
    public void revokePermission(Long permissionId) {
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            permission.setIsActive(false);
            permissionRepository.save(permission);
        }
    }
    
    @Override
    public void activatePermission(Long permissionId) {
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            permission.setIsActive(true);
            permissionRepository.save(permission);
        }
    }
    
    @Override
    public void deactivatePermission(Long permissionId) {
        revokePermission(permissionId);
    }
    
    @Override
    public void extendPermission(Long permissionId, LocalDateTime newExpiryDate) {
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            permission.setExpiresAt(newExpiryDate);
            permissionRepository.save(permission);
        }
    }
    
    @Override
    public void makePermissionPermanent(Long permissionId) {
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            permission.setExpiresAt(null);
            permissionRepository.save(permission);
        }
    }
    
    @Override
    public Optional<Permission> getUserResourcePermission(Long userId, Long resourceId) {
        return permissionRepository.findByUserIdAndResourceId(userId, resourceId);
    }
    
    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return permissionRepository.findByUserId(userId);
    }
    
    @Override
    public List<Permission> getResourcePermissions(Long resourceId) {
        return permissionRepository.findByResourceId(resourceId);
    }
    
    @Override
    public List<Permission> getActivePermissions() {
        return permissionRepository.findByIsActive(true);
    }
    
    @Override
    public List<Permission> getExpiredPermissions() {
        return permissionRepository.findExpiredPermissions(LocalDateTime.now());
    }
    
    @Override
    public List<Permission> getExpiringPermissions(LocalDateTime beforeDate) {
        return permissionRepository.findExpiredPermissions(beforeDate);
    }
    
    @Override
    public boolean hasPermission(Long userId, Long resourceId) {
        Optional<Permission> permission = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        return permission.isPresent() && permission.get().getIsActive() &&
               (permission.get().getExpiresAt() == null || permission.get().getExpiresAt().isAfter(LocalDateTime.now()));
    }
    
    @Override
    public boolean hasPermissionLevel(Long userId, Long resourceId, Permission.AccessLevel requiredLevel) {
        Optional<Permission> permission = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        if (permission.isPresent() && permission.get().getIsActive()) {
            Permission.AccessLevel userLevel = permission.get().getAccessLevel();
            return isAccessLevelSufficient(userLevel, requiredLevel);
        }
        return false;
    }
    
    private boolean isAccessLevelSufficient(Permission.AccessLevel userLevel, Permission.AccessLevel requiredLevel) {
        // FULL_ACCESS > ADMIN > WRITE > READ
        int userLevelValue = getAccessLevelValue(userLevel);
        int requiredLevelValue = getAccessLevelValue(requiredLevel);
        return userLevelValue >= requiredLevelValue;
    }
    
    private int getAccessLevelValue(Permission.AccessLevel level) {
        switch (level) {
            case READ: return 1;
            case WRITE: return 2;
            case ADMIN: return 3;
            case FULL_ACCESS: return 4;
            default: return 0;
        }
    }
    
    @Override
    public Permission.AccessLevel getUserAccessLevel(Long userId, Long resourceId) {
        Optional<Permission> permission = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        if (permission.isPresent() && permission.get().getIsActive()) {
            return permission.get().getAccessLevel();
        }
        return null;
    }
    
    @Override
    public List<User> getUsersWithAccessToResource(Long resourceId) {
        List<Permission> permissions = permissionRepository.findByResourceId(resourceId);
        return permissions.stream()
            .filter(Permission::getIsActive)
            .filter(p -> p.getExpiresAt() == null || p.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(Permission::getUser)
            .distinct()
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Resource> getResourcesAccessibleByUser(Long userId) {
        List<Permission> permissions = permissionRepository.findByUserId(userId);
        return permissions.stream()
            .filter(Permission::getIsActive)
            .filter(p -> p.getExpiresAt() == null || p.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(Permission::getResource)
            .distinct()
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Permission> getProjectPermissions(Long projectId) {
        return permissionRepository.findByUserIdAndProjectId(null, projectId);
    }
    
    @Override
    public List<Permission> getUserPermissionsInProject(Long userId, Long projectId) {
        return permissionRepository.findByUserIdAndProjectId(userId, projectId);
    }
    
    @Override
    public void revokeAllUserPermissionsInProject(Long userId, Long projectId) {
        List<Permission> permissions = permissionRepository.findByUserIdAndProjectId(userId, projectId);
        permissions.forEach(permission -> {
            permission.setIsActive(false);
            permissionRepository.save(permission);
        });
    }
    
    @Override
    public void revokeAllPermissionsForResource(Long resourceId) {
        List<Permission> permissions = permissionRepository.findByResourceId(resourceId);
        permissions.forEach(permission -> {
            permission.setIsActive(false);
            permissionRepository.save(permission);
        });
    }
    
    @Override
    public List<Permission> getTimeBoundPermissions() {
        return permissionRepository.findAll().stream()
            .filter(p -> p.getExpiresAt() != null)
            .collect(Collectors.toList());
    }
    
    @Override
    public void cleanupExpiredPermissions() {
        List<Permission> expiredPermissions = permissionRepository.findExpiredPermissions(LocalDateTime.now());
        expiredPermissions.forEach(permission -> {
            permission.setIsActive(false);
            permissionRepository.save(permission);
        });
    }
    
    @Override
    public void cleanupUserExpiredPermissions(Long userId) {
        List<Permission> userPermissions = permissionRepository.findByUserId(userId);
        userPermissions.stream()
            .filter(p -> p.getExpiresAt() != null && p.getExpiresAt().isBefore(LocalDateTime.now()))
            .forEach(permission -> {
                permission.setIsActive(false);
                permissionRepository.save(permission);
            });
    }
    
    @Override
    public List<Permission> getPermissionsExpiringIn(int days) {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(days);
        return permissionRepository.findExpiredPermissions(futureDate);
    }
    
    @Override
    public void notifyExpiringPermissions(int daysBeforeExpiry) {
        List<Permission> expiringPermissions = getPermissionsExpiringIn(daysBeforeExpiry);
        // Implementation for notification logic would go here
    }
    
    @Override
    public void grantMultiplePermissions(List<Long> userIds, Long resourceId, Permission.AccessLevel accessLevel, LocalDateTime expiresAt) {
        userIds.forEach(userId -> grantPermission(userId, resourceId, accessLevel, expiresAt));
    }
    
    @Override
    public void revokeMultiplePermissions(List<Long> permissionIds) {
        permissionIds.forEach(this::revokePermission);
    }
    
    @Override
    public void updateMultiplePermissionLevels(List<Long> permissionIds, Permission.AccessLevel newAccessLevel) {
        permissionIds.forEach(permissionId -> {
            Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
            if (permissionOpt.isPresent()) {
                Permission permission = permissionOpt.get();
                permission.setAccessLevel(newAccessLevel);
                permissionRepository.save(permission);
            }
        });
    }
    
    @Override
    public long getPermissionCount() {
        return permissionRepository.count();
    }
    
    @Override
    public long getActivePermissionCount() {
        return permissionRepository.findByIsActive(true).size();
    }
    
    @Override
    public long getExpiredPermissionCount() {
        return permissionRepository.findExpiredPermissions(LocalDateTime.now()).size();
    }
    
    @Override
    public int getUserPermissionCount(Long userId) {
        return permissionRepository.findByUserId(userId).size();
    }
    
    @Override
    public int getResourcePermissionCount(Long resourceId) {
        return permissionRepository.findByResourceId(resourceId).size();
    }
    
    @Override
    public boolean isPermissionActive(Long permissionId) {
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        return permissionOpt.isPresent() && permissionOpt.get().getIsActive();
    }
    
    @Override
    public boolean isPermissionExpired(Long permissionId) {
        Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            return permission.getExpiresAt() != null && permission.getExpiresAt().isBefore(LocalDateTime.now());
        }
        return false;
    }
    
    @Override
    public boolean canUserAccessResource(Long userId, Long resourceId) {
        // First check if user has explicit permission
        if (hasPermission(userId, resourceId)) {
            return true;
        }
        
        // Check if user can access via allowedUserGroups
        return canUserAccessViaUserGroups(userId, resourceId);
    }
    
    private boolean canUserAccessViaUserGroups(Long userId, Long resourceId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        
        if (userOpt.isPresent() && resourceOpt.isPresent()) {
            User user = userOpt.get();
            Resource resource = resourceOpt.get();
            
            // Only check for manager controlled resources
            // if (resource.getAccessType() != Resource.ResourceAccessType.MANAGER_CONTROLLED) {
            //     return false;
            // }
            
            String allowedGroups = resource.getAllowedUserGroups();
            if (allowedGroups == null || allowedGroups.trim().isEmpty()) {
                return false;
            }
            
            String username = user.getUsername();
            if (username == null) {
                return false;
            }
            
            // Check if user's domain matches any allowed groups
            String[] groups = allowedGroups.split(",");
            for (String group : groups) {
                group = group.trim();
                if (!group.isEmpty() && username.toLowerCase().contains("." + group.toLowerCase())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean permissionExists(Long userId, Long resourceId) {
        return permissionRepository.findByUserIdAndResourceId(userId, resourceId).isPresent();
    }
    
    @Override
    public void revokeUserResourceAccess(Long userId, Long resourceId) {
        Optional<Permission> permission = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        if (permission.isPresent()) {
            permission.get().setIsActive(false);
            permissionRepository.save(permission.get());
        }
    }
}