package com.example.TeamResourceAccessManagement.controller;

import com.example.TeamResourceAccessManagement.dto.ProjectRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ProjectResponseDTO;
import com.example.TeamResourceAccessManagement.domain.Project;
import com.example.TeamResourceAccessManagement.service.ProjectService;
import com.example.TeamResourceAccessManagement.exceptions.ProjectNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectResponseDTO project = projectService.createProject(projectRequestDTO);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
            .map(project -> ResponseEntity.ok(project))
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        List<ProjectResponseDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByStatus(@PathVariable Project.ProjectStatus status) {
        List<ProjectResponseDTO> projects = projectService.getProjectsByStatus(status);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProjectResponseDTO>> getActiveProjects() {
        List<ProjectResponseDTO> projects = projectService.getActiveProjects();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectResponseDTO project = projectService.updateProject(id, projectRequestDTO);
        if (project != null) {
            return ResponseEntity.ok(project);
        }
        throw new ProjectNotFoundException("Project not found with id: " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByUser(@PathVariable Long userId) {
        List<ProjectResponseDTO> projects = projectService.getProjectsByUser(userId);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> addUserToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.addUserToProject(projectId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromProject(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/resources/{resourceId}")
    public ResponseEntity<Void> assignResourceToProject(@PathVariable Long projectId, @PathVariable Long resourceId) {
        projectService.assignResourceToProject(projectId, resourceId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> activateProject(@PathVariable Long id) {
        projectService.activateProject(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deactivateProject(@PathVariable Long id) {
        projectService.deactivateProject(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> completeProject(@PathVariable Long id) {
        projectService.completeProject(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> archiveProject(@PathVariable Long id) {
        projectService.archiveProject(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectResponseDTO>> searchProjectsByName(@RequestParam String name) {
        List<ProjectResponseDTO> projects = projectService.searchProjectsByName(name);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getProjectCount() {
        long count = projectService.getProjectCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveProjectCount() {
        long count = projectService.getActiveProjectCount();
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{resourceId}/remove-from-project")
    public ResponseEntity<Void> removeResourceFromProject(@PathVariable Long resourceId) {
        projectService.removeResourceFromProject(resourceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/users/bulk-add")
    public ResponseEntity<Void> addMultipleUsersToProject(@PathVariable Long projectId, @Valid @RequestBody List<Long> userIds) {
        projectService.addMultipleUsersToProject(projectId, userIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/users/bulk-remove")
    public ResponseEntity<Void> removeMultipleUsersFromProject(@PathVariable Long projectId, @Valid @RequestBody List<Long> userIds) {
        projectService.removeMultipleUsersFromProject(projectId, userIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{projectId}/user/{userId}/member")
    public ResponseEntity<Boolean> isUserInProject(@PathVariable Long projectId, @PathVariable Long userId) {
        boolean isMember = projectService.isUserInProject(userId, projectId);
        return ResponseEntity.ok(isMember);
    }

    @GetMapping("/{projectId}/user/{userId}/access")
    public ResponseEntity<Boolean> hasProjectAccess(@PathVariable Long projectId, @PathVariable Long userId) {
        boolean hasAccess = projectService.hasProjectAccess(userId, projectId);
        return ResponseEntity.ok(hasAccess);
    }

    @GetMapping("/{projectId}/count/users")
    public ResponseEntity<Integer> getProjectUserCount(@PathVariable Long projectId) {
        int count = projectService.getProjectUserCount(projectId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{projectId}/count/resources")
    public ResponseEntity<Integer> getProjectResourceCount(@PathVariable Long projectId) {
        int count = projectService.getProjectResourceCount(projectId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{projectId}/exists")
    public ResponseEntity<Boolean> projectExists(@PathVariable Long projectId) {
        boolean exists = projectService.projectExists(projectId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{projectId}/active")
    public ResponseEntity<Boolean> isProjectActive(@PathVariable Long projectId) {
        boolean isActive = projectService.isProjectActive(projectId);
        return ResponseEntity.ok(isActive);
    }

    @GetMapping("/{projectId}/users")
    public ResponseEntity<List<com.example.TeamResourceAccessManagement.dto.UserResponseDTO>> getProjectUsers(@PathVariable Long projectId) {
        List<com.example.TeamResourceAccessManagement.dto.UserResponseDTO> users = projectService.getProjectUsers(projectId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{projectId}/resources")
    public ResponseEntity<List<com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO>> getProjectResources(@PathVariable Long projectId) {
        List<com.example.TeamResourceAccessManagement.dto.ResourceResponseDTO> resources = projectService.getProjectResources(projectId);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectResponseDTO>> getMyProjects(java.security.Principal principal) {
        List<ProjectResponseDTO> projects = projectService.getProjectsByUsername(principal.getName());
        return ResponseEntity.ok(projects);
    }
}