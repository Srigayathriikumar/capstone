package com.example.TeamResourceAccessManagement.dto;

import java.time.LocalDateTime;
import com.example.TeamResourceAccessManagement.domain.Project;

public class ProjectResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Project.ProjectStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int memberCount;
    private int resourceCount;
    
    // Constructors
    public ProjectResponseDTO() {}
    
    public ProjectResponseDTO(Long id, String name, String description, Project.ProjectStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, int memberCount, int resourceCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.memberCount = memberCount;
        this.resourceCount = resourceCount;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Project.ProjectStatus getStatus() { return status; }
    public void setStatus(Project.ProjectStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    
    public int getResourceCount() { return resourceCount; }
    public void setResourceCount(int resourceCount) { this.resourceCount = resourceCount; }
}
