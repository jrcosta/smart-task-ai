package com.smarttask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Estrutura de entrada para configurar preferencias de notificacoes via
 * WhatsApp.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceRequest {

    /** Mensagem apresentada quando o numero de WhatsApp nao foi informado. */
    private static final String WHATSAPP_REQUIRED_MESSAGE =
            "Numero do WhatsApp e obrigatorio";

    /** Orientacao exibida quando o numero informado possui formato invalido. */
    private static final String WHATSAPP_INVALID_MESSAGE =
            "Numero de WhatsApp invalido (use formato internacional: "
                    + "+5511999999999)";

    /** Alerta retornado quando o horario informado nao segue o padrao HH:mm. */
    private static final String TIME_INVALID_MESSAGE =
            "Horario invalido (use formato HH:mm)";

    /** Numero de telefone em formato E.164 para envio de notificacoes. */
    @NotBlank(message = WHATSAPP_REQUIRED_MESSAGE)
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = WHATSAPP_INVALID_MESSAGE)
    private String whatsappNumber;

    /** Indica se o envio de notificacoes esta habilitado. */
    private Boolean enabled;

    /** Horario diario preferencial para envio de lembretes. */
    @Pattern(
            regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = TIME_INVALID_MESSAGE)
    private String dailyReminderTime;

    /** Fuso horario escolhido pelo usuario para programar lembretes. */
    private String timezone;

    /** Define se alertas de tarefas atrasadas devem ser enviados. */
    private Boolean sendOverdueAlerts;

    /** Determina o envio de resumo diario de tarefas concluidas. */
    private Boolean sendCompletionSummary;
}
