package com.example.TeamResourceAccessManagement.dto;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.domain.Permission;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class AccessRequestDTO {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    private String userName;
    
    @NotNull(message = "Resource ID is required")
    private Long resourceId;
    private String resourceName;
    
    private Long projectId;
    private String projectName;
    
    private Long projectManagerId;
    private String projectManagerName;
    
    @NotNull(message = "Requested access level is required")
    private Permission.AccessLevel requestedAccessLevel;
    
    private AccessRequest.RequestStatus status;
    
    @Size(max = 1000, message = "Justification must not exceed 1000 characters")
    private String justification;
    
    @Size(max = 1000, message = "Approver comments must not exceed 1000 characters")
    private String approverComments;
    
    private Long approvedById;
    private String approvedByName;
    
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime requestedUntil;
    
    // Constructors
    public AccessRequestDTO() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public Long getProjectManagerId() { return projectManagerId; }
    public void setProjectManagerId(Long projectManagerId) { this.projectManagerId = projectManagerId; }
    
    public String getProjectManagerName() { return projectManagerName; }
    public void setProjectManagerName(String projectManagerName) { this.projectManagerName = projectManagerName; }
    
    public Permission.AccessLevel getRequestedAccessLevel() { return requestedAccessLevel; }
    public void setRequestedAccessLevel(Permission.AccessLevel requestedAccessLevel) { this.requestedAccessLevel = requestedAccessLevel; }
    
    public AccessRequest.RequestStatus getStatus() { return status; }
    public void setStatus(AccessRequest.RequestStatus status) { this.status = status; }
    
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    
    public String getApproverComments() { return approverComments; }
    public void setApproverComments(String approverComments) { this.approverComments = approverComments; }
    
    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }
    
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getRequestedUntil() { return requestedUntil; }
    public void setRequestedUntil(LocalDateTime requestedUntil) { this.requestedUntil = requestedUntil; }
    
    @Override
    public String toString() {
        return "AccessRequestDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", resourceId=" + resourceId +
                ", requestedAccessLevel=" + requestedAccessLevel +
                ", status=" + status +
                ", justification='" + justification + '\'' +
                '}';
    }
}