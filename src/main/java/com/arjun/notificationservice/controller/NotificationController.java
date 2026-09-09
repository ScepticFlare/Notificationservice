package com.arjun.notificationservice.controller;

import com.arjun.notificationservice.dto.CreateNotificationRequest;
import com.arjun.notificationservice.dto.NotificationResponse;
import com.arjun.notificationservice.entity.Notification;
import com.arjun.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import com.arjun.notificationservice.dto.NotificationResponse;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
private final NotificationService notificationService;

@PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody CreateNotificationRequest request)
{
    NotificationResponse notification= notificationService.createNotification(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(notification);




}
@GetMapping("/{receiverId}")
public ResponseEntity<Page<NotificationResponse>> getNotification(@PathVariable Long receiverId,@PageableDefault(size=20,sort="createdAt",direction = Sort.Direction.DESC) Pageable pageable)
{
    Page<NotificationResponse> notifications=notificationService.getNotificationByReceiverId(receiverId,pageable);

    return ResponseEntity.ok(notifications);
}

@PatchMapping("/{id}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long id)
{
    notificationService.markAsread(id);
    return ResponseEntity.ok("Notification marked as read");
}

@DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id)
{
 notificationService.deleteNotification(id);
 return ResponseEntity.ok("Notification deleted");

}
    @GetMapping("/{receiverId}/unread")
    public ResponseEntity<Page<NotificationResponse>> getUnreadNotifications(
            @PathVariable Long receiverId,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<NotificationResponse> notifications =
                notificationService.getUnreadNotifications(receiverId, pageable);

        return ResponseEntity.ok(notifications);
    }



}
