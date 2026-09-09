package com.arjun.notificationservice.dto;

import com.arjun.notificationservice.entity.Notification;
import com.arjun.notificationservice.enums.NotificationStatus;
import com.arjun.notificationservice.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class NotificationResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private NotificationStatus status;
    private NotificationType type;


}
