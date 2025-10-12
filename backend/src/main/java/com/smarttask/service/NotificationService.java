package com.smarttask.service;

import com.smarttask.dto.NotificationPreferenceRequest;
import com.smarttask.dto.NotificationPreferenceResponse;
import com.smarttask.exception.ResourceNotFoundException;
import com.smarttask.model.NotificationPreference;
import com.smarttask.model.Task;
import com.smarttask.model.User;
import com.smarttask.repository.NotificationPreferenceRepository;
import com.smarttask.repository.TaskRepository;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Orquestra preferências e rotinas agendadas de notificações via WhatsApp,
 * integrando repositórios e o serviço de envio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final WhatsAppService whatsAppService;

    @Transactional
    public NotificationPreferenceResponse savePreferences(NotificationPreferenceRequest request, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        NotificationPreference preference = preferenceRepository.findByUserId(user.getId())
                .orElse(NotificationPreference.builder()
                        .user(user)
                        .build());

        String sanitizedNumber = request.getWhatsappNumber() != null ? request.getWhatsappNumber().trim() : null;

        if (Boolean.TRUE.equals(request.getEnabled()) && !StringUtils.hasText(sanitizedNumber)) {
            throw new IllegalStateException("Configure um número de WhatsApp válido antes de ativar as notificações.");
        }

        preference.setWhatsappNumber(StringUtils.hasText(sanitizedNumber) ? sanitizedNumber : null);
        preference.setEnabled(request.getEnabled() != null ? request.getEnabled() : false);
        
        if (request.getDailyReminderTime() != null && !request.getDailyReminderTime().isEmpty()) {
            preference.setDailyReminderTime(LocalTime.parse(request.getDailyReminderTime()));
        }
        
        if (request.getTimezone() != null) {
            preference.setTimezone(request.getTimezone());
        }
        
        if (request.getSendOverdueAlerts() != null) {
            preference.setSendOverdueAlerts(request.getSendOverdueAlerts());
        }
        
        if (request.getSendCompletionSummary() != null) {
            preference.setSendCompletionSummary(request.getSendCompletionSummary());
        }

        preference = preferenceRepository.save(preference);
        return mapToResponse(preference);
    }

    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreferences(UserPrincipal currentUser) {
        NotificationPreference preference = preferenceRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification preferences not found"));
        
        return mapToResponse(preference);
    }

    public void sendTestNotification(UserPrincipal currentUser) {
        NotificationPreference preference = preferenceRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification preferences not found"));

        if (!StringUtils.hasText(preference.getWhatsappNumber())) {
            throw new IllegalStateException("Número do WhatsApp não configurado para este usuário.");
        }

        whatsAppService.sendTestMessage(
            preference.getWhatsappNumber(),
            currentUser.getUsername()
        );
    }

    // Executar a cada minuto para verificar se há notificações para enviar
    @Scheduled(cron = "0 * * * * *") // A cada minuto
    @Transactional
    public void sendScheduledNotifications() {
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
        
        List<NotificationPreference> preferences = preferenceRepository.findByDailyReminderTime(currentTime);
        
    log.info("Verificando notificações agendadas às {}: {} usuários encontrados", currentTime, preferences.size());
        
        for (NotificationPreference preference : preferences) {
            try {
                sendDailyReminder(preference);
            } catch (Exception e) {
                log.error("Erro ao enviar notificação para o usuário {}: {}",
                        preference.getUser().getId(), e.getMessage());
            }
        }
    }

    // Verificar tarefas atrasadas a cada 6 horas
    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void sendOverdueAlerts() {
        List<NotificationPreference> preferences = preferenceRepository.findAllEnabled();
        
    log.info("Verificando tarefas atrasadas: {} usuários com notificações habilitadas", preferences.size());
        
        for (NotificationPreference preference : preferences) {
            if (!preference.getSendOverdueAlerts()) {
                continue;
            }
            
            try {
                List<Task> overdueTasks = taskRepository.findOverdueTasks(
                    preference.getUser().getId(), 
                    LocalDateTime.now()
                );
                
                if (!overdueTasks.isEmpty()) {
                    if (!StringUtils.hasText(preference.getWhatsappNumber())) {
                        log.warn("Usuário {} possui alertas habilitados, mas sem número de WhatsApp cadastrado.",
                                preference.getUser().getId());
                        continue;
                    }

                    whatsAppService.sendOverdueAlert(
                        preference.getWhatsappNumber(),
                        preference.getUser().getUsername(),
                        overdueTasks
                    );
                }
            } catch (Exception e) {
                log.error("Erro ao enviar alerta de atraso para o usuário {}: {}",
                        preference.getUser().getId(), e.getMessage());
            }
        }
    }

    private void sendDailyReminder(NotificationPreference preference) {
        User user = preference.getUser();

        if (!StringUtils.hasText(preference.getWhatsappNumber())) {
            log.warn("Usuário {} possui lembretes diários agendados sem número de WhatsApp cadastrado.",
                    user.getId());
            return;
        }
        
        // Buscar tarefas pendentes e em progresso
        List<Task> todoTasks = taskRepository.findByUserIdAndStatus(
            user.getId(), 
            Task.TaskStatus.TODO
        );
        
        List<Task> inProgressTasks = taskRepository.findByUserIdAndStatus(
            user.getId(), 
            Task.TaskStatus.IN_PROGRESS
        );
        
        // Combinar as listas
        todoTasks.addAll(inProgressTasks);
        
        // Ordenar por prioridade (URGENT > HIGH > MEDIUM > LOW)
        todoTasks.sort((t1, t2) -> {
            int priority1 = getPriorityValue(t1.getPriority());
            int priority2 = getPriorityValue(t2.getPriority());
            return Integer.compare(priority2, priority1);
        });
        
        whatsAppService.sendDailyTaskReminder(
            preference.getWhatsappNumber(),
            user.getUsername(),
            todoTasks
        );
        
    log.info("Lembrete diário enviado para o usuário {} com {} tarefas", user.getId(), todoTasks.size());
    }

    private int getPriorityValue(Task.TaskPriority priority) {
        switch (priority) {
            case URGENT: return 4;
            case HIGH: return 3;
            case MEDIUM: return 2;
            case LOW: return 1;
            default: return 0;
        }
    }

    private NotificationPreferenceResponse mapToResponse(NotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
                .id(preference.getId())
                .whatsappNumber(preference.getWhatsappNumber())
                .enabled(preference.getEnabled())
                .dailyReminderTime(preference.getDailyReminderTime() != null ? 
                                   preference.getDailyReminderTime().toString() : null)
                .timezone(preference.getTimezone())
                .sendOverdueAlerts(preference.getSendOverdueAlerts())
                .sendCompletionSummary(preference.getSendCompletionSummary())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}
