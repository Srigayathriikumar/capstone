package com.example.TeamResourceAccessManagement.dto;

import com.example.TeamResourceAccessManagement.domain.Permission;

public class ResourceAccessDTO {
    private Long userId;
    private String username;
    private String email;
    private Permission.AccessLevel accessLevel;
    
    public ResourceAccessDTO() {}
    
    public ResourceAccessDTO(Long userId, String username, String email, Permission.AccessLevel accessLevel) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.accessLevel = accessLevel;
    }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Permission.AccessLevel getAccessLevel() { return accessLevel; }
    public void setAccessLevel(Permission.AccessLevel accessLevel) { this.accessLevel = accessLevel; }
}