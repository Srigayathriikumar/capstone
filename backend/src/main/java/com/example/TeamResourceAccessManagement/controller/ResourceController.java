package com.example.TeamResourceAccessManagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.dto.ResourceRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceAccessUpdateDTO;
import com.example.TeamResourceAccessManagement.exceptions.ResourceNotFoundException;
import com.example.TeamResourceAccessManagement.service.ResourceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResourceResponseDTO> createResource(@RequestBody ResourceRequestDTO resourceRequestDTO) {
        try {
            System.out.println("Creating resource: " + resourceRequestDTO.getName());
            System.out.println("Resource type: " + resourceRequestDTO.getType());
            System.out.println("Resource category: " + resourceRequestDTO.getCategory());
            System.out.println("Resource accessType: " + resourceRequestDTO.getAccessType());
            
            ResourceResponseDTO resource = resourceService.createResource(resourceRequestDTO);
            return new ResponseEntity<>(resource, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating resource: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<ResourceResponseDTO> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("type") String type,
            @RequestParam("category") String category,
            @RequestParam("accessType") String accessType,
            @RequestParam("isGlobal") String isGlobal,
            @RequestParam("projectId") String projectId,
            @RequestParam(value = "resourceUrl", required = false) String resourceUrl,
            @RequestParam(value = "allowedUserGroups", required = false) String allowedUserGroups) {
        try {
            System.out.println("Uploading file: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());
            
            ResourceResponseDTO resource = resourceService.createResourceWithFile(
                file, name, description, type, category, accessType, 
                Boolean.parseBoolean(isGlobal), Long.parseLong(projectId), resourceUrl, allowedUserGroups);
            
            return new ResponseEntity<>(resource, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error uploading resource: " + e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        try {
            Optional<ResourceResponseDTO> resourceOpt = resourceService.getResourceById(id);
            if (resourceOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            ResourceResponseDTO resource = resourceOpt.get();
            if (resource.getFileData() == null || resource.getFileData().length == 0) {
                return ResponseEntity.badRequest().build();
            }
            
            String contentType = resource.getMimeType() != null ? 
                resource.getMimeType() : "application/octet-stream";
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "inline; filename=\"" + resource.getName() + "\"")
                .body(resource.getFileData());
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponseDTO> getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
            .map(resource -> ResponseEntity.ok(resource))
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponseDTO>> getAllResources() {
        List<ResourceResponseDTO> resources = resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResourceResponseDTO>> getResourcesByType(@PathVariable Resource.ResourceType type) {
        List<ResourceResponseDTO> resources = resourceService.getResourcesByType(type);
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponseDTO> updateResource(@PathVariable Long id, @Valid @RequestBody ResourceRequestDTO resourceRequestDTO) {
        ResourceResponseDTO resource = resourceService.updateResource(id, resourceRequestDTO);
        if (resource != null) {
            return ResponseEntity.ok(resource);
        }
        throw new ResourceNotFoundException("Resource not found with id: " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/global")
    public ResponseEntity<List<ResourceResponseDTO>> getGlobalResources() {
        List<ResourceResponseDTO> resources = resourceService.getGlobalResources();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ResourceResponseDTO>> getProjectResources(@PathVariable Long projectId) {
        List<ResourceResponseDTO> resources = resourceService.getProjectResources(projectId);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/project/{projectId}/common")
    public ResponseEntity<List<ResourceResponseDTO>> getCommonResources(@PathVariable Long projectId) {
        List<ResourceResponseDTO> resources = resourceService.getProjectResourcesByAccessType(projectId, Resource.ResourceAccessType.COMMON);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/project/{projectId}/manager-controlled")
    public ResponseEntity<List<ResourceResponseDTO>> getManagerControlledResources(@PathVariable Long projectId) {
        List<ResourceResponseDTO> resources = resourceService.getProjectResourcesByAccessType(projectId, Resource.ResourceAccessType.MANAGER_CONTROLLED);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/available/project/{projectId}")
    public ResponseEntity<List<ResourceResponseDTO>> getAvailableResourcesForProject(@PathVariable Long projectId) {
        List<ResourceResponseDTO> resources = resourceService.getAvailableResourcesForProject(projectId);
        return ResponseEntity.ok(resources);
    }

    @PutMapping("/{id}/make-global")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> makeResourceGlobal(@PathVariable Long id) {
        resourceService.makeResourceGlobal(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{resourceId}/assign-project/{projectId}")
    public ResponseEntity<Void> assignResourceToProject(@PathVariable Long resourceId, @PathVariable Long projectId) {
        resourceService.assignResourceToProject(resourceId, projectId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/remove-from-project")
    public ResponseEntity<Void> removeResourceFromProject(@PathVariable Long id) {
        resourceService.removeResourceFromProject(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ResourceResponseDTO>> searchResourcesByName(@RequestParam String name) {
        List<ResourceResponseDTO> resources = resourceService.searchResourcesByName(name);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResourceResponseDTO>> getResourcesByUser(@PathVariable Long userId) {
        List<ResourceResponseDTO> resources = resourceService.getResourcesByUser(userId);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/accessible/user/{userId}")
    public ResponseEntity<List<ResourceResponseDTO>> getAccessibleResourcesForUser(@PathVariable Long userId) {
        List<ResourceResponseDTO> resources = resourceService.getAccessibleResourcesForUser(userId);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/requestable/user/{userId}")
    public ResponseEntity<List<ResourceResponseDTO>> getResourcesUserCanRequest(@PathVariable Long userId) {
        List<ResourceResponseDTO> resources = resourceService.getResourcesUserCanRequest(userId);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{resourceId}/access/{userId}")
    public ResponseEntity<Boolean> hasUserAccessToResource(@PathVariable Long resourceId, @PathVariable Long userId) {
        boolean hasAccess = resourceService.hasUserAccessToResource(userId, resourceId);
        return ResponseEntity.ok(hasAccess);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getResourceCount() {
        long count = resourceService.getResourceCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/global")
    public ResponseEntity<Long> getGlobalResourceCount() {
        long count = resourceService.getGlobalResourceCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/project/{projectId}")
    public ResponseEntity<Long> getProjectResourceCount(@PathVariable Long projectId) {
        long count = resourceService.getProjectResourceCount(projectId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{resourceId}/available")
    public ResponseEntity<Boolean> isResourceAvailable(@PathVariable Long resourceId) {
        boolean isAvailable = resourceService.isResourceAvailable(resourceId);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/{resourceId}/global")
    public ResponseEntity<Boolean> isResourceGlobal(@PathVariable Long resourceId) {
        boolean isGlobal = resourceService.isResourceGlobal(resourceId);
        return ResponseEntity.ok(isGlobal);
    }

    @GetMapping("/{resourceId}/project/{projectId}/member")
    public ResponseEntity<Boolean> isResourceInProject(@PathVariable Long resourceId, @PathVariable Long projectId) {
        boolean isInProject = resourceService.isResourceInProject(resourceId, projectId);
        return ResponseEntity.ok(isInProject);
    }

    @GetMapping("/{resourceId}/count/users")
    public ResponseEntity<Integer> getResourceUserCount(@PathVariable Long resourceId) {
        int count = resourceService.getResourceUserCount(resourceId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{resourceId}/count/permissions")
    public ResponseEntity<Integer> getResourcePermissionCount(@PathVariable Long resourceId) {
        int count = resourceService.getResourcePermissionCount(resourceId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{resourceId}/exists")
    public ResponseEntity<Boolean> resourceExists(@PathVariable Long resourceId) {
        boolean exists = resourceService.resourceExists(resourceId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{resourceId}/user/{userId}/can-access")
    public ResponseEntity<Boolean> canUserAccessResource(@PathVariable Long resourceId, @PathVariable Long userId) {
        boolean canAccess = resourceService.canUserAccessResource(userId, resourceId);
        return ResponseEntity.ok(canAccess);
    }


    
    @GetMapping("/project/{projectId}/category/{category}")
    public ResponseEntity<List<ResourceResponseDTO>> getProjectResourcesByCategory(@PathVariable Long projectId, @PathVariable Resource.ResourceCategory category) {
        List<ResourceResponseDTO> resources = resourceService.getProjectResourcesByCategory(projectId, category);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/project/{projectId}/access-type/{accessType}")
    public ResponseEntity<List<ResourceResponseDTO>> getProjectResourcesByAccessType(@PathVariable Long projectId, @PathVariable Resource.ResourceAccessType accessType) {
        List<ResourceResponseDTO> resources = resourceService.getProjectResourcesByAccessType(projectId, accessType);
        return ResponseEntity.ok(resources);
    }
    
    @PostMapping("/update-user-groups")
    public ResponseEntity<String> updateExistingResourcesWithUserGroups() {
        ((com.example.TeamResourceAccessManagement.service.ResourceServiceImpl) resourceService).updateExistingResourcesWithUserGroups();
        return ResponseEntity.ok("Updated existing manager-controlled resources with user groups: dev, test, QA");
    }
    
    @PutMapping("/{id}/access-settings")
    public ResponseEntity<ResourceResponseDTO> updateResourceAccessSettings(
            @PathVariable Long id, 
            @RequestBody ResourceAccessUpdateDTO accessUpdateDTO) {
        ResourceResponseDTO resource = resourceService.updateResourceAccessSettings(id, accessUpdateDTO);
        return ResponseEntity.ok(resource);
    }
}