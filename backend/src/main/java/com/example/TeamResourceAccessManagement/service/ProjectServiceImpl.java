package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.Project;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.dto.ProjectRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ProjectResponseDTO;
import com.example.TeamResourceAccessManagement.dto.UserResponseDTO;
import com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO;
import com.example.TeamResourceAccessManagement.mapper.ProjectMapper;
import com.example.TeamResourceAccessManagement.mapper.UserMapper;
import com.example.TeamResourceAccessManagement.mapper.ResourceMapper;
import com.example.TeamResourceAccessManagement.repository.ProjectRepository;
import com.example.TeamResourceAccessManagement.repository.UserRepository;
import com.example.TeamResourceAccessManagement.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO) {
        Project project = ProjectMapper.toEntity(projectRequestDTO);
        Project savedProject = projectRepository.save(project);
        return ProjectMapper.toResponse(savedProject);
    }
    
    @Override
    public Optional<ProjectResponseDTO> getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
            .map(ProjectMapper::toResponse);
    }
    
    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAllWithUsersAndResources().stream()
            .map(ProjectMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ProjectResponseDTO> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status).stream()
            .map(ProjectMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ProjectResponseDTO> getActiveProjects() {
        return projectRepository.findByStatus(Project.ProjectStatus.ACTIVE).stream()
            .map(ProjectMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public ProjectResponseDTO updateProject(Long projectId, ProjectRequestDTO projectRequestDTO) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setName(projectRequestDTO.getName());
            project.setDescription(projectRequestDTO.getDescription());
            project.setStatus(projectRequestDTO.getStatus());
            Project savedProject = projectRepository.save(project);
            return ProjectMapper.toResponse(savedProject);
        }
        return null;
    }
    
    @Override
    public void deleteProject(Long projectId) {
        projectRepository.deleteById(projectId);
    }
    
    @Override
    public List<ProjectResponseDTO> getProjectsByUser(Long userId) {
        return projectRepository.findByUserId(userId).stream()
            .map(ProjectMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void addUserToProject(Long projectId, Long userId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (projectOpt.isPresent() && userOpt.isPresent()) {
            Project project = projectOpt.get();
            User user = userOpt.get();
            project.getUsers().add(user);
            projectRepository.save(project);
        }
    }
    
    @Override
    public void removeUserFromProject(Long projectId, Long userId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (projectOpt.isPresent() && userOpt.isPresent()) {
            Project project = projectOpt.get();
            User user = userOpt.get();
            project.getUsers().remove(user);
            projectRepository.save(project);
        }
    }
    
    @Override
    public List<UserResponseDTO> getProjectUsers(Long projectId) {
        return userRepository.findByProjectId(projectId).stream()
            .map(UserMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public void addMultipleUsersToProject(Long projectId, List<Long> userIds) {
        userIds.forEach(userId -> addUserToProject(projectId, userId));
    }
    
    @Override
    public void removeMultipleUsersFromProject(Long projectId, List<Long> userIds) {
        userIds.forEach(userId -> removeUserFromProject(projectId, userId));
    }
    
    @Override
    public List<ResourceResponseDTO> getProjectResources(Long projectId) {
        return resourceRepository.findByProjectId(projectId).stream()
            .map(ResourceMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Resource> getAvailableResourcesForProject(Long projectId) {
        return resourceRepository.findAvailableResourcesForProject(projectId);
    }
    
    @Override
    public void assignResourceToProject(Long projectId, Long resourceId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        
        if (projectOpt.isPresent() && resourceOpt.isPresent()) {
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
            resourceRepository.save(resource);
        }
    }
    
    @Override
    public void activateProject(Long projectId) {
        updateProjectStatus(projectId, Project.ProjectStatus.ACTIVE);
    }
    
    @Override
    public void deactivateProject(Long projectId) {
        updateProjectStatus(projectId, Project.ProjectStatus.INACTIVE);
    }
    
    @Override
    public void completeProject(Long projectId) {
        updateProjectStatus(projectId, Project.ProjectStatus.COMPLETED);
    }
    
    @Override
    public void archiveProject(Long projectId) {
        updateProjectStatus(projectId, Project.ProjectStatus.ARCHIVED);
    }
    
    private void updateProjectStatus(Long projectId, Project.ProjectStatus status) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setStatus(status);
            project.setUpdatedAt(LocalDateTime.now());
            projectRepository.save(project);
        }
    }
    
    @Override
    public List<ProjectResponseDTO> searchProjectsByName(String name) {
        return projectRepository.findByNameContaining(name).stream()
            .map(ProjectMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isUserInProject(Long userId, Long projectId) {
        List<Project> userProjects = projectRepository.findByUserId(userId);
        return userProjects.stream().anyMatch(project -> project.getId().equals(projectId));
    }
    
    @Override
    public boolean hasProjectAccess(Long userId, Long projectId) {
        return isUserInProject(userId, projectId);
    }
    
    @Override
    public long getProjectCount() {
        return projectRepository.count();
    }
    
    @Override
    public long getActiveProjectCount() {
        return projectRepository.findByStatus(Project.ProjectStatus.ACTIVE).size();
    }
    
    @Override
    public int getProjectUserCount(Long projectId) {
        List<User> users = userRepository.findByProjectId(projectId);
        return users.size();
    }
    
    @Override
    public int getProjectResourceCount(Long projectId) {
        List<Resource> resources = resourceRepository.findByProjectId(projectId);
        return resources.size();
    }
    
    @Override
    public boolean projectExists(Long projectId) {
        return projectRepository.existsById(projectId);
    }
    
    @Override
    public boolean isProjectActive(Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        return projectOpt.isPresent() && projectOpt.get().getStatus() == Project.ProjectStatus.ACTIVE;
    }
    
    @Override
    public List<ProjectResponseDTO> getProjectsByUsername(String username) {
        try {
            // For superadmin and admin users, return all projects
            if ("superadmin".equals(username) || "john.admin".equals(username)) {
                return getAllProjects();
            }
            
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                return getProjectsByUser(userOpt.get().getId());
            }
            
            // If user not found in database, return empty list
            return List.of();
        } catch (Exception e) {
            System.err.println("Error in getProjectsByUsername: " + e.getMessage());
            e.printStackTrace();
            // Return empty list on error instead of all projects
            return List.of();
        }
    }
}