package com.arjun.notificationservice;

import com.arjun.notificationservice.entity.Notification;
import com.arjun.notificationservice.enums.NotificationStatus;
import com.arjun.notificationservice.enums.NotificationType;
import com.arjun.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationRepositoryIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldSaveAndRetrieveNotification() {

        // Arrange
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setReceiverId(42L);
        notification.setSenderId(1L);
        notification.setTitle("Test notification");
        notification.setMessage("Integration test");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setType(NotificationType.ALERT);

        // Act
        Notification savedNotification =
                notificationRepository.save(notification);

        Notification retrievedNotification =
                notificationRepository.findById(savedNotification.getId())
                        .orElseThrow();

        // Assert
        assertNotNull(savedNotification.getId());

        assertEquals(42L, retrievedNotification.getReceiverId());
        assertEquals(1L, retrievedNotification.getSenderId());
        assertEquals("Test notification", retrievedNotification.getTitle());
        assertEquals("Integration test", retrievedNotification.getMessage());
        assertEquals(NotificationStatus.UNREAD,
                retrievedNotification.getStatus());
        assertEquals(NotificationType.ALERT,
                retrievedNotification.getType());
    }
    @Test
    void shouldfindByReceiverIdAndStatus()
    {
        Notification notification_A = new Notification();
        Notification notification_B = new Notification();
        Notification notification_C = new Notification();

        notification_A.setStatus(NotificationStatus.UNREAD);
        notification_B.setStatus(NotificationStatus.READ);
        notification_C.setStatus(NotificationStatus.UNREAD);
        notification_A.setReceiverId(987654L);
        notification_B.setReceiverId(987654L);
        notification_C.setReceiverId(987655L);

        notificationRepository.save(notification_A);
        notificationRepository.save(notification_B);
        notificationRepository.save(notification_C);



        Page<Notification> result =notificationRepository.findByReceiverIdAndStatus(987654L,NotificationStatus.UNREAD, PageRequest.of(0,10));


        assertEquals(1L,result.getTotalElements());
        assertEquals(1L, result.getContent().size());
        Notification returnedNotification = result.getContent().get(0);
        assertEquals(987654L, returnedNotification.getReceiverId());
        assertEquals(
                NotificationStatus.UNREAD,
                returnedNotification.getStatus()
        );

    }
}