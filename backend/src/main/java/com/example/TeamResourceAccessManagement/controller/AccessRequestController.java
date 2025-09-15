package com.example.TeamResourceAccessManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.dto.AccessRequestDTO;
import com.example.TeamResourceAccessManagement.service.AccessRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/access-requests")
public class AccessRequestController {
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("Test endpoint reached");
        return ResponseEntity.ok("Controller is working");
    }
    
    @Autowired
    private AccessRequestService accessRequestService;
    
    @PostMapping("/simple")
    public ResponseEntity<AccessRequestDTO> createSimpleRequest(@RequestBody java.util.Map<String, Object> requestMap) {
        System.out.println("Simple POST method reached!");
        System.out.println("Request map: " + requestMap);
        
        try {
            System.out.println("Controller: Processing request map: " + requestMap);
            
            AccessRequestDTO dto = new AccessRequestDTO();
            Long userId = Long.valueOf(requestMap.get("userId").toString());
            Long resourceId = Long.valueOf(requestMap.get("resourceId").toString());
            String accessLevel = requestMap.get("requestedAccessLevel").toString();
            String justification = requestMap.get("justification").toString();
            
            System.out.println("Controller: Parsed - userId: " + userId + ", resourceId: " + resourceId + ", accessLevel: " + accessLevel);
            
            dto.setUserId(userId);
            dto.setResourceId(resourceId);
            dto.setRequestedAccessLevel(com.example.TeamResourceAccessManagement.domain.Permission.AccessLevel.valueOf(accessLevel.toUpperCase()));
            dto.setJustification(justification);
            
            System.out.println("Controller: Calling service with DTO: " + dto);
            AccessRequestDTO createdRequest = accessRequestService.createAccessRequest(dto);
            System.out.println("Controller: Service returned: " + createdRequest);
            return ResponseEntity.ok(createdRequest);
        } catch (IllegalArgumentException e) {
            System.out.println("Controller Bad Request: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.out.println("Controller Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/map")
    public ResponseEntity<String> createWithMap(@RequestBody java.util.Map<String, Object> requestMap) {
        System.out.println("Map POST method reached!");
        System.out.println("Request map: " + requestMap);
        return ResponseEntity.ok("Map received: " + requestMap.toString());
    }
    
    @PostMapping
    public ResponseEntity<AccessRequestDTO> createAccessRequest(@RequestBody AccessRequestDTO accessRequestDTO) {
        System.out.println("POST method reached!");
        System.out.println("Received access request: " + accessRequestDTO);
        System.out.println("UserId: " + accessRequestDTO.getUserId());
        System.out.println("ResourceId: " + accessRequestDTO.getResourceId());
        System.out.println("AccessLevel: " + accessRequestDTO.getRequestedAccessLevel());
        System.out.println("Justification: " + accessRequestDTO.getJustification());
        
        if (accessRequestDTO.getUserId() == null || accessRequestDTO.getResourceId() == null) {
            System.out.println("Validation failed: missing userId or resourceId");
            return ResponseEntity.badRequest().build();
        }
        try {
            AccessRequestDTO createdRequest = accessRequestService.createAccessRequest(accessRequestDTO);
            return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("Exception in createAccessRequest: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AccessRequestDTO> getAccessRequestById(@PathVariable Long id) {
        return accessRequestService.getAccessRequestById(id)
            .map(request -> ResponseEntity.ok(request))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<AccessRequestDTO>> getAllAccessRequests() {
        List<AccessRequestDTO> requests = accessRequestService.getAllAccessRequests();
        return ResponseEntity.ok(requests);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AccessRequestDTO> updateAccessRequest(@PathVariable Long id, @Valid @RequestBody AccessRequestDTO accessRequestDTO) {
        AccessRequestDTO updatedRequest = accessRequestService.updateAccessRequest(id, accessRequestDTO);
        return ResponseEntity.ok(updatedRequest);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccessRequest(@PathVariable Long id) {
        accessRequestService.deleteAccessRequest(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccessRequestDTO>> getAccessRequestsByUser(@PathVariable Long userId) {
        List<AccessRequestDTO> requests = accessRequestService.getAccessRequestsByUser(userId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<AccessRequestDTO>> getAccessRequestsByResource(@PathVariable Long resourceId) {
        List<AccessRequestDTO> requests = accessRequestService.getAccessRequestsByResource(resourceId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<AccessRequestDTO>> getAccessRequestsByProject(@PathVariable Long projectId) {
        if (projectId == null || projectId <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<AccessRequestDTO> requests = accessRequestService.getAccessRequestsByProject(projectId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<AccessRequestDTO>> getAccessRequestsByManager(@PathVariable Long managerId) {
        List<AccessRequestDTO> requests = accessRequestService.getAccessRequestsByProjectManager(managerId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccessRequestDTO>> getAccessRequestsByStatus(@PathVariable AccessRequest.RequestStatus status) {
        List<AccessRequestDTO> requests = accessRequestService.getAccessRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }
    
    @PutMapping("/{id}/approve")
    public ResponseEntity<AccessRequestDTO> approveAccessRequest(@PathVariable Long id, @RequestBody String comments) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            AccessRequestDTO approvedRequest = accessRequestService.approveAccessRequest(id, comments);
            return ResponseEntity.ok(approvedRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/reject")
    public ResponseEntity<AccessRequestDTO> rejectAccessRequest(@PathVariable Long id, @RequestBody String comments) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            AccessRequestDTO rejectedRequest = accessRequestService.rejectAccessRequest(id, comments);
            return ResponseEntity.ok(rejectedRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<AccessRequestDTO>> getPendingRequests() {
        List<AccessRequestDTO> requests = accessRequestService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/pending/manager/{managerId}")
    public ResponseEntity<List<AccessRequestDTO>> getPendingRequestsForManager(@PathVariable Long managerId) {
        List<AccessRequestDTO> requests = accessRequestService.getPendingRequestsForManager(managerId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/pending/user/{userId}")
    public ResponseEntity<List<AccessRequestDTO>> getUserPendingRequests(@PathVariable Long userId) {
        List<AccessRequestDTO> requests = accessRequestService.getUserPendingRequests(userId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/pending/project/{projectId}")
    public ResponseEntity<List<AccessRequestDTO>> getProjectPendingRequests(@PathVariable Long projectId) {
        List<AccessRequestDTO> requests = accessRequestService.getProjectPendingRequests(projectId);
        return ResponseEntity.ok(requests);
    }
}