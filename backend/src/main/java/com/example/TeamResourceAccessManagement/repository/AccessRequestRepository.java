package com.example.TeamResourceAccessManagement.repository;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByUserId(Long userId);
    List<AccessRequest> findByResourceId(Long resourceId);
    List<AccessRequest> findByProjectId(Long projectId);
    List<AccessRequest> findByProjectManagerId(Long projectManagerId);
    List<AccessRequest> findByStatus(AccessRequest.RequestStatus status);
    
    @Query("SELECT ar FROM AccessRequest ar WHERE ar.user.id = :userId AND ar.status = :status")
    List<AccessRequest> findByUserIdAndStatus(Long userId, AccessRequest.RequestStatus status);
    
    @Query("SELECT ar FROM AccessRequest ar WHERE ar.project.id = :projectId AND ar.status = :status")
    List<AccessRequest> findByProjectIdAndStatus(Long projectId, AccessRequest.RequestStatus status);
    
    @Query("SELECT ar FROM AccessRequest ar WHERE ar.projectManager.id = :managerId AND ar.status = :status")
    List<AccessRequest> findByProjectManagerIdAndStatus(Long managerId, AccessRequest.RequestStatus status);
    
    @Query("SELECT ar FROM AccessRequest ar WHERE ar.status = 'PENDING' ORDER BY ar.requestedAt ASC")
    List<AccessRequest> findPendingRequests();
}