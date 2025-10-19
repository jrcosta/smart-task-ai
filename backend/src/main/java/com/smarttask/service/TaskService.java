package com.smarttask.service;

import com.smarttask.dto.AIAnalysisRequest;
import com.smarttask.dto.AIAnalysisResponse;
import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.exception.ResourceNotFoundException;
import com.smarttask.model.Task;
import com.smarttask.model.Task.TaskStatus;
import com.smarttask.model.User;
import com.smarttask.observability.MetricsService;
import com.smarttask.observability.Traced;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio de tarefas no sistema.
 * 
 * <p>Este serviço gerencia o ciclo de vida completo das tarefas, incluindo:</p>
 * <ul>
 *   <li>Criação de tarefas com análise opcional de IA</li>
 *   <li>Atualização de status, prioridade e detalhes</li>
 *   <li>Exclusão e exclusão em cascata de subtarefas</li>
 *   <li>Consultas filtradas por usuário, status e prioridade</li>
 *   <li>Integração com {@link AIService} para análise inteligente</li>
 *   <li>Rastreamento de métricas via {@link MetricsService}</li>
 * </ul>
 * 
 * <p>Todos os métodos públicos utilizam a anotação {@link Traced} para observabilidade
 * automática via OpenTelemetry.</p>
 * 
 * @author Smart Task AI Team
 * @version 1.0
 * @since 2025-10
 * @see Task
 * @see AIService
 * @see MetricsService
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    /** Repositório para acesso a dados de tarefas */
    private final TaskRepository taskRepository;
    
    /** Repositório para acesso a dados de usuários */
    private final UserRepository userRepository;
    
    /** Serviço de IA para análise de tarefas */
    private final AIService aiService;
    
    /** Serviço para registrar métricas customizadas */
    private final MetricsService metricsService;

    /**
     * Cria uma nova tarefa para o usuário autenticado.
     * 
     * <p>Se a requisição indicar {@code analyzeWithAI = true}, a tarefa será
     * enviada para análise pela IA, que fornecerá sugestões de prioridade,
     * tags e subtarefas.</p>
     * 
     * @param request DTO com dados da tarefa (título, descrição, prioridade, etc.)
     * @param currentUser Principal do usuário autenticado
     * @return {@link TaskResponse} com os dados da tarefa criada
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional
    @Traced(value = "TaskService.createTask", captureParameters = true)
    public TaskResponse createTask(TaskRequest request, UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .tags(request.getTags())
                .user(user)
                .build();

        if (request.getParentTaskId() != null) {
            Task parentTask = taskRepository.findById(request.getParentTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent task not found"));
            task.setParentTask(parentTask);
        }

        task = taskRepository.save(task);
        
        metricsService.recordTaskCreated(task.getPriority().toString());
        metricsService.recordTaskDuration(System.currentTimeMillis() - startTime, "create");
        
        return mapToResponse(task);
    }

    @Transactional
    @Traced(value = "TaskService.createTaskWithAI", captureParameters = true)
    public TaskResponse createTaskWithAI(TaskRequest request, UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        
        // Analisa a tarefa com IA antes de criar
        AIAnalysisRequest aiRequest = new AIAnalysisRequest();
        aiRequest.setText(request.getTitle() + " " + (request.getDescription() != null ? request.getDescription() : ""));
        
        AIAnalysisResponse aiAnalysis = aiService.analyzeTask(aiRequest, currentUser.getId());

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : aiAnalysis.getSuggestedPriority())
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours() != null ? request.getEstimatedHours() : aiAnalysis.getEstimatedHours())
                .tags(request.getTags() != null ? request.getTags() : aiAnalysis.getSuggestedTags().stream().collect(Collectors.toSet()))
                .aiSuggestedPriority(true)
                .aiAnalysis(aiAnalysis.getAnalysis())
                .user(user)
                .build();

        task = taskRepository.save(task);

        // Cria subtarefas sugeridas pela IA
        if (aiAnalysis.getSuggestedSubtasks() != null && !aiAnalysis.getSuggestedSubtasks().isEmpty()) {
            for (String subtaskTitle : aiAnalysis.getSuggestedSubtasks()) {
                Task subtask = Task.builder()
                        .title(subtaskTitle)
                        .status(TaskStatus.TODO)
                        .priority(task.getPriority())
                        .user(user)
                        .parentTask(task)
                        .build();
                taskRepository.save(subtask);
            }
        }

        metricsService.recordTaskCreated(task.getPriority().toString());
        metricsService.recordTaskDuration(System.currentTimeMillis() - startTime, "create_with_ai");
        
        return mapToResponse(taskRepository.findById(task.getId()).orElseThrow());
    }

    @Transactional(readOnly = true)
    @Traced("TaskService.getAllTasks")
    public List<TaskResponse> getAllTasks(UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        List<TaskResponse> tasks = taskRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        metricsService.recordTaskDuration(System.currentTimeMillis() - startTime, "list_all");
        return tasks;
    }

    @Transactional(readOnly = true)
    @Traced("TaskService.getTasksByStatus")
    public List<TaskResponse> getTasksByStatus(TaskStatus status, UserPrincipal currentUser) {
        return taskRepository.findByUserIdAndStatus(currentUser.getId(), status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Traced("TaskService.getTaskById")
    public TaskResponse getTaskById(Long id, UserPrincipal currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to access this task");
        }

        return mapToResponse(task);
    }

    @Transactional
    @Traced(value = "TaskService.updateTask", captureParameters = true)
    public TaskResponse updateTask(Long id, TaskRequest request, UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setTags(request.getTags());

        if (request.getStatus() == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
            metricsService.recordTaskCompleted();
        }

        task = taskRepository.save(task);
        metricsService.recordTaskDuration(System.currentTimeMillis() - startTime, "update");
        
        return mapToResponse(task);
    }

    @Transactional
    @Traced("TaskService.deleteTask")
    public void deleteTask(Long id, UserPrincipal currentUser) {
        long startTime = System.currentTimeMillis();
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
        metricsService.recordTaskDeleted();
        metricsService.recordTaskDuration(System.currentTimeMillis() - startTime, "delete");
    }

    @Transactional(readOnly = true)
    @Traced("TaskService.getOverdueTasks")
    public List<TaskResponse> getOverdueTasks(UserPrincipal currentUser) {
        return taskRepository.findOverdueTasks(currentUser.getId(), LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .estimatedHours(task.getEstimatedHours())
                .actualHours(task.getActualHours())
                .tags(task.getTags())
                .parentTaskId(task.getParentTask() != null ? task.getParentTask().getId() : null)
                .subtaskCount(task.getSubtasks().size())
                .aiSuggestedPriority(task.getAiSuggestedPriority())
                .aiAnalysis(task.getAiAnalysis())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
