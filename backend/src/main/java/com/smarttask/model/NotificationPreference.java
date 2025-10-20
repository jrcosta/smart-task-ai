package com.smarttask.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entidade que registra as configurações de notificação via WhatsApp para cada
 * usuário.
 */
@Entity
@Table(name = "notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotificationPreference {

    /** Fuso horário utilizado por padrão nas notificações. */
    public static final String DEFAULT_TIMEZONE = "America/Sao_Paulo";

    /** Identificador técnico da preferência configurada. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuário ao qual a configuração pertence. */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Número do WhatsApp que receberá notificações. */
    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    /** Indica se o envio de notificações está habilitado. */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = false;

    /** Horário diário para envio de lembretes. */
    @Column(name = "daily_reminder_time")
    private LocalTime dailyReminderTime;

    /** Fuso horário associado aos lembretes do usuário. */
    @Column(name = "timezone")
    @Builder.Default
    private String timezone = DEFAULT_TIMEZONE;

    /** Indica se alertas de tarefas atrasadas devem ser enviados. */
    @Column(name = "send_overdue_alerts")
    @Builder.Default
    private Boolean sendOverdueAlerts = true;

    /** Indica se um resumo de conclusão diário deve ser enviado. */
    @Column(name = "send_completion_summary")
    @Builder.Default
    private Boolean sendCompletionSummary = true;

    /** Data de criação deste registro. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Data da última atualização desta configuração. */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
