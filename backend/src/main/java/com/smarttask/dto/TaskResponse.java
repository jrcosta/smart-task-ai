package com.smarttask.dto;

import com.smarttask.model.Task.TaskPriority;
import com.smarttask.model.Task.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Representação de saída das tarefas para o frontend, incluindo metadados
 * calculados e informações fornecidas pela IA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    /** Identificador único da tarefa. */
    private Long id;

    /** Título descritivo cadastrado pelo usuário. */
    private String title;

    /** Descrição detalhada da tarefa. */
    private String description;

    /** Estado atual do fluxo de trabalho da tarefa. */
    private TaskStatus status;

    /** Prioridade definida manualmente ou sugerida pela IA. */
    private TaskPriority priority;

    /** Data limite configurada para conclusão. */
    private LocalDateTime dueDate;

    /** Data e hora em que a tarefa foi finalizada. */
    private LocalDateTime completedAt;

    /** Estimativa de esforço em horas informada pelo usuário. */
    private Integer estimatedHours;

    /** Horas efetivamente registradas após conclusão. */
    private Integer actualHours;

    /** Etiquetas categorizando a tarefa. */
    private Set<String> tags;

    /** Identificador da tarefa pai quando aplicável. */
    private Long parentTaskId;

    /** Quantidade de subtarefas diretamente associadas. */
    private Integer subtaskCount;

    /** Indica se a prioridade foi ajustada pela IA. */
    private Boolean aiSuggestedPriority;

    /** Texto analítico produzido pelo módulo de IA. */
    private String aiAnalysis;

    /** Momento de criação do registro. */
    private LocalDateTime createdAt;

    /** Momento da última atualização persistida. */
    private LocalDateTime updatedAt;
}
