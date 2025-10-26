package com.smarttask.service;

import com.smarttask.dto.TaskRequest;
import com.smarttask.model.Task;
import com.smarttask.model.Task.TaskPriority;
import com.smarttask.model.User;
import com.smarttask.observability.MetricsService;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AIService aiService;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_devePersistirTarefa_eRegistrarMetricas() {
        // Arrange
        TaskRequest request = new TaskRequest();
        request.setTitle("Implementar autenticação");
        request.setDescription("Criar endpoints de login e registro");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDateTime.of(2025, 10, 30, 12, 0));
        request.setEstimatedHours(8);
        request.setTags(Set.of("auth", "backend"));

        User user = User.builder()
                .id(1L)
                .username("smartuser")
                .email("user@smarttask.ai")
                .password("hash")
                .roles(new HashSet<>(Set.of("USER")))
                .build();
        UserPrincipal principal = UserPrincipal.create(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task persisted = invocation.getArgument(0, Task.class);
            persisted.setId(42L);
            return persisted;
        });

        // Act
        var response = taskService.createTask(request, principal);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getTitle()).isEqualTo("Implementar autenticação");
        assertThat(response.getPriority()).isEqualTo(TaskPriority.HIGH);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());
        Task capturedTask = taskCaptor.getValue();
        assertThat(capturedTask.getTitle()).isEqualTo(request.getTitle());
        assertThat(capturedTask.getUser()).isEqualTo(user);

        verify(metricsService).recordTaskCreated("HIGH");
        verify(metricsService).recordTaskDuration(anyLong(), eq("create"));
        verifyNoInteractions(aiService);
    }
}