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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para operacoes de tarefas vinculadas ao usuario autenticado.
 */
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public final class TaskController {

    /** Servico de negocio responsavel pelas operacoes de tarefas. */
    private final TaskService taskService;

    /**
     * Cria uma nova tarefa.
     *
     * @param request dados recebidos do cliente
     * @param currentUser usuario autenticado
     * @return resposta HTTP 201 com a tarefa criada
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody final TaskRequest request,
            @AuthenticationPrincipal final UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request, currentUser));
    }

    /**
     * Cria uma tarefa com analise de IA que sugere prioridade e tags.
     * Subtarefas sao propostas automaticamente a partir da analise.
     *
     * @param request dados recebidos do cliente
     * @param currentUser usuario autenticado
     * @return resposta HTTP 201 com a tarefa enriquecida pela analise de IA
     */
    @PostMapping("/ai")
    public ResponseEntity<TaskResponse> createTaskWithAI(
            @Valid @RequestBody final TaskRequest request,
            @AuthenticationPrincipal final UserPrincipal currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTaskWithAI(request, currentUser));
    }

    /**
     * Lista todas as tarefas do usuario autenticado.
     *
     * @param currentUser usuario autenticado
     * @return resposta HTTP 200 com lista de tarefas
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @AuthenticationPrincipal final UserPrincipal currentUser) {
    return ResponseEntity.ok(
        taskService.getAllTasks(currentUser));
    }

    /**
     * Lista tarefas filtradas por status para o usuario autenticado.
     *
     * @param status status desejado (TODO, IN_PROGRESS, COMPLETED, CANCELLED)
     * @param currentUser usuario autenticado
     * @return resposta HTTP 200 com tarefas do status informado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @PathVariable final TaskStatus status,
            @AuthenticationPrincipal final UserPrincipal currentUser) {
    return ResponseEntity.ok(
        taskService.getTasksByStatus(status, currentUser));
    }

    /**
     * Consulta as tarefas atrasadas do usuario autenticado.
     *
     * @param currentUser usuario autenticado
     * @return lista de tarefas em atraso
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(
            @AuthenticationPrincipal final UserPrincipal currentUser) {
    return ResponseEntity.ok(
        taskService.getOverdueTasks(currentUser));
    }

    /**
     * Busca uma tarefa especifica pelo identificador.
     *
     * @param id identificador da tarefa
     * @param currentUser usuario autenticado
     * @return tarefa localizada para o usuario
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable final Long id,
            @AuthenticationPrincipal final UserPrincipal currentUser) {
    return ResponseEntity.ok(
        taskService.getTaskById(id, currentUser));
    }

    /**
     * Atualiza os dados de uma tarefa existente.
     *
     * @param id identificador da tarefa
     * @param request novos dados informados pelo cliente
     * @param currentUser usuario autenticado
     * @return tarefa atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable final Long id,
            @Valid @RequestBody final TaskRequest request,
            @AuthenticationPrincipal final UserPrincipal currentUser) {
    return ResponseEntity.ok(
        taskService.updateTask(id, request, currentUser));
    }

    /**
     * Remove uma tarefa do usuario autenticado.
     *
     * @param id identificador da tarefa
     * @param currentUser usuario autenticado
     * @return resposta sem conteudo em caso de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable final Long id,
            @AuthenticationPrincipal final UserPrincipal currentUser) {
        taskService.deleteTask(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
