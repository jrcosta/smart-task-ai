package com.smarttask.service;

import com.smarttask.dto.AIAnalysisRequest;
import com.smarttask.dto.AIAnalysisResponse;
import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.exception.ResourceNotFoundException;
import com.smarttask.model.Task;
import com.smarttask.model.Task.TaskPriority;
import com.smarttask.model.Task.TaskStatus;
import com.smarttask.model.User;
import com.smarttask.observability.MetricsService;
import com.smarttask.observability.Traced;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.UserPrincipal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquestra o ciclo de vida das tarefas e integrações relacionadas.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    /** Mensagem retornada quando o usuario nao e encontrado. */
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    /** Mensagem utilizada quando a tarefa pai nao existe. */
    private static final String PARENT_TASK_NOT_FOUND_MESSAGE =
            "Parent task not found";

    /** Mensagem padrao para tarefa inexistente. */
    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found";

    /** Aviso de acesso negado a tarefas que nao pertencem ao usuario. */
    private static final String ACCESS_DENIED_MESSAGE =
            "You don't have permission to access this task";

    /** Aviso para operacoes de atualizacao sem permissao. */
    private static final String UPDATE_DENIED_MESSAGE =
            "You don't have permission to update this task";

    /** Aviso para operacoes de remocao sem permissao. */
    private static final String DELETE_DENIED_MESSAGE =
            "You don't have permission to delete this task";

    /** Repositório para acesso a dados de tarefas. */
    private final TaskRepository taskRepository;

    /** Repositório para acesso a dados de usuários. */
    private final UserRepository userRepository;

    /** Serviço de IA para análise das tarefas. */
    private final AIService aiService;

    /** Serviço de métricas utilizado para registrar eventos de tarefas. */
    private final MetricsService metricsService;

    /**
     * Cria uma nova tarefa para o usuário autenticado.
     *
     * @param request dados da tarefa
     * @param currentUser usuário autenticado
     * @return resposta serializada da tarefa criada
     */
    @Transactional
    @Traced(value = "TaskService.createTask", captureParameters = true)
    public TaskResponse createTask(final TaskRequest request,
            final UserPrincipal currentUser) {
        final long startTime = System.currentTimeMillis();
        final User user = findUser(currentUser.getId());

        final Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(resolveStatus(request.getStatus()))
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .tags(request.getTags())
                .user(user)
                .build();

        attachParentTask(request.getParentTaskId(), task);

        final Task savedTask = taskRepository.save(task);
        metricsService.recordTaskCreated(savedTask.getPriority().toString());
        recordDuration(startTime, "create");
        return mapToResponse(savedTask);
    }

    /**
     * Cria uma tarefa executando análise prévia com IA.
     *
     * @param request dados da tarefa
     * @param currentUser usuário autenticado
     * @return resposta serializada da tarefa criada
     */
    @Transactional
    @Traced(value = "TaskService.createTaskWithAI", captureParameters = true)
    public TaskResponse createTaskWithAI(final TaskRequest request,
            final UserPrincipal currentUser) {
        final long startTime = System.currentTimeMillis();
        final AIAnalysisResponse analysis = analyzeTask(request, currentUser);
        final User user = findUser(currentUser.getId());

        final Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(resolveStatus(request.getStatus()))
                .priority(resolvePriority(request, analysis))
                .dueDate(request.getDueDate())
                .estimatedHours(resolveEstimatedHours(request, analysis))
                .tags(resolveTags(request, analysis))
                .aiSuggestedPriority(true)
                .aiAnalysis(analysis.getAnalysis())
                .user(user)
                .build();

        final Task savedTask = taskRepository.save(task);
        createSuggestedSubtasks(analysis, user, savedTask);

        metricsService.recordTaskCreated(savedTask.getPriority().toString());
        recordDuration(startTime, "create_with_ai");
        final Task persistedTask = taskRepository.findById(savedTask.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        TASK_NOT_FOUND_MESSAGE));
        return mapToResponse(persistedTask);
    }

    /**
     * Lista todas as tarefas do usuário autenticado.
     *
     * @param currentUser usuário autenticado
     * @return lista de tarefas serializadas
     */
    @Transactional(readOnly = true)
    @Traced("TaskService.getAllTasks")
    public List<TaskResponse> getAllTasks(final UserPrincipal currentUser) {
        final long startTime = System.currentTimeMillis();
        final List<TaskResponse> tasks = taskRepository
                .findByUserId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        recordDuration(startTime, "list_all");
        return tasks;
    }

    /**
     * Lista tarefas do usuário por status.
     *
     * @param status status desejado
     * @param currentUser usuário autenticado
     * @return lista filtrada de tarefas
     */
    @Transactional(readOnly = true)
    @Traced("TaskService.getTasksByStatus")
    public List<TaskResponse> getTasksByStatus(final TaskStatus status,
            final UserPrincipal currentUser) {
        return taskRepository.findByUserIdAndStatus(currentUser.getId(), status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Recupera uma tarefa específica do usuário autenticado.
     *
     * @param taskId identificador da tarefa solicitada
     * @param currentUser usuário autenticado
     * @return tarefa encontrada
     */
    @Transactional(readOnly = true)
    @Traced("TaskService.getTaskById")
    public TaskResponse getTaskById(final Long taskId,
            final UserPrincipal currentUser) {
        final Task task = findTask(taskId);
        ensureOwnership(task, currentUser, ACCESS_DENIED_MESSAGE);
        return mapToResponse(task);
    }

    /**
     * Atualiza os dados de uma tarefa do usuário.
     *
     * @param taskId identificador da tarefa
     * @param request dados para atualização
     * @param currentUser usuário autenticado
     * @return tarefa atualizada
     */
    @Transactional
    @Traced(value = "TaskService.updateTask", captureParameters = true)
    public TaskResponse updateTask(final Long taskId, final TaskRequest request,
            final UserPrincipal currentUser) {
        final long startTime = System.currentTimeMillis();
        final Task task = findTask(taskId);
        ensureOwnership(task, currentUser, UPDATE_DENIED_MESSAGE);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setTags(request.getTags());

        if (request.getStatus() == TaskStatus.COMPLETED
                && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
            metricsService.recordTaskCompleted();
        }

        final Task updatedTask = taskRepository.save(task);
        recordDuration(startTime, "update");
        return mapToResponse(updatedTask);
    }

    /**
     * Remove uma tarefa pertencente ao usuário autenticado.
     *
     * @param taskId identificador da tarefa
     * @param currentUser usuário autenticado
     */
    @Transactional
    @Traced("TaskService.deleteTask")
    public void deleteTask(final Long taskId, final UserPrincipal currentUser) {
        final long startTime = System.currentTimeMillis();
        final Task task = findTask(taskId);
        ensureOwnership(task, currentUser, DELETE_DENIED_MESSAGE);

        taskRepository.delete(task);
        metricsService.recordTaskDeleted();
        recordDuration(startTime, "delete");
    }

    /**
     * Lista tarefas atrasadas do usuário autenticado.
     *
     * @param currentUser usuário autenticado
     * @return lista de tarefas em atraso
     */
    @Transactional(readOnly = true)
    @Traced("TaskService.getOverdueTasks")
    public List<TaskResponse> getOverdueTasks(final UserPrincipal currentUser) {
        return taskRepository
                .findOverdueTasks(currentUser.getId(), LocalDateTime.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void attachParentTask(final Long parentTaskId, final Task task) {
        if (parentTaskId == null) {
            return;
        }

        final Task parentTask = taskRepository.findById(parentTaskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PARENT_TASK_NOT_FOUND_MESSAGE));
        task.setParentTask(parentTask);
    }

    private Task findTask(final Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        TASK_NOT_FOUND_MESSAGE));
    }

    private User findUser(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        USER_NOT_FOUND_MESSAGE));
    }

    private void ensureOwnership(
            final Task task,
            final UserPrincipal currentUser,
            final String message) {
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException(message);
        }
    }

    private TaskStatus resolveStatus(final TaskStatus status) {
        return status == null ? TaskStatus.TODO : status;
    }

    private TaskResponse mapToResponse(final Task task) {
        final Long parentTaskId = task.getParentTask() == null
                ? null
                : task.getParentTask().getId();

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
                .parentTaskId(parentTaskId)
                .subtaskCount(task.getSubtasks().size())
                .aiSuggestedPriority(task.getAiSuggestedPriority())
                .aiAnalysis(task.getAiAnalysis())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    private AIAnalysisResponse analyzeTask(final TaskRequest request,
            final UserPrincipal currentUser) {
        final AIAnalysisRequest aiRequest = new AIAnalysisRequest();
        final String description = request.getDescription() == null ? ""
                : request.getDescription();
        aiRequest.setText(request.getTitle() + " " + description);
        return aiService.analyzeTask(aiRequest, currentUser.getId());
    }

    private TaskPriority resolvePriority(final TaskRequest request,
            final AIAnalysisResponse analysis) {
        return request.getPriority() == null
                ? analysis.getSuggestedPriority()
                : request.getPriority();
    }

    private Integer resolveEstimatedHours(final TaskRequest request,
            final AIAnalysisResponse analysis) {
        return request.getEstimatedHours() == null
                ? analysis.getEstimatedHours()
                : request.getEstimatedHours();
    }

    private Set<String> resolveTags(final TaskRequest request,
            final AIAnalysisResponse analysis) {
        if (request.getTags() != null) {
            return request.getTags();
        }
        return new HashSet<>(analysis.getSuggestedTags());
    }

    private void createSuggestedSubtasks(final AIAnalysisResponse analysis,
            final User user, final Task parentTask) {
        if (analysis.getSuggestedSubtasks() == null
                || analysis.getSuggestedSubtasks().isEmpty()) {
            return;
        }

        for (final String title : analysis.getSuggestedSubtasks()) {
            final Task subtask = Task.builder()
                    .title(title)
                    .status(TaskStatus.TODO)
                    .priority(parentTask.getPriority())
                    .user(user)
                    .parentTask(parentTask)
                    .build();
            taskRepository.save(subtask);
        }
    }

    private void recordDuration(final long startTime, final String operation) {
        metricsService.recordTaskDuration(
                System.currentTimeMillis() - startTime, operation);
    }
}
