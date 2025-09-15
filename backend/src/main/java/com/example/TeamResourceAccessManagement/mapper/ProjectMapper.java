package com.example.TeamResourceAccessManagement.mapper;

import org.springframework.stereotype.Component;
import com.example.TeamResourceAccessManagement.dto.ProjectRequestDTO;
import com.example.TeamResourceAccessManagement.dto.ProjectResponseDTO;
import com.example.TeamResourceAccessManagement.domain.Project;

@Component
public class ProjectMapper {
    
    public static ProjectResponseDTO toResponse(Project project) {
        ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setId(project.getId());
        projectResponseDTO.setName(project.getName());
        projectResponseDTO.setDescription(project.getDescription());
        projectResponseDTO.setStatus(project.getStatus());
        projectResponseDTO.setCreatedAt(project.getCreatedAt());
        projectResponseDTO.setUpdatedAt(project.getUpdatedAt());
        projectResponseDTO.setMemberCount(project.getUsers() != null ? project.getUsers().size() : 0);
        projectResponseDTO.setResourceCount(project.getResources() != null ? project.getResources().size() : 0);
        return projectResponseDTO;
    }

    public static Project toEntity(ProjectRequestDTO projectRequestDTO) {
        Project project = new Project();
        project.setName(projectRequestDTO.getName());
        project.setDescription(projectRequestDTO.getDescription());
        project.setStatus(projectRequestDTO.getStatus());
        return project;
    }
}