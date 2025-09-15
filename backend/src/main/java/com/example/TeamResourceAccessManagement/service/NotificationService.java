package com.example.TeamResourceAccessManagement.service;

import com.example.TeamResourceAccessManagement.domain.AccessRequest;
import com.example.TeamResourceAccessManagement.domain.Notification;
import com.example.TeamResourceAccessManagement.domain.User;
import com.example.TeamResourceAccessManagement.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public void createAccessRequestNotification(AccessRequest accessRequest) {
        if (accessRequest.getProjectManager() != null) {
            Notification notification = new Notification(
                accessRequest.getProjectManager(),
                "New Access Request",
                accessRequest.getUser().getUsername() + " requested access to " + accessRequest.getResource().getName(),
                Notification.NotificationType.ACCESS_REQUEST_RECEIVED
            );
            notification.setRelatedEntityId(accessRequest.getId());
            notification.setRelatedEntityType("ACCESS_REQUEST");
            notificationRepository.save(notification);
        }
    }
    
    public void createAccessResponseNotification(AccessRequest accessRequest, boolean approved) {
        String title = approved ? "Access Request Approved" : "Access Request Rejected";
        String message = "Your request for " + accessRequest.getResource().getName() + " has been " + 
                        (approved ? "approved" : "rejected");
        
        Notification.NotificationType type = approved ? 
            Notification.NotificationType.ACCESS_REQUEST_APPROVED : 
            Notification.NotificationType.ACCESS_REQUEST_REJECTED;
            
        Notification notification = new Notification(
            accessRequest.getUser(),
            title,
            message,
            type
        );
        notification.setRelatedEntityId(accessRequest.getId());
        notification.setRelatedEntityType("ACCESS_REQUEST");
        notificationRepository.save(notification);
    }
    
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
    }
    
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
    
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
}