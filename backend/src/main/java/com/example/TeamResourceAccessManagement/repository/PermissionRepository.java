package com.example.TeamResourceAccessManagement.repository;

import com.example.TeamResourceAccessManagement.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByUserId(Long userId);
    List<Permission> findByResourceId(Long resourceId);
    List<Permission> findByIsActive(Boolean isActive);
    
    Optional<Permission> findByUserIdAndResourceId(Long userId, Long resourceId);
    
    @Query("SELECT p FROM Permission p WHERE p.expiresAt < :now AND p.isActive = true")
    List<Permission> findExpiredPermissions(LocalDateTime now);
    
    @Query("SELECT p FROM Permission p WHERE p.user.id = :userId AND p.resource.project.id = :projectId")
    List<Permission> findByUserIdAndProjectId(Long userId, Long projectId);
}