package com.arjun.notificationservice.repository;
import com.arjun.notificationservice.entity.Notification;
import com.arjun.notificationservice.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);
    Page<Notification> findByReceiverIdAndStatus(Long receiverId, NotificationStatus notificationStatus,Pageable pageable);
}
