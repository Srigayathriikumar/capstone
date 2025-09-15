package com.example.TeamResourceAccessManagement.dto;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.domain.Permission;
import java.time.LocalDateTime;

public class AccessRequestResponseDTO {
    private Long id;
    private String requestedBy;
    private String resourceName;
    private Permission.AccessLevel requestedAccessLevel;
    private AccessRequest.RequestStatus status;
    private String justification;
    private String approverComments;
    private String approvedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    
    public AccessRequestResponseDTO() {}
    
    public AccessRequestResponseDTO(AccessRequest request) {
        this.id = request.getId();
        this.requestedBy = request.getUser() != null ? request.getUser().getFullName() : "Unknown";
        this.resourceName = request.getResource() != null ? request.getResource().getName() : "Unknown";
        this.requestedAccessLevel = request.getRequestedAccessLevel();
        this.status = request.getStatus();
        this.justification = request.getJustification();
        this.approverComments = request.getApproverComments();
        this.approvedBy = request.getApprovedBy() != null ? request.getApprovedBy().getFullName() : null;
        this.requestedAt = request.getRequestedAt();
        this.approvedAt = request.getApprovedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    
    public Permission.AccessLevel getRequestedAccessLevel() { return requestedAccessLevel; }
    public void setRequestedAccessLevel(Permission.AccessLevel requestedAccessLevel) { 
        this.requestedAccessLevel = requestedAccessLevel; 
    }
    
    public AccessRequest.RequestStatus getStatus() { return status; }
    public void setStatus(AccessRequest.RequestStatus status) { this.status = status; }
    
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    
    public String getApproverComments() { return approverComments; }
    public void setApproverComments(String approverComments) { this.approverComments = approverComments; }
    
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}