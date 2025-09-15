package com.example.TeamResourceAccessManagement.dto;

import jakarta.validation.constraints.*;
import com.example.TeamResourceAccessManagement.domain.Project;
import java.util.List;

public class ProjectRequestDTO {
    
    @NotBlank(message = "Project name is required")
    @Size(min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Project status is required")
    private Project.ProjectStatus status;
    
    private Long managerId;
    
    private List<Long> memberIds;
    
    // Constructors
    public ProjectRequestDTO() {}
    
    public ProjectRequestDTO(String name, String description, Project.ProjectStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Project.ProjectStatus getStatus() { return status; }
    public void setStatus(Project.ProjectStatus status) { this.status = status; }
    
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    
    public List<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
}
