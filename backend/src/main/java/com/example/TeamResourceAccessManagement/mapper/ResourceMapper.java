package com.example.TeamResourceAccessManagement.mapper;

import org.springframework.stereotype.Component;

import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.dto.ResourceRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO;

@Component
public class ResourceMapper {
    
    public static ResourceResponseDTO toResponse(Resource resource) {
        ResourceResponseDTO resourceResponseDTO = new ResourceResponseDTO();
        resourceResponseDTO.setId(resource.getId());
        resourceResponseDTO.setName(resource.getName());
        resourceResponseDTO.setDescription(resource.getDescription());
        resourceResponseDTO.setType(resource.getType());
        resourceResponseDTO.setCategory(resource.getCategory() != null ? resource.getCategory() : Resource.ResourceCategory.OTHER);
        resourceResponseDTO.setAccessType(resource.getAccessType() != null ? resource.getAccessType() : Resource.ResourceAccessType.COMMON);
        resourceResponseDTO.setResourceUrl(resource.getResourceUrl());
        resourceResponseDTO.setFilePath(resource.getFilePath());
        resourceResponseDTO.setFileSize(resource.getFileSize());
        resourceResponseDTO.setMimeType(resource.getMimeType());
        resourceResponseDTO.setFileExtension(resource.getFileExtension());
        resourceResponseDTO.setFileData(resource.getFileData());
        resourceResponseDTO.setIsGlobal(resource.getIsGlobal());
        resourceResponseDTO.setProjectId(resource.getProject() != null ? resource.getProject().getId() : null);
        resourceResponseDTO.setProjectName(resource.getProject() != null ? resource.getProject().getName() : null);
        resourceResponseDTO.setCreatedAt(resource.getCreatedAt());
        resourceResponseDTO.setUpdatedAt(resource.getUpdatedAt());
        resourceResponseDTO.setCreatedBy(resource.getCreatedBy());
        resourceResponseDTO.setUploadedBy(resource.getUploadedBy());
        resourceResponseDTO.setAllowedUserGroups(resource.getAllowedUserGroups());
        return resourceResponseDTO;
    }

    public static Resource toEntity(ResourceRequestDTO resourceRequestDTO) {
        Resource resource = new Resource();
        resource.setName(resourceRequestDTO.getName());
        resource.setDescription(resourceRequestDTO.getDescription());
        resource.setType(resourceRequestDTO.getType());
        resource.setCategory(resourceRequestDTO.getCategory() != null ? resourceRequestDTO.getCategory() : Resource.ResourceCategory.OTHER);
        resource.setAccessType(resourceRequestDTO.getAccessType() != null ? resourceRequestDTO.getAccessType() : Resource.ResourceAccessType.COMMON);
        resource.setResourceUrl(resourceRequestDTO.getResourceUrl());
        resource.setFilePath(resourceRequestDTO.getFilePath());
        resource.setFileSize(resourceRequestDTO.getFileSize());
        resource.setMimeType(resourceRequestDTO.getMimeType());
        resource.setFileExtension(resourceRequestDTO.getFileExtension());
        resource.setFileData(resourceRequestDTO.getFileData());
        resource.setIsGlobal(resourceRequestDTO.getIsGlobal());
        resource.setAllowedUserGroups(resourceRequestDTO.getAllowedUserGroups());
        return resource;
    }
}