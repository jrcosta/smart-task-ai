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
    /** Resumo textual gerado para a tarefa. */
    private String summary;

    /** Prioridade sugerida pela análise de IA. */
    private TaskPriority suggestedPriority;

    /** Estimativa de horas para conclusão da tarefa. */
    private Integer estimatedHours;

    /** Lista de tags recomendadas pela IA. */
    private List<String> suggestedTags;

    /** Subtarefas sugeridas para facilitar a execução. */
    private List<String> suggestedSubtasks;

    /** Análise detalhada com observações adicionais. */
    private String analysis;
}
