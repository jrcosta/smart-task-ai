package com.smarttask.dto;

import com.smarttask.model.Task.TaskPriority;
import com.smarttask.model.Task.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Estrutura de transporte para criacao e atualizacao de tarefas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    /** Comprimento minimo aceito para o titulo da tarefa. */
    private static final int MIN_TITLE_LENGTH = 3;

    /** Comprimento maximo permitido para o titulo da tarefa. */
    private static final int MAX_TITLE_LENGTH = 200;

    /**
     * Titulo obrigatorio da tarefa.
     * Deve ter entre 3 e 200 caracteres.
     */
    @NotBlank
    @Size(min = MIN_TITLE_LENGTH, max = MAX_TITLE_LENGTH)
    private String title;

    /**
     * Descricao detalhada da tarefa (opcional).
     */
    private String description;

    /**
     * Status da tarefa (TODO, IN_PROGRESS, COMPLETED, CANCELLED).
     * Se nao informado, padrao e TODO.
     */
    private TaskStatus status;

    /**
     * Prioridade da tarefa (LOW, MEDIUM, HIGH, URGENT).
     * Se nao informado, padrao e MEDIUM.
     */
    private TaskPriority priority;

    /**
     * Data e hora de vencimento da tarefa (opcional).
     */
    private LocalDateTime dueDate;

    /**
     * Estimativa de horas necessarias para concluir (opcional).
     * Pode ser sugerido pela IA.
     */
    private Integer estimatedHours;

    /**
     * Conjunto de tags para categorizacao (opcional).
     * Permite multiplas tags por tarefa.
     */
    private Set<String> tags;

    /**
     * ID da tarefa pai, para criacao de subtarefas (opcional).
     * Se informado, a tarefa sera criada como subtarefa.
     */
    private Long parentTaskId;
}
