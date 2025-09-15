package com.example.TeamResourceAccessManagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.dto.ResourceRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceAccessUpdateDTO;

public interface ResourceService {
    
    // Resource CRUD Operations
    ResourceResponseDTO createResource(ResourceRequestDTO resourceRequestDTO);
    ResourceResponseDTO createResourceWithFile(MultipartFile file, String name, String description, 
                                             String type, String category, String accessType, 
                                             Boolean isGlobal, Long projectId, String resourceUrl, String allowedUserGroups);
    Optional<ResourceResponseDTO> getResourceById(Long resourceId);
    List<ResourceResponseDTO> getAllResources();
    List<ResourceResponseDTO> getResourcesByType(Resource.ResourceType type);
    ResourceResponseDTO updateResource(Long resourceId, ResourceRequestDTO resourceRequestDTO);
    void deleteResource(Long resourceId);
    
    // Global vs Project Resources
    List<ResourceResponseDTO> getGlobalResources();
    List<ResourceResponseDTO> getProjectResources(Long projectId);
    List<ResourceResponseDTO> getAvailableResourcesForProject(Long projectId);
    void makeResourceGlobal(Long resourceId);
    void assignResourceToProject(Long resourceId, Long projectId);
    void removeResourceFromProject(Long resourceId);
    
    // Resource Access Management
    List<Permission> getResourcePermissions(Long resourceId);
    List<User> getResourceUsers(Long resourceId);
    List<AccessRequest> getResourceAccessRequests(Long resourceId);
    boolean hasUserAccessToResource(Long userId, Long resourceId);
    Permission.AccessLevel getUserAccessLevel(Long userId, Long resourceId);
    
    // Resource Search Operations
    List<ResourceResponseDTO> searchResourcesByName(String name);
    List<ResourceResponseDTO> getResourcesByUser(Long userId);
    List<ResourceResponseDTO> getAccessibleResourcesForUser(Long userId);
    List<ResourceResponseDTO> getResourcesUserCanRequest(Long userId);
    
    // Resource Status Operations
    boolean isResourceAvailable(Long resourceId);
    boolean isResourceGlobal(Long resourceId);
    boolean isResourceInProject(Long resourceId, Long projectId);
    
    // Resource Statistics
    long getResourceCount();
    long getGlobalResourceCount();
    long getProjectResourceCount(Long projectId);
    int getResourceUserCount(Long resourceId);
    int getResourcePermissionCount(Long resourceId);
    
    // Resource Validation
    boolean resourceExists(Long resourceId);
    boolean canUserAccessResource(Long userId, Long resourceId);
    boolean isResourceNameUnique(String name);
    
    // Category and access type filtering
    List<ResourceResponseDTO> getResourcesByCategory(Resource.ResourceCategory category);
    List<ResourceResponseDTO> getProjectResourcesByCategory(Long projectId, Resource.ResourceCategory category);
    List<ResourceResponseDTO> getProjectResourcesByAccessType(Long projectId, Resource.ResourceAccessType accessType);
    
    // Resource access settings update
    ResourceResponseDTO updateResourceAccessSettings(Long resourceId, ResourceAccessUpdateDTO accessUpdateDTO);
}