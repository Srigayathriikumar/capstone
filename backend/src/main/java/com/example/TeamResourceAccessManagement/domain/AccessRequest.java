package com.example.TeamResourceAccessManagement.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_requests",
       indexes = {
           @Index(name = "idx_access_request_user_id", columnList = "user_id"),
           @Index(name = "idx_access_request_resource_id", columnList = "resource_id"),
           @Index(name = "idx_access_request_status", columnList = "status"),
           @Index(name = "idx_access_request_approved_by", columnList = "approved_by"),
           @Index(name = "idx_access_request_requested_at", columnList = "requested_at")
       })
public class AccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_access_request_user"))
    @JsonManagedReference
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "resource_id", nullable = false, foreignKey = @ForeignKey(name = "fk_access_request_resource"))
    @JsonManagedReference
    private Resource resource;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_access_request_project"))
    @JsonManagedReference
    private Project project;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_manager_id", foreignKey = @ForeignKey(name = "fk_access_request_project_manager"))
    @JsonManagedReference
    private User projectManager;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_access_level", nullable = false, length = 20)
    private Permission.AccessLevel requestedAccessLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status;
    
    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;
    
    @Column(name = "approver_comments", columnDefinition = "TEXT")
    private String approverComments;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by", foreignKey = @ForeignKey(name = "fk_access_request_approver"))
    @JsonManagedReference
    private User approvedBy;
    
    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "requested_until")
    private LocalDateTime requestedUntil; 

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED, EXPIRED
    }

    public AccessRequest() {}
    
    public AccessRequest(User user, Resource resource, Permission.AccessLevel requestedAccessLevel, 
                        String justification, LocalDateTime requestedUntil) {
        this.user = user;
        this.resource = resource;
        this.requestedAccessLevel = requestedAccessLevel;
        this.justification = justification;
        this.requestedUntil = requestedUntil;
        this.status = RequestStatus.PENDING;
    }

 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public User getProjectManager() { return projectManager; }
    public void setProjectManager(User projectManager) { this.projectManager = projectManager; }
    
    public Permission.AccessLevel getRequestedAccessLevel() { return requestedAccessLevel; }
    public void setRequestedAccessLevel(Permission.AccessLevel requestedAccessLevel) { 
        this.requestedAccessLevel = requestedAccessLevel; 
    }
    
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    
    public String getApproverComments() { return approverComments; }
    public void setApproverComments(String approverComments) { this.approverComments = approverComments; }
    
    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getRequestedUntil() { return requestedUntil; }
    public void setRequestedUntil(LocalDateTime requestedUntil) { this.requestedUntil = requestedUntil; }
}