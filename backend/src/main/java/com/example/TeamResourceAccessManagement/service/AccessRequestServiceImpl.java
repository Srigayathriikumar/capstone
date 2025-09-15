package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.domain.Resource;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.dto.AccessRequestDTO;
import com.example.TeamResourceAccessManagement.mapper.AccessRequestMapper;
import com.example.TeamResourceAccessManagement.repository.AccessRequestRepository;
import com.example.TeamResourceAccessManagement.repository.PermissionRepository;
import com.example.TeamResourceAccessManagement.repository.ResourceRepository;
import com.example.TeamResourceAccessManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessRequestServiceImpl implements AccessRequestService {
    
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    public AccessRequestDTO createAccessRequest(AccessRequestDTO accessRequestDTO) {
        try {
            System.out.println("Creating access request for userId: " + accessRequestDTO.getUserId() + ", resourceId: " + accessRequestDTO.getResourceId());
            
            AccessRequest accessRequest = new AccessRequest();
            
            // Set user
            Optional<User> userOpt = userRepository.findById(accessRequestDTO.getUserId());
            if (!userOpt.isPresent()) {
                System.out.println("User not found with id: " + accessRequestDTO.getUserId());
                throw new RuntimeException("User not found with id: " + accessRequestDTO.getUserId());
            }
            User user = userOpt.get();
            System.out.println("Found user: " + user.getUsername());
            accessRequest.setUser(user);
            
            // Set resource
            Optional<Resource> resourceOpt = resourceRepository.findById(accessRequestDTO.getResourceId());
            if (!resourceOpt.isPresent()) {
                System.out.println("Resource not found with id: " + accessRequestDTO.getResourceId());
                throw new RuntimeException("Resource not found with id: " + accessRequestDTO.getResourceId());
            }
            
            Resource resource = resourceOpt.get();
            System.out.println("Found resource: " + resource.getName());
            accessRequest.setResource(resource);
            accessRequest.setProject(resource.getProject());
            
            // Set basic fields
            accessRequest.setRequestedAccessLevel(accessRequestDTO.getRequestedAccessLevel());
            accessRequest.setJustification(accessRequestDTO.getJustification());
            accessRequest.setStatus(AccessRequest.RequestStatus.PENDING);
            accessRequest.setRequestedAt(LocalDateTime.now());
            
            System.out.println("Saving access request...");
            AccessRequest savedRequest = accessRequestRepository.save(accessRequest);
            System.out.println("Access request saved with id: " + savedRequest.getId());
            
            return AccessRequestMapper.toDTO(savedRequest);
        } catch (Exception e) {
            System.out.println("Exception in createAccessRequest: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create access request: " + e.getMessage());
        }
    }
    
    @Override
    public Optional<AccessRequestDTO> getAccessRequestById(Long id) {
        return accessRequestRepository.findById(id)
            .map(AccessRequestMapper::toDTO);
    }
    
    @Override
    public List<AccessRequestDTO> getAllAccessRequests() {
        return accessRequestRepository.findAll().stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public AccessRequestDTO updateAccessRequest(Long id, AccessRequestDTO accessRequestDTO) {
        Optional<AccessRequest> requestOpt = accessRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AccessRequest request = requestOpt.get();
            request.setJustification(accessRequestDTO.getJustification());
            request.setRequestedUntil(accessRequestDTO.getRequestedUntil());
            AccessRequest savedRequest = accessRequestRepository.save(request);
            return AccessRequestMapper.toDTO(savedRequest);
        }
        throw new RuntimeException("Access request not found with id: " + id);
    }
    
    @Override
    public void deleteAccessRequest(Long id) {
        accessRequestRepository.deleteById(id);
    }
    
    @Override
    public List<AccessRequestDTO> getAccessRequestsByUser(Long userId) {
        return accessRequestRepository.findByUserId(userId).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequestDTO> getAccessRequestsByResource(Long resourceId) {
        return accessRequestRepository.findByResourceId(resourceId).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequestDTO> getAccessRequestsByProject(Long projectId) {
        return accessRequestRepository.findByProjectId(projectId).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequestDTO> getAccessRequestsByProjectManager(Long projectManagerId) {
        return accessRequestRepository.findByProjectManagerId(projectManagerId).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequestDTO> getAccessRequestsByStatus(AccessRequest.RequestStatus status) {
        return accessRequestRepository.findByStatus(status).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public AccessRequestDTO approveAccessRequest(Long id, String comments) {
        Optional<AccessRequest> requestOpt = accessRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AccessRequest request = requestOpt.get();
            request.setStatus(AccessRequest.RequestStatus.APPROVED);
            request.setApproverComments(comments);
            request.setApprovedAt(LocalDateTime.now());
            AccessRequest savedRequest = accessRequestRepository.save(request);
            
            // Create permission for the user
            Permission permission = new Permission();
            permission.setUser(request.getUser());
            permission.setResource(request.getResource());
            permission.setAccessLevel(request.getRequestedAccessLevel());
            permission.setIsActive(true);
            permission.setGrantedAt(LocalDateTime.now());
            permissionRepository.save(permission);
            
            // Create notification for requester
            notificationService.createAccessResponseNotification(savedRequest, true);
            
            return AccessRequestMapper.toDTO(savedRequest);
        }
        throw new RuntimeException("Access request not found with id: " + id);
    }
    
    @Override
    public AccessRequestDTO rejectAccessRequest(Long id, String comments) {
        Optional<AccessRequest> requestOpt = accessRequestRepository.findById(id);
        if (requestOpt.isPresent()) {
            AccessRequest request = requestOpt.get();
            request.setStatus(AccessRequest.RequestStatus.REJECTED);
            request.setApproverComments(comments);
            request.setApprovedAt(LocalDateTime.now());
            AccessRequest savedRequest = accessRequestRepository.save(request);
            
            // Create notification for requester
            notificationService.createAccessResponseNotification(savedRequest, false);
            
            return AccessRequestMapper.toDTO(savedRequest);
        }
        throw new RuntimeException("Access request not found with id: " + id);
    }
    
    @Override
    public List<AccessRequestDTO> getPendingRequestsForManager(Long managerId) {
        return accessRequestRepository.findByProjectManagerIdAndStatus(managerId, AccessRequest.RequestStatus.PENDING).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequestDTO> getPendingRequests() {
        return accessRequestRepository.findPendingRequests().stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequestDTO> getUserPendingRequests(Long userId) {
        return accessRequestRepository.findByUserIdAndStatus(userId, AccessRequest.RequestStatus.PENDING).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AccessRequestDTO> getProjectPendingRequests(Long projectId) {
        return accessRequestRepository.findByProjectIdAndStatus(projectId, AccessRequest.RequestStatus.PENDING).stream()
            .map(AccessRequestMapper::toDTO)
            .collect(Collectors.toList());
    }
}