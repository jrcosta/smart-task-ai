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
    /** Identificador da preferência configurada. */
    private Long id;

    /** Número de WhatsApp associado ao usuário. */
    private String whatsappNumber;

    /** Indica se as notificações estão habilitadas. */
    private Boolean enabled;

    /** Horário diário definido para envio de lembretes. */
    private String dailyReminderTime;

    /** Fuso horário utilizado para calcular lembretes. */
    private String timezone;

    /** Informa se alertas de tarefas atrasadas serão enviados. */
    private Boolean sendOverdueAlerts;

    /** Indica se um resumo diário de tarefas concluídas será enviado. */
    private Boolean sendCompletionSummary;

    /** Data de criação do registro de preferências. */
    private LocalDateTime createdAt;

    /** Última atualização das preferências. */
    private LocalDateTime updatedAt;
}
