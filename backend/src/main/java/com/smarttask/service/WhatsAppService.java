package com.smarttask.service;

import com.smarttask.model.Task;
import com.smarttask.observability.MetricsService;
import com.smarttask.observability.Traced;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Encapsula a integracao com o Twilio para envio de mensagens WhatsApp.
 * Mantem fallback de simulacao quando as credenciais nao estao definidas.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WhatsAppService {

    /** Formato aplicado aos lembretes diarios. */
    private static final DateTimeFormatter DAILY_REMINDER_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM HH:mm");

    /** Formato aplicado aos alertas de atraso. */
    private static final DateTimeFormatter OVERDUE_FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Indicador de log quando o envio ocorre em simulacao. */
    private static final String SIMULATION_MARKER = "[SIMULACAO]";

    /** Indicador de log quando ocorre fallback apos falha. */
    private static final String FALLBACK_MARKER = "[FALLBACK]";

    /** Sequencia de quebra de linha usada nas mensagens. */
    private static final String LINE_BREAK = "\n";

    /** Prefixo padrao exigido pelo Twilio para WhatsApp. */
    private static final String WHATSAPP_PREFIX = "whatsapp:";

    /** Identificador da conta Twilio configurado via propriedade. */
    @Value("${twilio.account-sid:}")
    private String defaultAccountSid;

    /** Token Twilio padrao configurado via propriedade. */
    @Value("${twilio.auth-token:}")
    private String defaultAuthToken;

    /** Numero WhatsApp padrao utilizado nas simulacoes. */
    @Value("${twilio.whatsapp-number:}")
    private String defaultTwilioWhatsAppNumber;

    /** Indica se as credenciais padrao estao prontas para uso. */
    private boolean defaultTwilioConfigured;

    /** Servico de metricas responsavel por registrar mensagens enviadas. */
    private final MetricsService metricsService;

    /** Servico de configuracoes que fornece as credenciais de cada usuario. */
    private final SettingsService settingsService;

    /**
     * Inicializa o cliente Twilio padrao quando as credenciais sao definidas.
     */
    @PostConstruct
    public final void init() {
    if (!StringUtils.hasText(defaultAccountSid)
        || !StringUtils.hasText(defaultAuthToken)) {
        log.info("Sem credenciais Twilio padrao. Usando dados do usuario.");
            return;
        }

        try {
            Twilio.init(defaultAccountSid, defaultAuthToken);
            defaultTwilioConfigured = true;
            log.info("Servico Twilio padrao inicializado para WhatsApp.");
        } catch (Exception ex) {
            log.warn("Falha ao inicializar Twilio padrao: {}", ex.getMessage());
            defaultTwilioConfigured = false;
        }
    }

    /**
     * Envia um lembrete diario das tarefas do usuario.
     *
     * @param userId identificador do usuario
     * @param userName nome do usuario
     * @param tasks lista de tarefas previstas para o dia
     */
    @Traced("WhatsAppService.sendDailyTaskReminder")
    public final void sendDailyTaskReminder(
            final Long userId,
            final String userName,
            final List<Task> tasks) {
        final SettingsService.TwilioCredentials credentials =
                settingsService.getDecryptedTwilioCredentials(userId);
        final String toNumber = credentials.userWhatsappNumber();

        if (!StringUtils.hasText(toNumber)) {
            log.warn(
                    "Usuario {} sem numero WhatsApp. Mensagem ignorada.",
                    userId);
            return;
        }

        final String message = buildDailyReminderMessage(userName, tasks);
        sendMessage(userId, toNumber, message, "daily_reminder");
    }

    /**
     * Envia uma notificacao de tarefas atrasadas.
     *
     * @param userId identificador do usuario
     * @param userName nome do usuario
     * @param overdueTasks lista de tarefas atrasadas
     */
    @Traced("WhatsAppService.sendOverdueAlert")
    public final void sendOverdueAlert(
            final Long userId,
            final String userName,
            final List<Task> overdueTasks) {
        if (overdueTasks.isEmpty()) {
            return;
        }

        final SettingsService.TwilioCredentials credentials =
                settingsService.getDecryptedTwilioCredentials(userId);
        final String toNumber = credentials.userWhatsappNumber();

        if (!StringUtils.hasText(toNumber)) {
            log.warn(
                    "Usuario {} sem numero WhatsApp. Mensagem ignorada.",
                    userId);
            return;
        }

        final String message = buildOverdueAlertMessage(userName, overdueTasks);
        sendMessage(userId, toNumber, message, "overdue_alert");
    }

    /**
     * Envia um resumo de tarefas concluidas no dia.
     *
     * @param userId identificador do usuario
     * @param userName nome do usuario
     * @param completedToday total de tarefas concluidas
     * @param totalHours total de horas trabalhadas
     */
    @Traced("WhatsAppService.sendCompletionSummary")
    public final void sendCompletionSummary(
            final Long userId,
            final String userName,
            final int completedToday,
            final int totalHours) {
        final SettingsService.TwilioCredentials credentials =
                settingsService.getDecryptedTwilioCredentials(userId);
        final String toNumber = credentials.userWhatsappNumber();

        if (!StringUtils.hasText(toNumber)) {
            log.warn(
                    "Usuario {} sem numero WhatsApp. Mensagem ignorada.",
                    userId);
            return;
        }

        final String message = buildCompletionSummaryMessage(
                userName,
                completedToday,
                totalHours);
        sendMessage(userId, toNumber, message, "completion_summary");
    }

    /**
     * Envia uma mensagem de teste para validar as credenciais do usuario.
     *
     * @param userId identificador do usuario
     * @param userName nome do usuario
     */
    @Traced("WhatsAppService.sendTestMessage")
    public final void sendTestMessage(
            final Long userId,
            final String userName) {
        final SettingsService.TwilioCredentials credentials =
                settingsService.getDecryptedTwilioCredentials(userId);
        final String toNumber = credentials.userWhatsappNumber();

        if (!StringUtils.hasText(toNumber)) {
            log.warn(
                    "Usuario {} sem numero WhatsApp. Mensagem ignorada.",
                    userId);
            return;
        }

        final String message = buildTestMessage(userName);
        sendMessage(userId, toNumber, message, "test_message");
    }

    private String buildDailyReminderMessage(
            final String userName,
            final List<Task> tasks) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Bom dia, ")
                .append(userName)
                .append("!")
                .append(LINE_BREAK)
                .append(LINE_BREAK)
                .append("Resumo das tarefas do dia:")
                .append(LINE_BREAK)
                .append(LINE_BREAK);

        if (tasks.isEmpty()) {
            builder.append("Sem tarefas pendentes. Aproveite o dia!");
            return builder.toString();
        }

        int index = 1;
        for (Task task : tasks) {
            builder.append(index++)
                    .append(". ")
                    .append(priorityPrefix(task))
                    .append(task.getTitle())
                    .append(LINE_BREAK);

            if (task.getDueDate() != null) {
                builder.append("   Prazo: ")
                        .append(task.getDueDate().format(DAILY_REMINDER_FORMAT))
                        .append(LINE_BREAK);
            }

            if (task.getEstimatedHours() != null) {
                builder.append("   Estimativa: ")
                        .append(task.getEstimatedHours())
                        .append("h")
                        .append(LINE_BREAK);
            }

            builder.append(LINE_BREAK);
        }

            builder.append("Voce consegue concluir tudo hoje. Estamos juntos!");
        return builder.toString();
    }

    private String buildOverdueAlertMessage(
            final String userName,
            final List<Task> overdueTasks) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Atencao, ")
                .append(userName)
                .append("!")
                .append(LINE_BREAK)
                .append(LINE_BREAK)
                .append("Voce possui ")
                .append(overdueTasks.size())
                .append(" tarefa(s) atrasada(s):")
                .append(LINE_BREAK)
                .append(LINE_BREAK);

        int index = 1;
        for (Task task : overdueTasks) {
            builder.append(index++)
                    .append(". ")
                    .append(task.getTitle())
                    .append(LINE_BREAK);

            if (task.getDueDate() != null) {
                builder.append("   Venceu em: ")
                        .append(task.getDueDate().format(OVERDUE_FORMAT))
                        .append(LINE_BREAK);
            }

            builder.append(LINE_BREAK);
        }

        builder.append("Planeje tempo hoje para resolver tarefas pendentes.");
        return builder.toString();
    }

    private String buildCompletionSummaryMessage(
            final String userName,
            final int completedToday,
            final int totalHours) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Parabens, ")
                .append(userName)
                .append("!")
                .append(LINE_BREAK)
                .append(LINE_BREAK)
                .append("Voce concluiu ")
                .append(completedToday)
                .append(" tarefa(s) hoje.")
                .append(LINE_BREAK);

        if (totalHours > 0) {
            builder.append("Total de horas trabalhadas: ")
                    .append(totalHours)
                    .append("h")
                    .append(LINE_BREAK);
        }

        builder.append(LINE_BREAK)
                .append("Continue com o bom ritmo. Otimo trabalho!");
        return builder.toString();
    }

    private String buildTestMessage(final String userName) {
        return new StringBuilder()
                .append("Ola, ")
                .append(userName)
                .append("!")
                .append(LINE_BREAK)
                .append(LINE_BREAK)
        .append("Mensagem de teste: canal WhatsApp ativo.")
                .append(LINE_BREAK)
                .append(LINE_BREAK)
        .append("Voce recebera lembretes diarios das tarefas configuradas.")
                .toString();
    }

    private String priorityPrefix(final Task task) {
        if (task.getPriority() == null) {
            return "[SEM PRIORIDADE] ";
        }

        switch (task.getPriority()) {
            case URGENT:
                return "[URGENTE] ";
            case HIGH:
                return "[ALTA] ";
            case MEDIUM:
                return "[MEDIA] ";
            case LOW:
                return "[BAIXA] ";
            default:
                return "[SEM PRIORIDADE] ";
        }
    }

    private void sendMessage(
            final Long userId,
            final String toNumber,
            final String messageBody,
            final String messageType) {
        if (!StringUtils.hasText(toNumber)) {
            log.warn("Envio sem numero WhatsApp. Mensagem ignorada.");
            return;
        }

        final String sanitizedTo = toNumber.trim();
        final SettingsService.TwilioCredentials credentials =
                settingsService.getDecryptedTwilioCredentials(userId);

        String accountSid = credentials.accountSid();
        String authToken = credentials.authToken();
        String twilioNumber = credentials.whatsappNumber();
        boolean twilioConfigured = hasCredentials(accountSid, authToken);

        if (!twilioConfigured) {
            accountSid = defaultAccountSid;
            authToken = defaultAuthToken;
            twilioNumber = defaultTwilioWhatsAppNumber;
            twilioConfigured = defaultTwilioConfigured;
        }

        if (!twilioConfigured) {
            logSimulation(
                    sanitizedTo,
                    messageBody,
                    messageType,
                    SIMULATION_MARKER);
            return;
        }

        if (!StringUtils.hasText(twilioNumber)) {
            log.error(
                    "Twilio sem numero de origem. Simulacao para {}.",
                    sanitizedTo);
            logSimulation(
                    sanitizedTo,
                    messageBody,
                    messageType,
                    FALLBACK_MARKER);
            return;
        }

        try {
            Twilio.init(accountSid, authToken);
            final Message message = Message.creator(
                    new PhoneNumber(ensureWhatsAppPrefix(sanitizedTo)),
                    new PhoneNumber(ensureWhatsAppPrefix(twilioNumber)),
                    messageBody)
                    .create();

            log.info("Mensagem WhatsApp enviada. SID: {}", message.getSid());
            metricsService.recordWhatsAppMessage(messageType);
        } catch (Exception ex) {
            log.error("Falha ao enviar WhatsApp para {}: {}",
                    sanitizedTo,
                    ex.getMessage());
            logSimulation(
                    sanitizedTo,
                    messageBody,
                    messageType + "_failed",
                    FALLBACK_MARKER);
        }
    }

    private boolean hasCredentials(
            final String accountSid,
            final String authToken) {
        return StringUtils.hasText(accountSid)
                && StringUtils.hasText(authToken);
    }

    private String ensureWhatsAppPrefix(final String number) {
        if (!StringUtils.hasText(number)) {
            return number;
        }

        return number.startsWith(WHATSAPP_PREFIX)
                ? number
                : WHATSAPP_PREFIX + number;
    }

    private void logSimulation(
            final String toNumber,
            final String messageBody,
            final String messageType,
            final String marker) {
        log.info("{} Mensagem WhatsApp para {}:{}{}",
                marker,
                toNumber,
                LINE_BREAK,
                messageBody);
        metricsService.recordWhatsAppMessage(messageType);
    }
}
