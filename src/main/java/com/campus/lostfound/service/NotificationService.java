package com.campus.lostfound.service;

import com.campus.lostfound.dto.response.NotificationResponse;
import com.campus.lostfound.entity.User;

import java.util.List;

/**
 * Service interface for managing user notifications.
 */
public interface NotificationService {

    void sendNotification(User user, String title, String message, String type, Long referenceId);

    List<NotificationResponse> getUserNotifications(User user);

    void markAsRead(Long notificationId, User user);

    void markAllAsRead(User user);

    long getUnreadCount(User user);
}
