package com.example.TeamResourceAccessManagement.dto;

import java.time.LocalDateTime;

import com.example.TeamResourceAccessManagement.domain.Resource;

public class ResourceResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private Resource.ResourceType type;
    private Resource.ResourceCategory category;
    private Resource.ResourceAccessType accessType;
    private String resourceUrl;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private String fileExtension;
    private byte[] fileData;
    private Boolean isGlobal;
    private Long projectId;
    private String projectName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String uploadedBy;
    private String allowedUserGroups;
    
    // Constructors
    public ResourceResponseDTO() {}
    
    public ResourceResponseDTO(Long id, String name, String description, Resource.ResourceType type, Resource.ResourceCategory category, Resource.ResourceAccessType accessType, String resourceUrl, String filePath, Long fileSize, String mimeType, String fileExtension, Boolean isGlobal, Long projectId, String projectName, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String uploadedBy, String allowedUserGroups) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.category = category;
        this.accessType = accessType;
        this.resourceUrl = resourceUrl;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
        this.isGlobal = isGlobal;
        this.projectId = projectId;
        this.projectName = projectName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.uploadedBy = uploadedBy;
        this.allowedUserGroups = allowedUserGroups;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Resource.ResourceType getType() { return type; }
    public void setType(Resource.ResourceType type) { this.type = type; }
    
    public Resource.ResourceCategory getCategory() { return category; }
    public void setCategory(Resource.ResourceCategory category) { this.category = category; }
    
    public Resource.ResourceAccessType getAccessType() { return accessType; }
    public void setAccessType(Resource.ResourceAccessType accessType) { this.accessType = accessType; }
    
    public String getResourceUrl() { return resourceUrl; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getFileExtension() { return fileExtension; }
    public void setFileExtension(String fileExtension) { this.fileExtension = fileExtension; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    
    public Boolean getIsGlobal() { return isGlobal; }
    public void setIsGlobal(Boolean isGlobal) { this.isGlobal = isGlobal; }
    
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public String getAllowedUserGroups() { return allowedUserGroups; }
    public void setAllowedUserGroups(String allowedUserGroups) { this.allowedUserGroups = allowedUserGroups; }
}
