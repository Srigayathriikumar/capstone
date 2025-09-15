package com.example.TeamResourceAccessManagement.mapper;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.dto.AccessRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class AccessRequestMapper {
    
    public static AccessRequestDTO toDTO(AccessRequest accessRequest) {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setId(accessRequest.getId());
        
        // User information
        if (accessRequest.getUser() != null) {
            dto.setUserId(accessRequest.getUser().getId());
            dto.setUserName(accessRequest.getUser().getUsername());
        }
        
        // Resource information
        if (accessRequest.getResource() != null) {
            dto.setResourceId(accessRequest.getResource().getId());
            dto.setResourceName(accessRequest.getResource().getName());
        }
        
        // Project information
        if (accessRequest.getProject() != null) {
            dto.setProjectId(accessRequest.getProject().getId());
            dto.setProjectName(accessRequest.getProject().getName());
        }
        
        // Project Manager information
        if (accessRequest.getProjectManager() != null) {
            dto.setProjectManagerId(accessRequest.getProjectManager().getId());
            dto.setProjectManagerName(accessRequest.getProjectManager().getUsername());
        }
        
        dto.setRequestedAccessLevel(accessRequest.getRequestedAccessLevel());
        dto.setStatus(accessRequest.getStatus());
        dto.setJustification(accessRequest.getJustification());
        dto.setApproverComments(accessRequest.getApproverComments());
        
        // Approver information
        if (accessRequest.getApprovedBy() != null) {
            dto.setApprovedById(accessRequest.getApprovedBy().getId());
            dto.setApprovedByName(accessRequest.getApprovedBy().getUsername());
        }
        
        dto.setRequestedAt(accessRequest.getRequestedAt());
        dto.setApprovedAt(accessRequest.getApprovedAt());
        dto.setRequestedUntil(accessRequest.getRequestedUntil());
        
        return dto;
    }
}