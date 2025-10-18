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
 * Data Transfer Object (DTO) para requisições de criação/atualização de tarefas.
 * 
 * <p>Este DTO é usado para transferir dados da requisição HTTP para o serviço de negócio.
 * Inclui validações de entrada para garantir integridade dos dados.</p>
 * 
 * <p>Uso em endpoints:</p>
 * <ul>
 *   <li>{@code POST /tasks} - Criar nova tarefa</li>
 *   <li>{@code PUT /tasks/{id}} - Atualizar tarefa existente</li>
 *   <li>{@code POST /tasks/ai} - Criar com análise de IA</li>
 * </ul>
 * 
 * @author Smart Task AI Team
 * @version 1.0
 * @since 2025-10
 * @see com.smarttask.controller.TaskController
 * @see com.smarttask.service.TaskService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    /**
     * Título obrigatório da tarefa.
     * Deve ter entre 3 e 200 caracteres.
     */
    @NotBlank
    @Size(min = 3, max = 200)
    private String title;
    
    /**
     * Descrição detalhada da tarefa (opcional).
     */
    private String description;
    
    /**
     * Status da tarefa (TODO, IN_PROGRESS, COMPLETED, CANCELLED).
     * Se não informado, padrão é TODO.
     */
    private TaskStatus status;
    
    /**
     * Prioridade da tarefa (LOW, MEDIUM, HIGH, URGENT).
     * Se não informado, padrão é MEDIUM.
     */
    private TaskPriority priority;
    
    /**
     * Data e hora de vencimento da tarefa (opcional).
     */
    private LocalDateTime dueDate;
    
    /**
     * Estimativa de horas necessárias para concluir (opcional).
     * Pode ser sugerido pela IA.
     */
    private Integer estimatedHours;
    
    /**
     * Conjunto de tags para categorização (opcional).
     * Permite múltiplas tags por tarefa.
     */
    private Set<String> tags;
    
    /**
     * ID da tarefa pai, para criação de subtarefas (opcional).
     * Se informado, a tarefa será criada como subtarefa.
     */
    private Long parentTaskId;
}
