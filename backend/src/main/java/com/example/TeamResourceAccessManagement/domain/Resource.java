package com.example.TeamResourceAccessManagement.domain;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "resources",
       indexes = {
           @Index(name = "idx_resource_name", columnList = "name"),
           @Index(name = "idx_resource_type", columnList = "type"),
           @Index(name = "idx_resource_project_id", columnList = "project_id"),
           @Index(name = "idx_resource_is_global", columnList = "is_global")
       })
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ResourceType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30)
    private ResourceCategory category;
    

    
    @Column(name = "resource_url", length = 1000)
    private String resourceUrl;
    
    @Column(name = "file_path", length = 1000)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @Column(name = "file_extension", length = 20)
    private String fileExtension;
    
    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;
    
    @Column(name = "is_global", nullable = false)
    private Boolean isGlobal = false;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "access_type", length = 20)
    private ResourceAccessType accessType;
    
    @Column(name = "allowed_user_groups", length = 500)
    private String allowedUserGroups;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", foreignKey = @ForeignKey(name = "fk_resource_project"))
    private Project project; 
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Permission> permissions;
    
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AccessRequest> accessRequests;

    public enum ResourceType {
        // Document Types
        PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT, RTF, ODT, ODS, ODP,
        
        // Image Types
        JPG, JPEG, PNG, GIF, BMP, TIFF, SVG, WEBP, ICO,
        
        // Video Types
        MP4, AVI, MOV, WMV, FLV, WEBM, MKV, MPEG, MPG,
        
        // Audio Types
        MP3, WAV, FLAC, AAC, OGG, WMA, M4A,
        
        // Archive Types
        ZIP, RAR, TAR, GZ, BZ2, SEVEN_Z,
        
        // Code/Development
        JAVA, JAVASCRIPT, PYTHON, HTML, CSS, XML, JSON, YAML, SQL, SH, BAT,
        
        // Database & API
        DATABASE, API, REST_API, GRAPHQL_API,
        
        // Cloud & External
        GITHUB_LINK, GITLAB_LINK, BITBUCKET_LINK, URL, CLOUD_SERVICE, AWS_S3, GOOGLE_DRIVE, DROPBOX,
        
        // Other
        OTHER
    }
    

    
    public enum ResourceAccessType {
        MANAGER_CONTROLLED, COMMON
    }
    
    public enum ResourceCategory {
        DATABASE("Database Resources"),
        API("API Resources"), 
        DOCUMENTATION("Documentation"),
        REPOSITORY("Code Repositories"),
        EXTERNAL_LINKS("External Links"),
        CLOUD_SERVICES("Cloud Services"),
        DOCUMENTS("Documents"),
        IMAGES("Images"),
        VIDEOS("Videos"),
        AUDIO("Audio Files"),
        ARCHIVES("Archive Files"),
        CODE_FILES("Code Files"),
        MEDIA("Media Files"),
        OTHER("Other Resources");
        
        private final String displayName;
        
        ResourceCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Resource() {}
    
    public Resource(String name, String description, ResourceType type, Boolean isGlobal, Project project) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.isGlobal = isGlobal;
        this.project = project;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }
    
    public ResourceCategory getCategory() { return category; }
    public void setCategory(ResourceCategory category) { this.category = category; }
    
    public ResourceAccessType getAccessType() { return accessType; }
    public void setAccessType(ResourceAccessType accessType) { this.accessType = accessType; }
    

    
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
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
    
    public Set<AccessRequest> getAccessRequests() { return accessRequests; }
    public void setAccessRequests(Set<AccessRequest> accessRequests) { this.accessRequests = accessRequests; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    
    public String getAllowedUserGroups() { return allowedUserGroups; }
    public void setAllowedUserGroups(String allowedUserGroups) { this.allowedUserGroups = allowedUserGroups; }
}