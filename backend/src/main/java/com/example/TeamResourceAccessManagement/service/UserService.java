package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Project;
import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.dto.UserRequestDTO;
import com.example.TeamResourceAccessManagement.dto.UserResponseDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    // User CRUD Operations
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    
    Optional<UserResponseDTO> getUserById(Long userId);
    Optional<UserResponseDTO> getUserByUsername(String username);
    Optional<UserResponseDTO> getUserByEmail(String email);
    List<UserResponseDTO> getAllUsers();
    List<UserResponseDTO> getUsersByRole(User.UserRole role);
    UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO);
    void deleteUser(Long userId);
    
    // Project Assignment Operations
    void assignUserToProject(Long userId, Long projectId);
    void removeUserFromProject(Long userId, Long projectId);
    List<UserResponseDTO> getUsersByProjectId(Long projectId);
    List<Project> getProjectsByUserId(Long userId);
    
    // Permission Operations
    List<Permission> getUserPermissions(Long userId);
    List<Permission> getUserPermissionsByProject(Long userId, Long projectId);
    void revokeUserPermission(Long userId, Long resourceId);
    
    // Access Request Operations
    List<AccessRequest> getUserAccessRequests(Long userId);
    List<AccessRequest> getPendingRequestsForApprover(Long approverId);
    
    // User Status Operations
    boolean isUserActive(Long userId);
    boolean hasUserAccessToResource(Long userId, Long resourceId);
    boolean isUserInProject(Long userId, Long projectId);
    
    // Bulk Operations
    void assignMultipleUsersToProject(List<Long> userIds, Long projectId);
    void removeMultipleUsersFromProject(List<Long> userIds, Long projectId);
    
    // Cleanup Operations
    void cleanupExpiredPermissions(Long userId);
    long getUserCount();
    long getActiveUserCount();
    
    // Search Operations
    List<UserResponseDTO> searchUsers(String query);
}