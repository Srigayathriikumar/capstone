package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.Project;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.dto.ProjectRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ProjectResponseDTO;
import java.util.List;
import java.util.Optional;

public interface ProjectService {
    
    // Project CRUD Operations
    ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO);
    Optional<ProjectResponseDTO> getProjectById(Long projectId);
    List<ProjectResponseDTO> getAllProjects();
    List<ProjectResponseDTO> getProjectsByStatus(Project.ProjectStatus status);
    List<ProjectResponseDTO> getActiveProjects();
    ProjectResponseDTO updateProject(Long projectId, ProjectRequestDTO projectRequestDTO);
    void deleteProject(Long projectId);
    
    // User-Project Operations
    List<ProjectResponseDTO> getProjectsByUser(Long userId);
    List<ProjectResponseDTO> getProjectsByUsername(String username);
    void addUserToProject(Long projectId, Long userId);
    void removeUserFromProject(Long projectId, Long userId);
    List<com.example.TeamResourceAccessManagement.dto.UserResponseDTO> getProjectUsers(Long projectId);
    void addMultipleUsersToProject(Long projectId, List<Long> userIds);
    void removeMultipleUsersFromProject(Long projectId, List<Long> userIds);
    
    // Resource-Project Operations
    List<com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO> getProjectResources(Long projectId);
    List<Resource> getAvailableResourcesForProject(Long projectId);
    void assignResourceToProject(Long projectId, Long resourceId);
    void removeResourceFromProject(Long resourceId);
    
    // Project Status Operations
    void activateProject(Long projectId);
    void deactivateProject(Long projectId);
    void completeProject(Long projectId);
    void archiveProject(Long projectId);
    
    // Project Search Operations
    List<ProjectResponseDTO> searchProjectsByName(String name);
    boolean isUserInProject(Long userId, Long projectId);
    boolean hasProjectAccess(Long userId, Long projectId);
    
    // Project Statistics
    long getProjectCount();
    long getActiveProjectCount();
    int getProjectUserCount(Long projectId);
    int getProjectResourceCount(Long projectId);
    
    // Project Validation
    boolean projectExists(Long projectId);
    boolean isProjectActive(Long projectId);
}