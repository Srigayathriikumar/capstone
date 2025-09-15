package com.example.TeamResourceAccessManagement.repository;

import com.example.TeamResourceAccessManagement.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(Project.ProjectStatus status);
    
    @Query("SELECT p FROM Project p JOIN p.users u WHERE u.id = :userId")
    List<Project> findByUserId(Long userId);
    
    @Query("SELECT p FROM Project p WHERE p.name LIKE %:name%")
    List<Project> findByNameContaining(String name);
    
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.users LEFT JOIN FETCH p.resources")
    List<Project> findAllWithUsersAndResources();
}