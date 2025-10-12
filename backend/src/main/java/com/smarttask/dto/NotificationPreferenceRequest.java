package com.smarttask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Estrutura de entrada para configurar preferências de notificações via WhatsApp.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceRequest {
    
    @NotBlank(message = "Número do WhatsApp é obrigatório")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Número de WhatsApp inválido (use formato internacional: +5511999999999)")
    private String whatsappNumber;
    
    private Boolean enabled;
    
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Horário inválido (use formato HH:mm)")
    private String dailyReminderTime; // Formato: "08:30"
    
    private String timezone;
    
    private Boolean sendOverdueAlerts;
    
    private Boolean sendCompletionSummary;
}
