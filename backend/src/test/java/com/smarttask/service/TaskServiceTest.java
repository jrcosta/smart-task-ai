package com.smarttask.service;

import com.smarttask.model.Task;
import com.smarttask.repository.TaskRepository;
import com.smarttask.dto.CreateTaskRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AIService aiService;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_devePersistirTarefa_eInvocarAnaliseIA() {
        // Arrange
        CreateTaskRequest req = new CreateTaskRequest();
        // ...existing code...
        // Ajuste os setters conforme seu DTO
        // ex:
        // req.setTitle("Implementar autenticação");
        // req.setDescription("Criar endpoint /auth/login e /auth/register");

        String mockAnalysis = "PRIORIDADE: ALTA\nTAGS: auth, jwt\nSUBTAREFAS: 1) endpoint login 2) endpoint register\nANÁLISE: ...\nHORAS: 8";
        when(aiService.analyzeTask(anyString())).thenReturn(mockAnalysis);

        Task saved = new Task();
        // ...existing code...
        // ex:
        // saved.setId(1L);
        // saved.setTitle(req.getTitle());
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        // Act
        Task result = taskService.createTask(req);

        // Assert
        assertThat(result).isNotNull();
        verify(aiService, times(1)).analyzeTask(anyString());
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, times(1)).save(taskCaptor.capture());
        Task toSave = taskCaptor.getValue();
        assertThat(toSave).isNotNull();
        // Opcional: validar algum campo mapeado do DTO para a entidade
        // assertThat(toSave.getTitle()).isEqualTo(req.getTitle());
        verifyNoMoreInteractions(aiService, taskRepository);
        // Métrica chamada (se aplicável)
        // verify(metricsService).recordXxx(...);
    }
}