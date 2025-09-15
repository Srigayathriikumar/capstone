package com.example.TeamResourceAccessManagement.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.domain.Project;
import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.dto.ResourceRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceAccessUpdateDTO;
import com.example.TeamResourceAccessManagement.mapper.ResourceMapper;
import com.example.TeamResourceAccessManagement.repository.AccessRequestRepository;
import com.example.TeamResourceAccessManagement.repository.PermissionRepository;
import com.example.TeamResourceAccessManagement.repository.ProjectRepository;
import com.example.TeamResourceAccessManagement.repository.ResourceRepository;
import com.example.TeamResourceAccessManagement.repository.UserRepository;

@Service
public class ResourceServiceImpl implements ResourceService {
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Override
    public ResourceResponseDTO createResource(ResourceRequestDTO resourceRequestDTO) {
        Resource resource = ResourceMapper.toEntity(resourceRequestDTO);
        
        // Set defaults if not provided
        if (resource.getCategory() == null) {
            resource.setCategory(Resource.ResourceCategory.OTHER);
        }
        if (resource.getAccessType() == null) {
            resource.setAccessType(Resource.ResourceAccessType.COMMON);
        }
        
        // Set created by current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            resource.setCreatedBy(auth.getName());
            resource.setUploadedBy(auth.getName());
        }
        
        if (!resourceRequestDTO.getIsGlobal() && resourceRequestDTO.getProjectId() != null) {
            Optional<Project> projectOpt = projectRepository.findById(resourceRequestDTO.getProjectId());
            if (projectOpt.isPresent()) {
                resource.setProject(projectOpt.get());
            }
        }
        Resource savedResource = resourceRepository.save(resource);
        
        // Auto-grant permissions for COMMON resources
        if (savedResource.getAccessType() == Resource.ResourceAccessType.COMMON) {
            grantCommonResourcePermissions(savedResource);
        }
        
        return ResourceMapper.toResponse(savedResource);
    }
    
    @Override
    public ResourceResponseDTO createResourceWithFile(MultipartFile file, String name, String description, 
                                                     String type, String category, String accessType, 
                                                     Boolean isGlobal, Long projectId, String resourceUrl, String allowedUserGroups) {
        try {
            // Get file data as byte array
            byte[] fileData = file.getBytes();
            
            // Get file metadata
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // Create resource entity
            Resource resource = new Resource();
            resource.setName(name);
            resource.setDescription(description);
            resource.setType(Resource.ResourceType.valueOf(type));
            resource.setCategory(Resource.ResourceCategory.valueOf(category));
            resource.setAccessType(Resource.ResourceAccessType.valueOf(accessType));
            resource.setIsGlobal(isGlobal);
            resource.setFileData(fileData); // Store file data in database
            resource.setFileSize(file.getSize());
            resource.setMimeType(file.getContentType());
            resource.setFileExtension(fileExtension);
            
            if (resourceUrl != null && !resourceUrl.isEmpty()) {
                resource.setResourceUrl(resourceUrl);
            }
            
            if (allowedUserGroups != null && !allowedUserGroups.isEmpty()) {
                resource.setAllowedUserGroups(allowedUserGroups);
            }
            
            // Set project if not global
            if (!isGlobal && projectId != null) {
                Optional<Project> projectOpt = projectRepository.findById(projectId);
                if (projectOpt.isPresent()) {
                    resource.setProject(projectOpt.get());
                }
            }
            
            // Set created by current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getName() != null) {
                resource.setCreatedBy(auth.getName());
                resource.setUploadedBy(auth.getName());
            }
            
            Resource savedResource = resourceRepository.save(resource);
            
            // Auto-grant permissions for COMMON resources
            if (savedResource.getAccessType() == Resource.ResourceAccessType.COMMON) {
                grantCommonResourcePermissions(savedResource);
            }
            
            return ResourceMapper.toResponse(savedResource);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    private void grantCommonResourcePermissions(Resource resource) {
        // Get all users in the project or all users if global
        List<User> users;
        if (resource.getProject() != null) {
            users = resource.getProject().getUsers().stream().collect(Collectors.toList());
        } else {
            users = userRepository.findAll();
        }
        
        // Grant permissions to all users
        for (User user : users) {
            Permission permission = new Permission();
            permission.setUser(user);
            permission.setResource(resource);
            permission.setAccessLevel(isManagerOrAdmin(user) ? Permission.AccessLevel.ADMIN : Permission.AccessLevel.READ);
            permission.setIsActive(true);
            permission.setGrantedAt(LocalDateTime.now());
            permissionRepository.save(permission);
        }
    }
    
    private boolean isManagerOrAdmin(User user) {
        return user.getRole() == User.UserRole.ADMIN || 
               user.getRole() == User.UserRole.PROJECT_MANAGER ||
               user.getRole() == User.UserRole.TEAMLEAD;
    }
    
    @Override
    public Optional<ResourceResponseDTO> getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
            .map(ResourceMapper::toResponse);
    }
    
    @Override
    public List<ResourceResponseDTO> getAllResources() {
        return resourceRepository.findAll().stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceResponseDTO> getResourcesByType(Resource.ResourceType type) {
        return resourceRepository.findByType(type).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public ResourceResponseDTO updateResource(Long resourceId, ResourceRequestDTO resourceRequestDTO) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (resourceOpt.isPresent()) {
            Resource resource = resourceOpt.get();

            
            Resource savedResource = resourceRepository.save(resource);
            return ResourceMapper.toResponse(savedResource);
        }
        throw new RuntimeException("Resource not found with id: " + resourceId);
    }
    
    @Override
    public void deleteResource(Long resourceId) {
        if (resourceRepository.existsById(resourceId)) {
            resourceRepository.deleteById(resourceId);
        } else {
            throw new RuntimeException("Resource not found with id: " + resourceId);
        }
    }
    
    @Override
    public List<ResourceResponseDTO> getGlobalResources() {
        return resourceRepository.findByIsGlobal(true).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceResponseDTO> getProjectResources(Long projectId) {
        return resourceRepository.findByProjectId(projectId).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceResponseDTO> getAvailableResourcesForProject(Long projectId) {
        return resourceRepository.findAvailableResourcesForProject(projectId).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void makeResourceGlobal(Long resourceId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (resourceOpt.isPresent()) {
            Resource resource = resourceOpt.get();
            resource.setIsGlobal(true);
            resource.setProject(null);
            resourceRepository.save(resource);
        }
    }
    
    @Override
    public void assignResourceToProject(Long resourceId, Long projectId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        
        if (resourceOpt.isPresent() && projectOpt.isPresent()) {
            Resource resource = resourceOpt.get();
            Project project = projectOpt.get();
            resource.setProject(project);
            resource.setIsGlobal(false);
            resourceRepository.save(resource);
        }
    }
    
    @Override
    public void removeResourceFromProject(Long resourceId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (resourceOpt.isPresent()) {
            Resource resource = resourceOpt.get();
            resource.setProject(null);
            resource.setIsGlobal(true);
            resourceRepository.save(resource);
        }
    }
    
    @Override
    public List<Permission> getResourcePermissions(Long resourceId) {
        return permissionRepository.findByResourceId(resourceId);
    }
    
    @Override
    public List<User> getResourceUsers(Long resourceId) {
        List<Permission> permissions = permissionRepository.findByResourceId(resourceId);
        return permissions.stream()
            .filter(Permission::getIsActive)
            .map(Permission::getUser)
            .distinct()
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequest> getResourceAccessRequests(Long resourceId) {
        return accessRequestRepository.findByResourceId(resourceId);
    }
    
    @Override
    public boolean hasUserAccessToResource(Long userId, Long resourceId) {
        // Check explicit permission first
        Optional<Permission> permission = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        if (permission.isPresent() && permission.get().getIsActive() &&
            (permission.get().getExpiresAt() == null || permission.get().getExpiresAt().isAfter(LocalDateTime.now()))) {
            return true;
        }
        
        // Check allowedUserGroups access
        return canUserAccessViaUserGroups(userId, resourceId);
    }
    
    private boolean canUserAccessViaUserGroups(Long userId, Long resourceId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        
        if (userOpt.isPresent() && resourceOpt.isPresent()) {
            User user = userOpt.get();
            Resource resource = resourceOpt.get();
            

            
            String allowedGroups = resource.getAllowedUserGroups();
            if (allowedGroups == null || allowedGroups.trim().isEmpty()) {
                return false;
            }
            
            String username = user.getUsername();
            if (username == null) {
                return false;
            }
            
            // Check if user's domain matches any allowed groups
            String[] groups = allowedGroups.split(",");
            for (String group : groups) {
                group = group.trim();
                if (!group.isEmpty() && username.toLowerCase().contains("." + group.toLowerCase())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public Permission.AccessLevel getUserAccessLevel(Long userId, Long resourceId) {
        Optional<Permission> permission = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        if (permission.isPresent() && permission.get().getIsActive()) {
            return permission.get().getAccessLevel();
        }
        return null;
    }
    
    @Override
    public List<ResourceResponseDTO> searchResourcesByName(String name) {
        return resourceRepository.findByNameContaining(name).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceResponseDTO> getResourcesByUser(Long userId) {
        List<Permission> permissions = permissionRepository.findByUserId(userId);
        return permissions.stream()
            .filter(Permission::getIsActive)
            .map(Permission::getResource)
            .distinct()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceResponseDTO> getAccessibleResourcesForUser(Long userId) {
        return getResourcesByUser(userId);
    }
    
    @Override
    public List<ResourceResponseDTO> getResourcesUserCanRequest(Long userId) {
        List<Resource> allResources = resourceRepository.findAll();
        List<Permission> userPermissions = permissionRepository.findByUserId(userId);
        List<Resource> userResources = userPermissions.stream()
            .filter(Permission::getIsActive)
            .map(Permission::getResource)
            .collect(Collectors.toList());
        
        return allResources.stream()
            .filter(resource -> !userResources.contains(resource))
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isResourceAvailable(Long resourceId) {
        return resourceRepository.existsById(resourceId);
    }
    
    @Override
    public boolean isResourceGlobal(Long resourceId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        return resourceOpt.isPresent() && resourceOpt.get().getIsGlobal();
    }
    
    @Override
    public boolean isResourceInProject(Long resourceId, Long projectId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        return resourceOpt.isPresent() && 
               resourceOpt.get().getProject() != null && 
               resourceOpt.get().getProject().getId().equals(projectId);
    }
    
    @Override
    public long getResourceCount() {
        return resourceRepository.count();
    }
    
    @Override
    public long getGlobalResourceCount() {
        return resourceRepository.findByIsGlobal(true).size();
    }
    
    @Override
    public long getProjectResourceCount(Long projectId) {
        return resourceRepository.findByProjectId(projectId).size();
    }
    
    @Override
    public int getResourceUserCount(Long resourceId) {
        List<Permission> permissions = permissionRepository.findByResourceId(resourceId);
        return (int) permissions.stream()
            .filter(Permission::getIsActive)
            .map(Permission::getUser)
            .distinct()
            .count();
    }
    
    @Override
    public int getResourcePermissionCount(Long resourceId) {
        return permissionRepository.findByResourceId(resourceId).size();
    }
    
    @Override
    public boolean resourceExists(Long resourceId) {
        return resourceRepository.existsById(resourceId);
    }
    
    @Override
    public boolean canUserAccessResource(Long userId, Long resourceId) {
        return hasUserAccessToResource(userId, resourceId);
    }
    
    @Override
    public boolean isResourceNameUnique(String name) {
        return resourceRepository.findByNameContaining(name).stream()
            .noneMatch(resource -> resource.getName().equals(name));
    }
    

    
    @Override
    public List<ResourceResponseDTO> getResourcesByCategory(Resource.ResourceCategory category) {
        return resourceRepository.findByCategory(category).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ResourceResponseDTO> getProjectResourcesByCategory(Long projectId, Resource.ResourceCategory category) {
        return resourceRepository.findByProjectIdAndCategory(projectId, category).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    // @Override
    // public List<ResourceResponseDTO> getProjectResourcesByAccessType(Long projectId, Resource.ResourceAccessType accessType) {
    //     return resourceRepository.findByProjectIdAndAccessType(projectId, accessType).stream()
    //         .map(ResourceMapper::toResponse)
    //         .collect(Collectors.toList());
    // }
    
    @Override
    public List<ResourceResponseDTO> getProjectResourcesByAccessType(Long projectId, Resource.ResourceAccessType accessType) {
        return resourceRepository.findByProjectIdAndAccessType(projectId, accessType).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    public void updateExistingResourcesWithUserGroups() {
        List<Resource> managerResources = resourceRepository.findAll().stream()
            .filter(r -> r.getAccessType() == Resource.ResourceAccessType.MANAGER_CONTROLLED)
            .filter(r -> r.getAllowedUserGroups() == null || r.getAllowedUserGroups().isEmpty())
            .collect(Collectors.toList());
        
        for (Resource resource : managerResources) {
            resource.setAllowedUserGroups("dev,test,QA");
            resourceRepository.save(resource);
        }
    }
    
    @Override
    public ResourceResponseDTO updateResourceAccessSettings(Long resourceId, ResourceAccessUpdateDTO accessUpdateDTO) {
        try {
            Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
            if (resourceOpt.isEmpty()) {
                throw new RuntimeException("Resource not found with id: " + resourceId);
            }
            
            Resource resource = resourceOpt.get();
            
            // Verify user owns this resource
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth != null ? auth.getName() : null;
            
            if (currentUser == null || 
                (!currentUser.equals(resource.getCreatedBy()) && !currentUser.equals(resource.getUploadedBy()))) {
                throw new RuntimeException("You can only modify access settings for resources you uploaded");
            }
            
            // Update access settings
            if (accessUpdateDTO.getAccessType() != null) {
                resource.setAccessType(accessUpdateDTO.getAccessType());
            }
            
            resource.setAllowedUserGroups(accessUpdateDTO.getAllowedUserGroups());
            
            Resource savedResource = resourceRepository.save(resource);
            return ResourceMapper.toResponse(savedResource);
        } catch (Exception e) {
            System.err.println("Error updating resource access settings: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update access settings: " + e.getMessage());
        }
    }
}