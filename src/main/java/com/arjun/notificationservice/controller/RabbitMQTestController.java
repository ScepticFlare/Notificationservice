package com.arjun.notificationservice.controller;
import com.arjun.notificationservice.service.NotificationProducer;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@AllArgsConstructor
@RequestMapping("/api/rabbitmq")
public class RabbitMQTestController {

    private final NotificationProducer notificationProducer;

    @PostMapping("/test")
    public String sendTestMessage(@RequestBody String message)
    {
        notificationProducer.sendTestMessage(message);
        return "Message published";
    }

}
