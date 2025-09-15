package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.dto.AccessRequestDTO;
import java.util.List;
import java.util.Optional;

public interface AccessRequestService {
    
    // CRUD Operations
    AccessRequestDTO createAccessRequest(AccessRequestDTO accessRequestDTO);
    Optional<AccessRequestDTO> getAccessRequestById(Long id);
    List<AccessRequestDTO> getAllAccessRequests();
    AccessRequestDTO updateAccessRequest(Long id, AccessRequestDTO accessRequestDTO);
    void deleteAccessRequest(Long id);
    
    // Filtering Operations
    List<AccessRequestDTO> getAccessRequestsByUser(Long userId);
    List<AccessRequestDTO> getAccessRequestsByResource(Long resourceId);
    List<AccessRequestDTO> getAccessRequestsByProject(Long projectId);
    List<AccessRequestDTO> getAccessRequestsByProjectManager(Long projectManagerId);
    List<AccessRequestDTO> getAccessRequestsByStatus(AccessRequest.RequestStatus status);
    
    // Manager Operations
    AccessRequestDTO approveAccessRequest(Long id, String comments);
    AccessRequestDTO rejectAccessRequest(Long id, String comments);
    List<AccessRequestDTO> getPendingRequestsForManager(Long managerId);
    
    // Status Operations
    List<AccessRequestDTO> getPendingRequests();
    List<AccessRequestDTO> getUserPendingRequests(Long userId);
    List<AccessRequestDTO> getProjectPendingRequests(Long projectId);
}