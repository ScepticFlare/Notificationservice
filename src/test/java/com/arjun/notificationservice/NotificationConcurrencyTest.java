package com.arjun.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.arjun.notificationservice.repository.NotificationRepository;
import com.arjun.notificationservice.entity.Notification;
import com.arjun.notificationservice.enums.NotificationStatus;
import com.arjun.notificationservice.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
public class NotificationConcurrencyTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void testConcurrency() {

        // Create notification
        Notification notification = new Notification();

        notification.setSenderId(1L);
        notification.setReceiverId(42L);
        notification.setTitle("Concurrency Test");
        notification.setMessage("Testing optimistic locking");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setType(NotificationType.ALERT);

        notification = notificationRepository.save(notification);

        Long notificationId = notification.getId();

        // Two worker threads
        ExecutorService executor =
                Executors.newFixedThreadPool(2);

        // Make both threads read before either one updates
        CyclicBarrier barrier =
                new CyclicBarrier(2);

        TransactionTemplate transactionTemplate =
                new TransactionTemplate(transactionManager);

        // Thread A
        Runnable taskA = () -> {

            transactionTemplate.execute(status -> {

                Notification n = notificationRepository
                        .findById(notificationId)
                        .orElseThrow();

                System.out.println(
                        "Thread A read version: " + n.getVersion()
                );

                try {
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                n.setTitle("updated by thread A");

                // Force UPDATE immediately
                notificationRepository.saveAndFlush(n);

                System.out.println(
                        "Thread A updated notification"
                );

                return null;
            });
        };

        // Thread B
        Runnable taskB = () -> {

            transactionTemplate.execute(status -> {

                Notification n = notificationRepository
                        .findById(notificationId)
                        .orElseThrow();

                System.out.println(
                        "Thread B read version: " + n.getVersion()
                );

                try {
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                n.setTitle("updated by thread B");

                // Force UPDATE immediately
                notificationRepository.saveAndFlush(n);

                System.out.println(
                        "Thread B updated notification"
                );

                return null;
            });
        };

        // Start both threads and keep their Futures
        Future<?> futureA = executor.submit(taskA);
        Future<?> futureB = executor.submit(taskB);

        executor.shutdown();

        // Wait for Thread A
        try {
            futureA.get();
        } catch (ExecutionException e) {
            System.out.println(
                    "Thread A failed: " + e.getCause()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for Thread B
        try {
            futureB.get();
        } catch (ExecutionException e) {
            System.out.println(
                    "Thread B failed: " + e.getCause()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println(
                "Notification ID: " + notificationId
        );
    }
}