package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.domain.Project;
import com.example.TeamResourceAccessManagement.domain.Permission;
import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.dto.UserRequestDTO;
import com.example.TeamResourceAccessManagement.dto.UserResponseDTO;
import com.example.TeamResourceAccessManagement.mapper.UserMapper;
import com.example.TeamResourceAccessManagement.repository.UserRepository;
import com.example.TeamResourceAccessManagement.repository.ProjectRepository;
import com.example.TeamResourceAccessManagement.repository.PermissionRepository;
import com.example.TeamResourceAccessManagement.repository.AccessRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private AccessRequestRepository accessRequestRepository;
    
    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        System.out.println("=== CREATING NEW USER ===");
        System.out.println("Username: " + userRequestDTO.getUsername());
        System.out.println("Email: " + userRequestDTO.getEmail());
        System.out.println("Full Name: " + userRequestDTO.getFullName());
        System.out.println("Role: " + userRequestDTO.getRole());
        
        // Check if username already exists
        if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            System.out.println("ERROR: Username already exists: " + userRequestDTO.getUsername());
            throw new RuntimeException("Username already exists: " + userRequestDTO.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            System.out.println("ERROR: Email already exists: " + userRequestDTO.getEmail());
            throw new RuntimeException("Email already exists: " + userRequestDTO.getEmail());
        }
        
        User user = UserMapper.toEntity(userRequestDTO);
        User savedUser = userRepository.save(user);
        userRepository.flush(); // Force immediate database write
        
        System.out.println("USER CREATED SUCCESSFULLY!");
        System.out.println("Generated ID: " + savedUser.getId());
        System.out.println("Username: " + savedUser.getUsername());
        System.out.println("Email: " + savedUser.getEmail());
        System.out.println("Full Name: " + savedUser.getFullName());
        System.out.println("Role: " + savedUser.getRole());
        System.out.println("Created At: " + savedUser.getCreatedAt());
        System.out.println("=========================");
        
        return UserMapper.toResponse(savedUser);
    }
    
    @Override
    public Optional<UserResponseDTO> getUserById(Long userId) {
        return userRepository.findById(userId)
            .map(UserMapper::toResponse);
    }
    
    @Override
    public Optional<UserResponseDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(UserMapper::toResponse);
    }
    
    @Override
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(UserMapper::toResponse);
    }
    
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAllOrderByCreatedAtDesc().stream()
            .map(UserMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponseDTO> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role).stream()
            .map(UserMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUsername(userRequestDTO.getUsername());
            user.setEmail(userRequestDTO.getEmail());
            user.setFullName(userRequestDTO.getFullName());
            user.setRole(userRequestDTO.getRole());
            User savedUser = userRepository.save(user);
            return UserMapper.toResponse(savedUser);
        }
        return null;
    }
    
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    @Override
    public void assignUserToProject(Long userId, Long projectId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        
        if (userOpt.isPresent() && projectOpt.isPresent()) {
            User user = userOpt.get();
            Project project = projectOpt.get();
            user.getProjects().add(project);
            userRepository.save(user);
        }
    }
    
    @Override
    public void removeUserFromProject(Long userId, Long projectId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        
        if (userOpt.isPresent() && projectOpt.isPresent()) {
            User user = userOpt.get();
            Project project = projectOpt.get();
            user.getProjects().remove(project);
            userRepository.save(user);
        }
    }
    
    @Override
    public List<UserResponseDTO> getUsersByProjectId(Long projectId) {
        return userRepository.findByProjectId(projectId).stream()
            .map(UserMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Project> getProjectsByUserId(Long userId) {
        return projectRepository.findByUserId(userId);
    }
    
    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return permissionRepository.findByUserId(userId);
    }
    
    @Override
    public List<Permission> getUserPermissionsByProject(Long userId, Long projectId) {
        return permissionRepository.findByUserIdAndProjectId(userId, projectId);
    }
    
    @Override
    public void revokeUserPermission(Long userId, Long resourceId) {
        Optional<Permission> permissionOpt = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        if (permissionOpt.isPresent()) {
            Permission permission = permissionOpt.get();
            permission.setIsActive(false);
            permissionRepository.save(permission);
        }
    }
    
    @Override
    public List<AccessRequest> getUserAccessRequests(Long userId) {
        return accessRequestRepository.findByUserId(userId);
    }
    
    @Override
    public List<AccessRequest> getPendingRequestsForApprover(Long approverId) {
        return accessRequestRepository.findPendingRequests();
    }
    
    @Override
    public boolean isUserActive(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.isPresent();
    }
    
    @Override
    public boolean hasUserAccessToResource(Long userId, Long resourceId) {
        Optional<Permission> permission = permissionRepository.findByUserIdAndResourceId(userId, resourceId);
        return permission.isPresent() && permission.get().getIsActive() && 
               (permission.get().getExpiresAt() == null || permission.get().getExpiresAt().isAfter(LocalDateTime.now()));
    }
    
    @Override
    public boolean isUserInProject(Long userId, Long projectId) {
        List<Project> userProjects = projectRepository.findByUserId(userId);
        return userProjects.stream().anyMatch(project -> project.getId().equals(projectId));
    }
    
    @Override
    public void assignMultipleUsersToProject(List<Long> userIds, Long projectId) {
        userIds.forEach(userId -> assignUserToProject(userId, projectId));
    }
    
    @Override
    public void removeMultipleUsersFromProject(List<Long> userIds, Long projectId) {
        userIds.forEach(userId -> removeUserFromProject(userId, projectId));
    }
    
    @Override
    public void cleanupExpiredPermissions(Long userId) {
        List<Permission> expiredPermissions = permissionRepository.findExpiredPermissions(LocalDateTime.now());
        expiredPermissions.stream()
            .filter(permission -> permission.getUser().getId().equals(userId))
            .forEach(permission -> {
                permission.setIsActive(false);
                permissionRepository.save(permission);
            });
    }
    
    @Override
    public long getUserCount() {
        return userRepository.count();
    }
    
    @Override
    public long getActiveUserCount() {
        return userRepository.findAll().size();
    }
    
    @Override
    public List<UserResponseDTO> searchUsers(String query) {
        System.out.println("\nSEARCHING FOR: '" + query + "'");
        
        if (query == null || query.trim().isEmpty()) {
            List<UserResponseDTO> allUsers = getAllUsers();
            System.out.println("Empty query - returning all " + allUsers.size() + " users");
            return allUsers;
        }
        
        List<User> users = userRepository.searchUsers(query.trim());
        System.out.println("Native SQL search found: " + users.size() + " users");
        users.forEach(user -> System.out.println("Found: " + user.getUsername() + " | " + user.getEmail()));
        
        return users.stream()
            .map(UserMapper::toResponse)
            .collect(Collectors.toList());
    }
}