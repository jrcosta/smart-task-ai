package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Resultado retornado ao cliente com as preferências de notificações salvas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceResponse {
    
    private Long id;
    private String whatsappNumber;
    private Boolean enabled;
    private String dailyReminderTime;
    private String timezone;
    private Boolean sendOverdueAlerts;
    private Boolean sendCompletionSummary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
