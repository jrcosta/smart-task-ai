package com.smarttask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidade que registra as configurações de notificação via WhatsApp para cada usuário.
 */
@Entity
@Table(name = "notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "whatsapp_number")
    private String whatsappNumber;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = false;

    @Column(name = "daily_reminder_time")
    private LocalTime dailyReminderTime;

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "America/Sao_Paulo";

    @Column(name = "send_overdue_alerts")
    @Builder.Default
    private Boolean sendOverdueAlerts = true;

    @Column(name = "send_completion_summary")
    @Builder.Default
    private Boolean sendCompletionSummary = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
