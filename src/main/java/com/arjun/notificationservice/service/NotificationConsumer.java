
package com.arjun.notificationservice.service;

import com.arjun.notificationservice.config.RabbitMQConfig;
import com.arjun.notificationservice.entity.Notification;
import com.arjun.notificationservice.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;


    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receiveMessage(String message) {

        Long notificationId = Long.valueOf(message);

        Optional<Notification> optionalNotification =
                notificationRepository.findById(notificationId);

        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();

            System.out.println("Processing notification ID: "
                    + notification.getId());
            System.out.println("Title: " + notification.getTitle());
        } else {
            System.out.println("Notification not found: " + notificationId);
        }
    }
}
