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
    
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private Integer estimatedHours;
    private Integer actualHours;
    private Set<String> tags;
    private Long parentTaskId;
    private Integer subtaskCount;
    private Boolean aiSuggestedPriority;
    private String aiAnalysis;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
