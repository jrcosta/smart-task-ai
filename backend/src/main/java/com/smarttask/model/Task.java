package com.smarttask.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entidade JPA que representa uma tarefa do sistema Smart Task Manager.
 *
 * <p>Uma tarefa contem informacoes sobre uma atividade a ser realizada por um
 * usuario, incluindo:</p>
 * <ul>
 *   <li>Título e descrição detalhada</li>
 *   <li>Status (TODO, IN_PROGRESS, COMPLETED, CANCELLED)</li>
 *   <li>Prioridade (LOW, MEDIUM, HIGH, URGENT)</li>
 *   <li>Datas de vencimento e conclusão</li>
 *   <li>Estimativa de horas necessárias</li>
 *   <li>Tags para categorização</li>
 *   <li>Sugestões de IA para prioridade e análise</li>
 *   <li>Subtarefas associadas</li>
 * </ul>
 *
 * <p>A entidade mantém relacionamento com:</p>
 * <ul>
 *   <li>{@link User} - Proprietário da tarefa</li>
 *   <li>{@link Task} - Tarefas pai (para subtarefas)</li>
 * </ul>
 *
 * <p>Usa {@link AuditingEntityListener} para rastreamento automático de datas
 * de criação e modificação.</p>
 *
 * @author Smart Task AI Team
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Task {

    /** Tamanho mínimo permitido para o título da tarefa. */
    public static final int TITLE_MIN_LENGTH = 3;

    /** Tamanho máximo permitido para o título da tarefa. */
    public static final int TITLE_MAX_LENGTH = 200;

    /**
     * Identificador único da tarefa.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título da tarefa (obrigatório).
     * Deve ter entre 3 e 200 caracteres.
     */
    @NotBlank
    @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH)
    @Column(nullable = false)
    private String title;

    /**
     * Descrição detalhada da tarefa.
     * Pode conter até 10.000 caracteres de texto livre.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Status atual da tarefa.
     * Valores possíveis: TODO, IN_PROGRESS, COMPLETED, CANCELLED.
     * Padrão: TODO
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    /**
     * Prioridade da tarefa.
     * Valores possíveis: LOW, MEDIUM, HIGH, URGENT.
     * Padrão: MEDIUM
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    /**
     * Data e hora de vencimento da tarefa.
     * Utilizado para alertas de atraso.
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * Data e hora de conclusão da tarefa.
     * Preenchido automaticamente quando status = COMPLETED.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Estimativa de horas necessárias para concluir a tarefa.
     * Sugerido pela IA durante análise.
     */
    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    /**
     * Horas reais gastas na conclusão da tarefa.
     * Atualizado manualmente pelo usuário.
     */
    @Column(name = "actual_hours")
    private Integer actualHours;

    /**
     * Conjunto de tags para categorização da tarefa.
     * Permite múltiplas tags por tarefa.
     */
    @ElementCollection
    @CollectionTable(
        name = "task_tags",
        joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "tag")
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    /**
     * Usuário proprietário desta tarefa.
     * Relacionamento obrigatório com {@link User}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tarefa pai desta tarefa (em caso de subtarefa).
     * Nulo se a tarefa for uma tarefa de nível superior.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    /**
     * Conjunto de subtarefas associadas a esta tarefa.
     * Deletadas em cascata quando a tarefa pai é removida.
     */
    @OneToMany(
        mappedBy = "parentTask",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @Builder.Default
    private Set<Task> subtasks = new HashSet<>();

    /**
     * Indica se a prioridade foi sugerida pela IA.
     * True se a sugestão foi aplicada, false caso contrário.
     */
    @Column(name = "ai_suggested_priority")
    private Boolean aiSuggestedPriority;

    /**
     * Análise completa da tarefa gerada pela IA.
     * Contém recomendações e insights sobre a tarefa.
     */
    @Column(name = "ai_analysis", columnDefinition = "TEXT")
    private String aiAnalysis;

    /**
     * Data e hora de criação da tarefa.
     * Preenchida automaticamente pelo sistema.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última modificação da tarefa.
     * Atualizada automaticamente a cada mudança.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enumeração dos status possíveis para uma tarefa.
     */
    public enum TaskStatus {
        /** Tarefa ainda não iniciada. */
        TODO,
        /** Tarefa em andamento. */
        IN_PROGRESS,
        /** Tarefa concluída com sucesso. */
        COMPLETED,
        /** Tarefa cancelada. */
        CANCELLED
    }

    /** Enumeração das prioridades disponíveis para as tarefas. */
    public enum TaskPriority {
        /** Prioridade baixa. */
        LOW,
        /** Prioridade média. */
        MEDIUM,
        /** Prioridade alta. */
        HIGH,
        /** Prioridade urgente. */
        URGENT
    }
}
