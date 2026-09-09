    package com.arjun.notificationservice.service;

    import com.arjun.notificationservice.dto.CreateNotificationRequest;
    import com.arjun.notificationservice.dto.NotificationResponse;
    import com.arjun.notificationservice.entity.Notification;
    import com.arjun.notificationservice.enums.NotificationStatus;
    import com.arjun.notificationservice.exception.NotificationNotFoundException;
    import com.arjun.notificationservice.repository.NotificationRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.*;

    @Service
    @RequiredArgsConstructor
    public class NotificationService {
        private final NotificationRepository notificationRepository;

        public NotificationResponse createNotification(CreateNotificationRequest request)
        {
            Notification notification= new Notification();
            notification.setSenderId(request.getSenderId());
            notification.setReceiverId(request.getReceiverId());
            notification.setMessage(request.getMessage());
            notification.setTitle(request.getTitle());
            notification.setType(request.getType());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setStatus(NotificationStatus.UNREAD);

             notificationRepository.save(notification);
             return mapToResponse(notification);
        }
        public Page<NotificationResponse> getNotificationByReceiverId(Long receiverId, Pageable pageable)
        {
            int pageSize = Math.min(pageable.getPageSize(), 20);

            Pageable cappedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageSize,
                    pageable.getSort()
            );

            Page<Notification> notifications =
                    notificationRepository.findByReceiverId(receiverId, cappedPageable);

            return notifications.map(this::mapToResponse);

        }
        public Notification markAsread(Long id)
        {
            Optional<Notification> optionalNotification= notificationRepository.findById(id);
            if(optionalNotification.isPresent()) {
                Notification notification = optionalNotification.get();
                notification.setStatus(NotificationStatus.READ);
                return notificationRepository.save(notification);
            }
            else{
                throw new NotificationNotFoundException("Notification not found:" + id);
            }
        }
        public void deleteNotification(Long id)
        {
            Optional<Notification>optionalNotification=notificationRepository.findById(id);
            if(optionalNotification.isPresent())
            {
                Notification notification= optionalNotification.get();
                notificationRepository.delete(notification);
            }
            else
            {
                throw new NotificationNotFoundException("Notification not found :" +id);
            }
        }
        public Page<NotificationResponse> getUnreadNotifications(Long receiverId,Pageable pageable)
        {
            int minsize=Math.min(20,pageable.getPageSize());
            Pageable p= PageRequest.of(
                    pageable.getPageNumber(),
                    minsize,
                    pageable.getSort()
            );
            Page<Notification> notifications =notificationRepository.findByReceiverIdAndStatus(receiverId,NotificationStatus.UNREAD,p);
            return notifications.map(this::mapToResponse);
        }

        private NotificationResponse mapToResponse(Notification notification)
        {
            NotificationResponse response = new NotificationResponse();

            response.setId(notification.getId());
            response.setSenderId(notification.getSenderId());
            response.setReceiverId(notification.getReceiverId());
            response.setTitle(notification.getTitle());
            response.setMessage(notification.getMessage());
            response.setCreatedAt(notification.getCreatedAt());
            response.setStatus(notification.getStatus());
            response.setType(notification.getType());
            return response;
        }
    }
