package com.example.TeamResourceAccessManagement.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs",
       indexes = {
           @Index(name = "idx_audit_log_user_id", columnList = "user_id"),
           @Index(name = "idx_audit_log_resource_id", columnList = "resource_id"),
           @Index(name = "idx_audit_log_action", columnList = "action"),
           @Index(name = "idx_audit_log_timestamp", columnList = "timestamp")
       })
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_audit_log_user"))
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", foreignKey = @ForeignKey(name = "fk_audit_log_resource"))
    private Resource resource;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private ActionType action;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public enum ActionType {
        ACCESS_GRANTED, ACCESS_REVOKED, ACCESS_REQUESTED, 
        RESOURCE_CREATED, RESOURCE_UPDATED, RESOURCE_DELETED,
        USER_LOGIN, USER_LOGOUT, PERMISSION_CHANGED
    }

    // Constructors
    public AuditLog() {}
    
    public AuditLog(User user, Resource resource, ActionType action, String details, String ipAddress) {
        this.user = user;
        this.resource = resource;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }
    
    public ActionType getAction() { return action; }
    public void setAction(ActionType action) { this.action = action; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}