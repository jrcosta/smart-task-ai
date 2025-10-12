package com.smarttask.service;

import com.smarttask.model.Task;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Encapsula integração com Twilio para envio de mensagens WhatsApp, incluindo
 * rotinas de simulação quando as credenciais não estão disponíveis.
 */
@Service
@Slf4j
public class WhatsAppService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.whatsapp-number:}")
    private String twilioWhatsAppNumber;

    private boolean twilioConfigured = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isEmpty() && 
            authToken != null && !authToken.isEmpty()) {
            try {
                Twilio.init(accountSid, authToken);
                twilioConfigured = true;
                log.info("Twilio WhatsApp service initialized successfully");
            } catch (Exception e) {
                log.warn("Failed to initialize Twilio: {}", e.getMessage());
                twilioConfigured = false;
            }
        } else {
            log.warn("Twilio credentials not configured. WhatsApp notifications will be simulated.");
        }
    }

    public void sendDailyTaskReminder(String toNumber, String userName, List<Task> tasks) {
        StringBuilder message = new StringBuilder();
        message.append("🌅 *Bom dia, ").append(userName).append("!*\n\n");
        message.append("📋 *Suas tarefas para hoje:*\n\n");

        if (tasks.isEmpty()) {
            message.append("✨ Você não tem tarefas pendentes! Aproveite o dia! 🎉");
        } else {
            int count = 1;
            for (Task task : tasks) {
                message.append(count++).append(". ");
                
                // Emoji baseado na prioridade
                switch (task.getPriority()) {
                    case URGENT:
                        message.append("🔴 ");
                        break;
                    case HIGH:
                        message.append("🟠 ");
                        break;
                    case MEDIUM:
                        message.append("🟡 ");
                        break;
                    case LOW:
                        message.append("🟢 ");
                        break;
                }
                
                message.append("*").append(task.getTitle()).append("*");
                
                if (task.getDueDate() != null) {
                    message.append("\n   📅 Prazo: ")
                           .append(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")));
                }
                
                if (task.getEstimatedHours() != null) {
                    message.append("\n   ⏱️ Estimativa: ").append(task.getEstimatedHours()).append("h");
                }
                
                message.append("\n\n");
            }
            
            message.append("💪 Você consegue! Boa sorte!");
        }

        sendMessage(toNumber, message.toString());
    }

    public void sendOverdueAlert(String toNumber, String userName, List<Task> overdueTasks) {
        if (overdueTasks.isEmpty()) {
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("⚠️ *Atenção, ").append(userName).append("!*\n\n");
        message.append("Você tem *").append(overdueTasks.size()).append(" tarefa(s) atrasada(s)*:\n\n");

        int count = 1;
        for (Task task : overdueTasks) {
            message.append(count++).append(". *").append(task.getTitle()).append("*\n");
            if (task.getDueDate() != null) {
                message.append("   📅 Venceu em: ")
                       .append(task.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                       .append("\n");
            }
        }

        message.append("\n🚀 Que tal resolver essas tarefas hoje?");

        sendMessage(toNumber, message.toString());
    }

    public void sendCompletionSummary(String toNumber, String userName, int completedToday, int totalHours) {
        StringBuilder message = new StringBuilder();
        message.append("🎉 *Parabéns, ").append(userName).append("!*\n\n");
        message.append("Você completou *").append(completedToday).append(" tarefa(s)* hoje!\n");
        
        if (totalHours > 0) {
            message.append("⏱️ Total de horas trabalhadas: *").append(totalHours).append("h*\n");
        }
        
        message.append("\n✨ Continue assim! Você está indo muito bem! 💪");

        sendMessage(toNumber, message.toString());
    }

    public void sendTestMessage(String toNumber, String userName) {
        String message = String.format(
            "🤖 *Olá, %s!*\n\n" +
            "Este é um teste de notificação do *Smart Task Manager*.\n\n" +
            "✅ Suas notificações estão configuradas corretamente!\n\n" +
            "Você receberá lembretes diários sobre suas tarefas.",
            userName
        );

        sendMessage(toNumber, message);
    }

    private void sendMessage(String toNumber, String messageBody) {
        if (!StringUtils.hasText(toNumber)) {
            log.warn("Tentativa de envio de WhatsApp sem número de destino. Mensagem descartada.");
            return;
        }

        String sanitizedTo = toNumber.trim();

        if (!twilioConfigured) {
            log.info("📱 [SIMULAÇÃO] Mensagem WhatsApp para {}: \n{}", sanitizedTo, messageBody);
            return;
        }

        if (!StringUtils.hasText(twilioWhatsAppNumber)) {
            log.error("Twilio configurado sem número de origem. Mensagem enviada em modo de simulação para {}.", sanitizedTo);
            log.info("📱 [FALLBACK] Mensagem WhatsApp para {}: \n{}", sanitizedTo, messageBody);
            return;
        }

        try {
            // Garantir formato WhatsApp (whatsapp:+número)
            String formattedTo = sanitizedTo.startsWith("whatsapp:") ? sanitizedTo : "whatsapp:" + sanitizedTo;
            String formattedFrom = twilioWhatsAppNumber.startsWith("whatsapp:") ? 
                                   twilioWhatsAppNumber : "whatsapp:" + twilioWhatsAppNumber;

            Message message = Message.creator(
                new PhoneNumber(formattedTo),
                new PhoneNumber(formattedFrom),
                messageBody
            ).create();

            log.info("WhatsApp message sent successfully. SID: {}", message.getSid());
        } catch (Exception e) {
            log.error("Falha ao enviar WhatsApp para {}: {}", sanitizedTo, e.getMessage());
            // Fallback para simulação em caso de erro
            log.info("📱 [FALLBACK] Mensagem WhatsApp para {}: \n{}", sanitizedTo, messageBody);
        }
    }
}
