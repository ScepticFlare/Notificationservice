package com.arjun.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.CommandLineRunner;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "notification.exchange";
    public static final String QUEUE = "notification.queue";
    public static final String ROUTING_KEY = "notification.created";
    public static final String DLQ = "notification.dlq";
    public static final String DLX = "notification.dlx";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding notificationBinding(
            Queue notificationQueue,
            DirectExchange notificationExchange) {

        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY);
    }
    @Bean
    public String rabbitConfigDebug() {
        System.out.println("RabbitMQConfig loaded!");
        return "debug";
    }
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    @Bean
    public CommandLineRunner rabbitMqProbe(RabbitAdmin rabbitAdmin) {
        return args -> {
            try {
                rabbitAdmin.initialize();
                System.out.println("RABBITMQ PROBE: initialization succeeded");
            } catch (Exception e) {
                System.err.println("RABBITMQ PROBE: initialization failed");
                e.printStackTrace();
            }
        };
    }
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue,
                                     DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(ROUTING_KEY);
    }
}