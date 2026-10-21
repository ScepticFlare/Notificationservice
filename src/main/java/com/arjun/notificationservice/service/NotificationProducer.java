package com.arjun.notificationservice.service;

import com.arjun.notificationservice.config.RabbitMQConfig;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
@Service
@AllArgsConstructor


public class NotificationProducer {
    private final RabbitTemplate rabbitTemplate;

    public void sendTestMessage(String message)
    {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
    }
    public void publishNotificationCreated(Long notificationid)
    {
       rabbitTemplate.convertAndSend(
               RabbitMQConfig.EXCHANGE,
               RabbitMQConfig.ROUTING_KEY,
               String.valueOf(notificationid)
       );
    }

}
