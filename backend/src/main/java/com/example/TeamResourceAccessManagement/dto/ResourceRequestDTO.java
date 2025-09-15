package com.example.TeamResourceAccessManagement.dto;

import com.example.TeamResourceAccessManagement.domain.Resource;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResourceRequestDTO {
    
    @NotBlank(message = "Resource name is required")
    @Size(min = 2, max = 100, message = "Resource name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Resource type is required")
    private Resource.ResourceType type;
    
    private Resource.ResourceCategory category = Resource.ResourceCategory.OTHER;
    
    private Resource.ResourceAccessType accessType = Resource.ResourceAccessType.COMMON;
    
    @Size(max = 1000, message = "Resource URL must not exceed 1000 characters")
    @Pattern(regexp = "^(https?://.*|/.*|ftp://.*|file://.*)?$", message = "Invalid URL format")
    private String resourceUrl;
    
    @Size(max = 1000, message = "File path must not exceed 1000 characters")
    private String filePath;
    
    @Min(value = 0, message = "File size must be non-negative")
    private Long fileSize;
    
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    private String mimeType;
    
    @Size(max = 20, message = "File extension must not exceed 20 characters")
    private String fileExtension;
    
    private byte[] fileData;
    
    @NotNull(message = "Global flag is required")
    private Boolean isGlobal;
    
    // Project ID for non-global resources (handled in service layer)
    private Long projectId;
    
    @Size(max = 500, message = "Allowed user groups must not exceed 500 characters")
    private String allowedUserGroups;
    
    // Constructors
    public ResourceRequestDTO() {}
    
    public ResourceRequestDTO(String name, String description, Resource.ResourceType type, String resourceUrl, Boolean isGlobal, Long projectId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.resourceUrl = resourceUrl;
        this.isGlobal = isGlobal;
        this.projectId = projectId;
    }
    
    // Getters and Setters
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
    
    public String getAllowedUserGroups() { return allowedUserGroups; }
    public void setAllowedUserGroups(String allowedUserGroups) { this.allowedUserGroups = allowedUserGroups; }
}
