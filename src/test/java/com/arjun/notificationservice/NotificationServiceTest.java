package com.arjun.notificationservice;

import com.arjun.notificationservice.entity.Notification;
import com.arjun.notificationservice.enums.NotificationStatus;
import com.arjun.notificationservice.exception.NotificationNotFoundException;
import com.arjun.notificationservice.repository.NotificationRepository;
import com.arjun.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Test
    void markAsRead_shouldMarkNotificationAsRead() {

        // Arrange
        NotificationRepository repository= mock(NotificationRepository.class);
        RedisTemplate<String,String>redisTemplate= mock(RedisTemplate.class);
        NotificationService service =
                new NotificationService(repository, redisTemplate);
        Notification notification= new Notification();

        notification.setId(1L);
        notification.setReceiverId(42L);
        notification.setStatus(NotificationStatus.UNREAD);

        when(repository.findById(1L))
                .thenReturn(Optional.of(notification));



        // Act
        service.markAsread(1L);



        // Assert
        assertEquals(NotificationStatus.READ,notification.getStatus());
        verify(repository).save(notification);
        verify(redisTemplate).delete("notification:unread:42");

    }


    @Test
    void markAsRead_shouldThrowExceptionWhenNotificationNotFound() {

        // Arrange
        NotificationRepository repository= mock(NotificationRepository.class);
        RedisTemplate<String,String>redisTemplate= mock(RedisTemplate.class);
        NotificationService service =
                new NotificationService(repository, redisTemplate);



        when(repository.findById(99L))
                .thenReturn(Optional.empty());



        // Act + Assert

        assertThrows(NotificationNotFoundException.class,()->service.markAsread(99L)

        );

    }

    @Test
    void getUnreadCount_shouldReturnCachedValue() {

        // Arrange
        NotificationRepository repository =
                mock(NotificationRepository.class);

        RedisTemplate<String, String> redisTemplate =
                mock(RedisTemplate.class);

        NotificationService service =
                new NotificationService(repository, redisTemplate);
        ValueOperations<String,String> valueOperations=mock(ValueOperations.class);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get("notification:unread:42")).thenReturn("7");

        // Act

        long result = service.getUnreadCount(42L);



        // Assert
        assertEquals(7, result);
        verify(repository, never())
                .countByReceiverIdAndStatus(42L, NotificationStatus.UNREAD);


    }
    @Test
    void getUnreadCount_shouldQueryDatabaseOnCacheMiss() {

        // Arrange
        NotificationRepository repository =
                mock(NotificationRepository.class);

        RedisTemplate<String, String> redisTemplate =
                mock(RedisTemplate.class);

        NotificationService service =
                new NotificationService(repository, redisTemplate);
        ValueOperations<String,String> valueOperations=mock(ValueOperations.class);
        when(redisTemplate.opsForValue())
                .thenReturn(valueOperations);
        when(valueOperations.get("notification:unread:42")).thenReturn(null);
        when(repository.countByReceiverIdAndStatus(
                42L,
                NotificationStatus.UNREAD
        )).thenReturn(5L);




        // Act
        long result= service.getUnreadCount(42L);


        // Assert
        assertEquals(5, result);

        verify(repository).countByReceiverIdAndStatus(
                42L,
                NotificationStatus.UNREAD
        );
        verify(valueOperations).set(
                "notification:unread:42",
                "5"
        );


    }
    @Test
    void deleteNotification_shouldDeleteUnreadNotification() {

        // Arrange
        NotificationRepository repository= mock(NotificationRepository.class);
        RedisTemplate<String, String> redisTemplate =
                mock(RedisTemplate.class);
        NotificationService service= new NotificationService(repository,redisTemplate);
        Notification notification= new Notification();
        notification.setId(1L);
        notification.setReceiverId(42L);
        notification.setStatus(NotificationStatus.UNREAD);
        when(repository.findById(1L)).thenReturn(Optional.of(notification));


        // Act
        service.deleteNotification(1L);


        // Assert
        verify(repository).delete(notification);
        verify(redisTemplate).delete("notification:unread:42");

    }
    @Test
    void deleteNotification_shouldNotInvalidateCacheWhenRead() {

        // Arrange
        NotificationRepository repository= mock(NotificationRepository.class);
        RedisTemplate<String, String> redisTemplate =
                mock(RedisTemplate.class);
        NotificationService service= new NotificationService(repository,redisTemplate);
        Notification notification= new Notification();
        notification.setId(1L);
        notification.setReceiverId(42L);
        notification.setStatus(NotificationStatus.READ);
        when(repository.findById(1L)).thenReturn(Optional.of(notification));


        // Act
        service.deleteNotification(1L);


        // Assert
        verify(repository).delete(notification);
        verify(redisTemplate, never())
                .delete("notification:unread:42");


    }
}