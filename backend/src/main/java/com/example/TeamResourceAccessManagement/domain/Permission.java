package com.example.TeamResourceAccessManagement.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "permissions",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_permission_user_resource", columnNames = {"user_id", "resource_id"})
       },
       indexes = {
           @Index(name = "idx_permission_user_id", columnList = "user_id"),
           @Index(name = "idx_permission_resource_id", columnList = "resource_id"),
           @Index(name = "idx_permission_access_level", columnList = "access_level"),
           @Index(name = "idx_permission_is_active", columnList = "is_active"),
           @Index(name = "idx_permission_expires_at", columnList = "expires_at")
       })
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_permission_user"))
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false, foreignKey = @ForeignKey(name = "fk_permission_resource"))
    private Resource resource;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    private AccessLevel accessLevel;
    
    @CreationTimestamp
    @Column(name = "granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt; 
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public enum AccessLevel {
        READ, WRITE, ADMIN, FULL_ACCESS
    }

    public Permission() {}
    
    public Permission(User user, Resource resource, AccessLevel accessLevel, LocalDateTime expiresAt) {
        this.user = user;
        this.resource = resource;
        this.accessLevel = accessLevel;
        this.expiresAt = expiresAt;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }
    
    public AccessLevel getAccessLevel() { return accessLevel; }
    public void setAccessLevel(AccessLevel accessLevel) { this.accessLevel = accessLevel; }
    
    public LocalDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(LocalDateTime grantedAt) { this.grantedAt = grantedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}