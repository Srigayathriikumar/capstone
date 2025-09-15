package com.example.TeamResourceAccessManagement.repository;

import com.example.TeamResourceAccessManagement.domain.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByIsGlobal(Boolean isGlobal);
    List<Resource> findByProjectId(Long projectId);
    List<Resource> findByType(Resource.ResourceType type);
    List<Resource> findByCategory(Resource.ResourceCategory category);
    List<Resource> findByAccessType(Resource.ResourceAccessType accessType);
    List<Resource> findByProjectIdAndCategory(Long projectId, Resource.ResourceCategory category);
    List<Resource> findByProjectIdAndAccessType(Long projectId, Resource.ResourceAccessType accessType);
    
    @Query("SELECT r FROM Resource r WHERE r.isGlobal = true OR r.project.id = :projectId")
    List<Resource> findAvailableResourcesForProject(Long projectId);
    
    @Query("SELECT r FROM Resource r WHERE r.name LIKE %:name%")
    List<Resource> findByNameContaining(String name);
}