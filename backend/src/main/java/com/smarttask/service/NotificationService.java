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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Orquestra preferências e rotinas agendadas de notificações via WhatsApp,
 * integrando repositórios e o serviço de envio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    /** Expressao cron para execucoes a cada minuto. */
    private static final String CRON_EVERY_MINUTE = "0 * * * * *";

    /** Expressao cron para execucoes a cada seis horas. */
    private static final String CRON_EVERY_SIX_HOURS = "0 0 */6 * * *";

    /** Mensagem exibida quando o numero de WhatsApp nao foi informado. */
    private static final String VALID_NUMBER_MESSAGE =
            "Configure um numero de WhatsApp valido antes de ativar as "
                    + "notificacoes.";

    /** Ordenador que prioriza tarefas por maior prioridade primeiro. */
    private static final Comparator<Task> PRIORITY_COMPARATOR =
            Comparator.comparing(
                    Task::getPriority,
                    Comparator.nullsLast(Comparator.naturalOrder()))
                    .reversed();

    /** Repositório de preferências de notificação por usuário. */
    private final NotificationPreferenceRepository preferenceRepository;

    /** Repositório de usuários. */
    private final UserRepository userRepository;

    /** Repositório de tarefas associado às notificações. */
    private final TaskRepository taskRepository;

        /** Servico responsavel por enviar mensagens via WhatsApp. */
    private final WhatsAppService whatsAppService;

                /**
                 * Salva ou atualiza as preferencias de notificacao do usuario
                 * autenticado.
                 *
                 * @param request dados da preferencia informados pelo usuario
                 * @param currentUser usuario autenticado
                 * @return preferencias persistidas
                 */
    @Transactional
    public NotificationPreferenceResponse savePreferences(
            final NotificationPreferenceRequest request,
            final UserPrincipal currentUser) {
        final User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        NotificationPreference preference =
                preferenceRepository.findByUserId(user.getId())
                .orElse(NotificationPreference.builder()
                        .user(user)
                        .build());

        final String sanitizedNumber = sanitizeNumber(
                request.getWhatsappNumber());

        if (Boolean.TRUE.equals(request.getEnabled())
                && !StringUtils.hasText(sanitizedNumber)) {
            throw new IllegalStateException(VALID_NUMBER_MESSAGE);
        }

        preference.setWhatsappNumber(
                StringUtils.hasText(sanitizedNumber)
                        ? sanitizedNumber
                        : null);
        preference.setEnabled(Boolean.TRUE.equals(request.getEnabled()));

        if (StringUtils.hasText(request.getDailyReminderTime())) {
            preference.setDailyReminderTime(
                    LocalTime.parse(request.getDailyReminderTime()));
        }

        if (request.getTimezone() != null) {
            preference.setTimezone(request.getTimezone());
        }

        if (request.getSendOverdueAlerts() != null) {
            preference.setSendOverdueAlerts(request.getSendOverdueAlerts());
        }

        if (request.getSendCompletionSummary() != null) {
            preference.setSendCompletionSummary(
                    request.getSendCompletionSummary());
        }

        preference = preferenceRepository.save(preference);
        return mapToResponse(preference);
    }

    /**
     * Recupera as preferências configuradas pelo usuário autenticado.
     *
     * @param currentUser usuário autenticado
     * @return preferências encontradas
     */
    @Transactional(readOnly = true)
    public NotificationPreferenceResponse getPreferences(
            final UserPrincipal currentUser) {
        final NotificationPreference preference = preferenceRepository
                .findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification preferences not found"));

        return mapToResponse(preference);
    }

    /**
     * Envia uma mensagem de teste para validar as configurações atuais.
     *
     * @param currentUser usuário solicitante
     */
    public void sendTestNotification(final UserPrincipal currentUser) {
        preferenceRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification preferences not found"));

        whatsAppService.sendTestMessage(
                currentUser.getId(),
                currentUser.getUsername());
    }

    /**
     * Agenda lembretes diários de tarefas ativas.
     */
    @Scheduled(cron = CRON_EVERY_MINUTE)
    @Transactional
    public void sendScheduledNotifications() {
        final LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
        final List<NotificationPreference> preferences = preferenceRepository
                .findByDailyReminderTime(currentTime);

        log.info(
                "Verificando notificacoes agendadas as {}: {} "
                        + "usuarios encontrados",
                currentTime,
                preferences.size());

        for (final NotificationPreference preference : preferences) {
            try {
                sendDailyReminder(preference);
            } catch (Exception exception) {
                log.error(
                        "Erro ao enviar notificacao para o usuario {}: {}",
                        preference.getUser().getId(),
                        exception.getMessage());
            }
        }
    }

    /**
     * Agenda alertas periódicos sobre tarefas atrasadas.
     */
    @Scheduled(cron = CRON_EVERY_SIX_HOURS)
    @Transactional
    public void sendOverdueAlerts() {
        final List<NotificationPreference> preferences =
                preferenceRepository.findAllEnabled();

        log.info(
                "Verificando tarefas atrasadas: {} usuarios com notificacoes "
                        + "habilitadas",
                preferences.size());

        for (final NotificationPreference preference : preferences) {
            if (!Boolean.TRUE.equals(preference.getSendOverdueAlerts())) {
                continue;
            }

            try {
                final List<Task> overdueTasks = taskRepository.findOverdueTasks(
                        preference.getUser().getId(),
                        LocalDateTime.now());

                if (!overdueTasks.isEmpty()) {
                    whatsAppService.sendOverdueAlert(
                            preference.getUser().getId(),
                            preference.getUser().getUsername(),
                            overdueTasks);
                }
            } catch (Exception exception) {
                log.error(
                        "Erro ao enviar alerta de atraso para o usuario {}: {}",
                        preference.getUser().getId(),
                        exception.getMessage());
            }
        }
    }

    private void sendDailyReminder(final NotificationPreference preference) {
        final User user = preference.getUser();

        final List<Task> tasksToReview = taskRepository.findByUserIdAndStatus(
                user.getId(), Task.TaskStatus.TODO);
        tasksToReview.addAll(taskRepository.findByUserIdAndStatus(user.getId(),
                Task.TaskStatus.IN_PROGRESS));

        tasksToReview.sort(PRIORITY_COMPARATOR);

        whatsAppService.sendDailyTaskReminder(
                user.getId(),
                user.getUsername(),
                tasksToReview);

        log.info(
                "Lembrete diario enviado para o usuario {} com {} tarefas",
                user.getId(),
                tasksToReview.size());
    }

    private NotificationPreferenceResponse mapToResponse(
            final NotificationPreference preference) {
        final String reminderTime = preference.getDailyReminderTime() == null
                ? null
                : preference.getDailyReminderTime().toString();

        return NotificationPreferenceResponse.builder()
                .id(preference.getId())
                .whatsappNumber(preference.getWhatsappNumber())
                .enabled(preference.getEnabled())
                .dailyReminderTime(reminderTime)
                .timezone(preference.getTimezone())
                .sendOverdueAlerts(preference.getSendOverdueAlerts())
                .sendCompletionSummary(preference.getSendCompletionSummary())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }

    private String sanitizeNumber(final String whatsappNumber) {
        return whatsappNumber == null ? null : whatsappNumber.trim();
    }
}
