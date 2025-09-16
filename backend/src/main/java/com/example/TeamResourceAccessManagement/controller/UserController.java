package com.example.TeamResourceAccessManagement.controller;

import com.example.TeamResourceAccessManagement.dto.UserRequestDTO;
import com.example.TeamResourceAccessManagement.dto.UserResponseDTO;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.service.UserService;
import com.example.TeamResourceAccessManagement.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        System.out.println("\nUSER CREATION REQUEST RECEIVED IN CONTROLLER");
        System.out.println("Request Data: " + userRequestDTO.getUsername() + " | " + userRequestDTO.getEmail());
        
        try {
            UserResponseDTO user = userService.createUser(userRequestDTO);
            System.out.println("CONTROLLER: User creation successful, returning response");
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("CONTROLLER: User creation failed - " + e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(user -> ResponseEntity.ok(user))
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
            .map(user -> ResponseEntity.ok(user))
            .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
            .map(user -> ResponseEntity.ok(user))
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable User.UserRole role) {
        List<UserResponseDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO user = userService.updateUser(id, userRequestDTO);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        throw new UserNotFoundException("User not found with id: " + id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/projects/{projectId}")
    public ResponseEntity<Void> assignUserToProject(@PathVariable Long userId, @PathVariable Long projectId) {
        userService.assignUserToProject(userId, projectId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/projects/{projectId}")
    public ResponseEntity<Void> removeUserFromProject(@PathVariable Long userId, @PathVariable Long projectId) {
        userService.removeUserFromProject(userId, projectId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByProject(@PathVariable Long projectId) {
        List<UserResponseDTO> users = userService.getUsersByProjectId(projectId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/access/{resourceId}")
    public ResponseEntity<Boolean> hasUserAccessToResource(@PathVariable Long userId, @PathVariable Long resourceId) {
        boolean hasAccess = userService.hasUserAccessToResource(userId, resourceId);
        return ResponseEntity.ok(hasAccess);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveUserCount() {
        long count = userService.getActiveUserCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{userId}/active")
    public ResponseEntity<Boolean> isUserActive(@PathVariable Long userId) {
        boolean isActive = userService.isUserActive(userId);
        return ResponseEntity.ok(isActive);
    }

    @GetMapping("/{userId}/project/{projectId}/member")
    public ResponseEntity<Boolean> isUserInProject(@PathVariable Long userId, @PathVariable Long projectId) {
        boolean isMember = userService.isUserInProject(userId, projectId);
        return ResponseEntity.ok(isMember);
    }

    @PostMapping("/bulk-assign")
    public ResponseEntity<Void> assignMultipleUsersToProject(@Valid @RequestBody List<Long> userIds, @RequestParam Long projectId) {
        userService.assignMultipleUsersToProject(userIds, projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk-remove")
    public ResponseEntity<Void> removeMultipleUsersFromProject(@Valid @RequestBody List<Long> userIds, @RequestParam Long projectId) {
        userService.removeMultipleUsersFromProject(userIds, projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/cleanup-permissions")
    public ResponseEntity<Void> cleanupExpiredPermissions(@PathVariable Long userId) {
        userService.cleanupExpiredPermissions(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/managers")
    public ResponseEntity<List<UserResponseDTO>> getAllManagers() {
        List<UserResponseDTO> managers = userService.getUsersByRole(User.UserRole.PROJECT_MANAGER);
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<UserResponseDTO>> getAllEmployees() {
        List<UserResponseDTO> employees = userService.getAllUsers();
        System.out.println("\nGET ALL EMPLOYEES REQUEST");
        System.out.println("Total employees found: " + employees.size());
        employees.forEach(emp -> System.out.println("- " + emp.getUsername() + " (" + emp.getEmail() + ")"));
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String query) {
        System.out.println("\nSEARCH USERS REQUEST: '" + query + "'");
        List<UserResponseDTO> users = userService.searchUsers(query);
        System.out.println("Search results: " + users.size() + " users found");
        users.forEach(user -> System.out.println("- " + user.getUsername() + " (" + user.getEmail() + ")"));
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/teamleads")
    public ResponseEntity<List<UserResponseDTO>> getAllTeamLeads() {
        List<UserResponseDTO> teamLeads = userService.getUsersByRole(User.UserRole.TEAMLEAD);
        return ResponseEntity.ok(teamLeads);
    }
    
    @GetMapping("/debug/all")
    public ResponseEntity<List<UserResponseDTO>> debugGetAllUsers() {
        System.out.println("\nDEBUG: Getting all users from database");
        List<UserResponseDTO> users = userService.getAllUsers();
        System.out.println("Total users in database: " + users.size());
        users.forEach(user -> System.out.println("DB User: " + user.getUsername() + " | " + user.getEmail() + " | ID: " + user.getId()));
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/debug/search/{query}")
    public ResponseEntity<List<UserResponseDTO>> debugSearchUsers(@PathVariable String query) {
        System.out.println("\nDEBUG SEARCH: " + query);
        List<UserResponseDTO> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }
}