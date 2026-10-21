package com.arjun.notificationservice;

import com.arjun.notificationservice.controller.NotificationController;
import com.arjun.notificationservice.dto.NotificationResponse;
import com.arjun.notificationservice.enums.NotificationStatus;
import com.arjun.notificationservice.enums.NotificationType;
import com.arjun.notificationservice.service.NotificationService;
import com.arjun.notificationservice.dto.CreateNotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    private NotificationService notificationService;

    @Test
    void shouldReturnNotificationsForReceiver() throws Exception {

        //arange
        NotificationResponse response = new NotificationResponse(
                1L,
                10L,
                42L,
                "Test title",
                "Test message",
                LocalDateTime.of(2026, 10, 12, 10, 0),
                NotificationStatus.UNREAD,
                NotificationType.ALERT
        );

        Page<NotificationResponse> page = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 20),
                1
        );

        when(notificationService.getNotificationByReceiverId(
                eq(42L), any()
        )).thenReturn(page);

        // Act: Simulate an HTTP GET request
        mockMvc.perform(get("/api/notifications/42"))

                // Assert: Check HTTP status and all eight DTO fields
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].senderId").value(10))
                .andExpect(jsonPath("$.content[0].receiverId").value(42))
                .andExpect(jsonPath("$.content[0].title").value("Test title"))
                .andExpect(jsonPath("$.content[0].message").value("Test message"))
                .andExpect(jsonPath("$.content[0].createdAt")
                        .value("2026-10-12T10:00:00"))
                .andExpect(jsonPath("$.content[0].status").value("UNREAD"))
                .andExpect(jsonPath("$.content[0].type").value("ALERT"));

        // Verify: Ensure the controller passed the correct receiver ID
        verify(notificationService)
                .getNotificationByReceiverId(eq(42L), any());
    }
    @Test
    void shouldReturnUnreadNotification() throws Exception {

        //arange
        NotificationResponse response = new NotificationResponse(
                1L,
                10L,
                43L,
                "Test title",
                "Test message",
                LocalDateTime.of(2026, 10, 12, 10, 0),
                NotificationStatus.UNREAD,
                NotificationType.ALERT
        );
        Page<NotificationResponse> page = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 20),
                1
        );
        // Arrange: create response, page, and mock behavior
        when(notificationService.getUnreadNotifications(eq(43L), any()))
                .thenReturn(page);

// Act + Assert: make request and check response
        mockMvc.perform(get("/api/notifications/43/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].receiverId").value(43))
                .andExpect(jsonPath("$.content[0].status").value("UNREAD"))
                .andExpect(jsonPath("$.content[0].type").value("ALERT"));

// Verify: check the correct service method was called
        verify(notificationService)
                .getUnreadNotifications(eq(43L), any());


    }


    @Test
    void shouldMarkNotificationAsRead() throws Exception {
        // Act + Assert
        mockMvc.perform(patch("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification marked as read"));

        // Verify
        verify(notificationService).markAsread(1L);
    }


    @Test
    void shouldCreateNotification() throws Exception {
        // Arrange: create the incoming request
        CreateNotificationRequest request = new CreateNotificationRequest(
                10L,
                42L,
                "Test title",
                "Test message",
                NotificationType.ALERT
        );

        // Arrange: prepare the response the mocked service should return
        NotificationResponse response = new NotificationResponse(
                1L,
                10L,
                42L,
                "Test title",
                "Test message",
                LocalDateTime.of(2026, 10, 16, 10, 0),
                NotificationStatus.UNREAD,
                NotificationType.ALERT
        );

        when(notificationService.createNotification(
                any(CreateNotificationRequest.class)
        )).thenReturn(response);

        // Act: send a POST request containing JSON
        mockMvc.perform(post("/api/notifications")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Assert: check the HTTP response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderId").value(10))
                .andExpect(jsonPath("$.receiverId").value(42))
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.message").value("Test message"))
                .andExpect(jsonPath("$.status").value("UNREAD"))
                .andExpect(jsonPath("$.type").value("ALERT"));

        // Verify: ensure the controller called the service
        verify(notificationService).createNotification(
                any(CreateNotificationRequest.class)
        );
    }
    @Test
    void shouldRejectInvalidNotificationRequest() throws Exception {
        CreateNotificationRequest request = new CreateNotificationRequest(
                10L,
                42L,
                "", // Invalid: title cannot be blank
                "Test message",
                NotificationType.ALERT
        );

        mockMvc.perform(post("/api/notifications")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(notificationService, never())
                .createNotification(any(CreateNotificationRequest.class));
    }



}


