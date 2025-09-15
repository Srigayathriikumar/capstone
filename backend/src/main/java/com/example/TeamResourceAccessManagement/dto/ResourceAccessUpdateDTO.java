package com.example.TeamResourceAccessManagement.dto;

import com.example.TeamResourceAccessManagement.domain.Resource.ResourceAccessType;

public class ResourceAccessUpdateDTO {
    private ResourceAccessType accessType;
    private String allowedUserGroups;
    
    public ResourceAccessUpdateDTO() {}
    
    public ResourceAccessUpdateDTO(ResourceAccessType accessType, String allowedUserGroups) {
        this.accessType = accessType;
        this.allowedUserGroups = allowedUserGroups;
    }
    
    public ResourceAccessType getAccessType() {
        return accessType;
    }
    
    public void setAccessType(ResourceAccessType accessType) {
        this.accessType = accessType;
    }
    
    public String getAllowedUserGroups() {
        return allowedUserGroups;
    }
    
    public void setAllowedUserGroups(String allowedUserGroups) {
        this.allowedUserGroups = allowedUserGroups;
    }
}