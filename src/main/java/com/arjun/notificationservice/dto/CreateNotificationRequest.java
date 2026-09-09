package com.arjun.notificationservice.dto;

import com.arjun.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    @NotNull
    private Long senderId;
    @NotNull
    private Long receiverId;
    @NotBlank
    private String title;
    @NotBlank
    private String message;
    @NotNull
    private NotificationType type;

}
