package com.example.TeamResourceAccessManagement.controller;

import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping
    public ResponseEntity<Permission> grantPermission(
            @RequestParam Long userId,
            @RequestParam Long resourceId,
            @RequestParam Permission.AccessLevel accessLevel,
            @RequestParam(required = false) LocalDateTime expiresAt) {
        Permission permission = permissionService.grantPermission(userId, resourceId, accessLevel, expiresAt);
        return new ResponseEntity<>(permission, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long id) {
        Optional<Permission> permission = permissionService.getPermissionById(id);
        return permission.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Permission>> getUserPermissions(@PathVariable Long userId) {
        List<Permission> permissions = permissionService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<com.example.TeamResourceAccessManagement.dto.ResourceAccessDTO>> getResourcePermissions(@PathVariable Long resourceId) {
        List<Permission> permissions = permissionService.getResourcePermissions(resourceId);
        List<com.example.TeamResourceAccessManagement.dto.ResourceAccessDTO> accessList = permissions.stream()
            .map(p -> new com.example.TeamResourceAccessManagement.dto.ResourceAccessDTO(
                p.getUser().getId(),
                p.getUser().getUsername(),
                p.getUser().getEmail(),
                p.getAccessLevel()
            ))
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(accessList);
    }

    @GetMapping("/user/{userId}/resource/{resourceId}")
    public ResponseEntity<Permission> getUserResourcePermission(@PathVariable Long userId, @PathVariable Long resourceId) {
        Optional<Permission> permission = permissionService.getUserResourcePermission(userId, resourceId);
        return permission.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Permission>> getActivePermissions() {
        List<Permission> permissions = permissionService.getActivePermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Permission>> getExpiredPermissions() {
        List<Permission> permissions = permissionService.getExpiredPermissions();
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(
            @PathVariable Long id,
            @RequestParam Permission.AccessLevel accessLevel,
            @RequestParam(required = false) LocalDateTime expiresAt) {
        Permission permission = permissionService.updatePermission(id, accessLevel, expiresAt);
        if (permission != null) {
            return ResponseEntity.ok(permission);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/revoke")
    public ResponseEntity<Void> revokePermission(@PathVariable Long id) {
        permissionService.revokePermission(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activatePermission(@PathVariable Long id) {
        permissionService.activatePermission(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Permission>> getUserActivePermissions(@PathVariable Long userId) {
        List<Permission> permissions = permissionService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/resource/{resourceId}/active")
    public ResponseEntity<List<Permission>> getResourceActivePermissions(@PathVariable Long resourceId) {
        List<Permission> permissions = permissionService.getResourcePermissions(resourceId);
        return ResponseEntity.ok(permissions);
    }

    @PostMapping("/cleanup-expired")
    public ResponseEntity<Void> cleanupExpiredPermissions() {
        permissionService.cleanupExpiredPermissions();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getPermissionCount() {
        long count = permissionService.getPermissionCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> getActivePermissionCount() {
        long count = permissionService.getActivePermissionCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Integer> getUserPermissionCount(@PathVariable Long userId) {
        int count = permissionService.getUserPermissionCount(userId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePermission(@PathVariable Long id) {
        permissionService.deactivatePermission(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<Void> extendPermission(@PathVariable Long id, @RequestParam LocalDateTime newExpiryDate) {
        permissionService.extendPermission(id, newExpiryDate);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/make-permanent")
    public ResponseEntity<Void> makePermissionPermanent(@PathVariable Long id) {
        permissionService.makePermissionPermanent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Permission>> getExpiringPermissions(@RequestParam LocalDateTime beforeDate) {
        List<Permission> permissions = permissionService.getExpiringPermissions(beforeDate);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/user/{userId}/has-permission/{resourceId}")
    public ResponseEntity<Boolean> hasPermission(@PathVariable Long userId, @PathVariable Long resourceId) {
        boolean hasPermission = permissionService.hasPermission(userId, resourceId);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping("/user/{userId}/access-level/{resourceId}")
    public ResponseEntity<Permission.AccessLevel> getUserAccessLevel(@PathVariable Long userId, @PathVariable Long resourceId) {
        Permission.AccessLevel accessLevel = permissionService.getUserAccessLevel(userId, resourceId);
        return ResponseEntity.ok(accessLevel);
    }

    @PostMapping("/bulk-grant")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> grantMultiplePermissions(
            @Valid @RequestBody List<Long> userIds,
            @RequestParam Long resourceId,
            @RequestParam Permission.AccessLevel accessLevel,
            @RequestParam(required = false) LocalDateTime expiresAt) {
        permissionService.grantMultiplePermissions(userIds, resourceId, accessLevel, expiresAt);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-revoke")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> revokeMultiplePermissions(@Valid @RequestBody List<Long> permissionIds) {
        permissionService.revokeMultiplePermissions(permissionIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/expired")
    public ResponseEntity<Long> getExpiredPermissionCount() {
        long count = permissionService.getExpiredPermissionCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}/active")
    public ResponseEntity<Boolean> isPermissionActive(@PathVariable Long id) {
        boolean isActive = permissionService.isPermissionActive(id);
        return ResponseEntity.ok(isActive);
    }

    @GetMapping("/{id}/expired")
    public ResponseEntity<Boolean> isPermissionExpired(@PathVariable Long id) {
        boolean isExpired = permissionService.isPermissionExpired(id);
        return ResponseEntity.ok(isExpired);
    }
    
    @DeleteMapping("/user/{userId}/resource/{resourceId}")
    public ResponseEntity<Void> revokeUserResourceAccess(@PathVariable Long userId, @PathVariable Long resourceId) {
        System.out.println("\n🚀 REVOKE ACCESS REQUEST RECEIVED");
        System.out.println("Endpoint: DELETE /api/permissions/user/" + userId + "/resource/" + resourceId);
        
        try {
            if (userId == null || resourceId == null) {
                System.out.println("❌ Invalid input parameters");
                return ResponseEntity.badRequest().build();
            }
            
            permissionService.revokeUserResourceAccess(userId, resourceId);
            System.out.println("✅ CONTROLLER: Revoke access completed successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("❌ CONTROLLER: Revoke access failed - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}