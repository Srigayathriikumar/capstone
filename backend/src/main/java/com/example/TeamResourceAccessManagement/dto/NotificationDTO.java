package com.example.TeamResourceAccessManagement.dto;

import com.example.TeamResourceAccessManagement.domain.Notification;
import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long accessRequestId;
    
    public NotificationDTO() {}
    
    public NotificationDTO(Long id, String title, String message, Notification.NotificationType type, 
                          Boolean isRead, LocalDateTime createdAt, Long accessRequestId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.accessRequestId = accessRequestId;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Notification.NotificationType getType() { return type; }
    public void setType(Notification.NotificationType type) { this.type = type; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Long getAccessRequestId() { return accessRequestId; }
    public void setAccessRequestId(Long accessRequestId) { this.accessRequestId = accessRequestId; }
}