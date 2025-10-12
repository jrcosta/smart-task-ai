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
 * Dados recebidos ao criar ou atualizar uma tarefa, incluindo informações de
 * status, prioridade e relacionamento com subtarefas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    @NotBlank
    @Size(min = 3, max = 200)
    private String title;
    
    private String description;
    
    private TaskStatus status;
    
    private TaskPriority priority;
    
    private LocalDateTime dueDate;
    
    private Integer estimatedHours;
    
    private Set<String> tags;
    
    private Long parentTaskId;
}
