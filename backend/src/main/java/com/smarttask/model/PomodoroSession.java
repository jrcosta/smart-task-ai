package com.smarttask.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entidade que representa sessões de pomodoro associadas a tarefas e usuários.
 */
@Entity
@Table(name = "pomodoro_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PomodoroSession {

    /** Identificador técnico da sessão registrada. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Referência à tarefa que originou a sessão. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /** Usuário responsável por executar a sessão. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Duração planejada da sessão em minutos. */
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    /** Indica se a sessão foi concluída com sucesso. */
    @Column(name = "completed", nullable = false)
    @Builder.Default
    private Boolean completed = false;

    /** Momento em que a sessão foi iniciada. */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /** Momento em que a sessão foi encerrada. */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /** Data de criação deste registro. */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
