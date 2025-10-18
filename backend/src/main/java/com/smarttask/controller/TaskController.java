package com.smarttask.controller;

import com.smarttask.dto.TaskRequest;
import com.smarttask.dto.TaskResponse;
import com.smarttask.model.Task.TaskStatus;
import com.smarttask.security.UserPrincipal;
import com.smarttask.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que gerencia os endpoints relacionados a tarefas.
 * 
 * <p>Fornece operações CRUD (Create, Read, Update, Delete) completas para tarefas,
 * incluindo:</p>
 * <ul>
 *   <li>Criar tarefas (com ou sem análise de IA)</li>
 *   <li>Listar tarefas (todas ou filtradas por status/prioridade)</li>
 *   <li>Obter tarefa específica</li>
 *   <li>Atualizar status, prioridade e detalhes</li>
 *   <li>Deletar tarefas</li>
 * </ul>
 * 
 * <p>Todos os endpoints requerem autenticação JWT e operam em contexto do
 * usuário autenticado (isolamento de dados por usuário).</p>
 * 
 * <p>Base URL: {@code /tasks}</p>
 * 
 * @author Smart Task AI Team
 * @version 1.0
 * @since 2025-10
 * @see TaskService
 * @see TaskRequest
 * @see TaskResponse
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {

    /** Serviço de negócio para tarefas */
    private final TaskService taskService;

    /**
     * Cria uma nova tarefa.
     * 
     * @param request DTO com dados da tarefa
     * @param currentUser Usuário autenticado
     * @return Resposta HTTP 201 com a tarefa criada
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request, currentUser));
    }

    /**
     * Cria uma nova tarefa com análise de IA.
     * 
     * <p>A IA analisará a tarefa e fornecerá sugestões de prioridade,
     * tags e subtarefas recomendadas.</p>
     * 
     * @param request DTO com dados da tarefa
     * @param currentUser Usuário autenticado
     * @return Resposta HTTP 201 com a tarefa criada e análise de IA
     */
    @PostMapping("/ai")
    public ResponseEntity<TaskResponse> createTaskWithAI(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTaskWithAI(request, currentUser));
    }

    /**
     * Lista todas as tarefas do usuário autenticado.
     * 
     * @param currentUser Usuário autenticado
     * @return Resposta HTTP 200 com lista de tarefas
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(taskService.getAllTasks(currentUser));
    }

    /**
     * Lista tarefas filtradas por status.
     * 
     * @param status Status desejado (TODO, IN_PROGRESS, COMPLETED, CANCELLED)
     * @param currentUser Usuário autenticado
     * @return Resposta HTTP 200 com tarefas do status especificado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @PathVariable TaskStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status, currentUser));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(taskService.getOverdueTasks(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(taskService.getTaskById(id, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(taskService.updateTask(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        taskService.deleteTask(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}