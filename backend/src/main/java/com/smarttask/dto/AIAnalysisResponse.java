package com.smarttask.dto;

import com.smarttask.model.Task.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resultado retornado pela camada de IA, incluindo resumos, sugestões de
 * prioridade, horas estimadas e subtarefas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResponse {
    
    private String summary;
    private TaskPriority suggestedPriority;
    private Integer estimatedHours;
    private List<String> suggestedTags;
    private List<String> suggestedSubtasks;
    private String analysis;
}
