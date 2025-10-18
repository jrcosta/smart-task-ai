package com.smarttask.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Serviço para registrar métricas customizadas do OpenTelemetry.
 * Fornece contadores e histogramas para monitoramento de operações da aplicação.
 */
@Component
@Slf4j
public class MetricsService {

    private final Meter meter;
    
    // Contadores
    private final LongCounter taskCreatedCounter;
    private final LongCounter taskCompletedCounter;
    private final LongCounter taskDeletedCounter;
    private final LongCounter aiAnalysisCounter;
    private final LongCounter whatsappMessageCounter;
    private final LongCounter authenticationCounter;
    private final LongCounter authenticationFailureCounter;
    
    // Histogramas
    private final LongHistogram taskDurationHistogram;
    private final LongHistogram aiAnalysisDurationHistogram;

    public MetricsService(OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter("smart-task-manager");
        
        // Inicializa contadores
        this.taskCreatedCounter = meter
                .counterBuilder("tasks.created")
                .setDescription("Número total de tarefas criadas")
                .setUnit("tasks")
                .build();
                
        this.taskCompletedCounter = meter
                .counterBuilder("tasks.completed")
                .setDescription("Número total de tarefas completadas")
                .setUnit("tasks")
                .build();
                
        this.taskDeletedCounter = meter
                .counterBuilder("tasks.deleted")
                .setDescription("Número total de tarefas deletadas")
                .setUnit("tasks")
                .build();
                
        this.aiAnalysisCounter = meter
                .counterBuilder("ai.analysis.requests")
                .setDescription("Número total de análises de IA solicitadas")
                .setUnit("requests")
                .build();
                
        this.whatsappMessageCounter = meter
                .counterBuilder("whatsapp.messages.sent")
                .setDescription("Número total de mensagens WhatsApp enviadas")
                .setUnit("messages")
                .build();
                
        this.authenticationCounter = meter
                .counterBuilder("auth.success")
                .setDescription("Número total de autenticações bem-sucedidas")
                .setUnit("authentications")
                .build();
                
        this.authenticationFailureCounter = meter
                .counterBuilder("auth.failures")
                .setDescription("Número total de falhas de autenticação")
                .setUnit("failures")
                .build();
        
        // Inicializa histogramas
        this.taskDurationHistogram = meter
                .histogramBuilder("tasks.duration")
                .setDescription("Duração de operações com tarefas")
                .setUnit("ms")
                .ofLongs()
                .build();
                
        this.aiAnalysisDurationHistogram = meter
                .histogramBuilder("ai.analysis.duration")
                .setDescription("Duração de análises de IA")
                .setUnit("ms")
                .ofLongs()
                .build();
        
        log.info("MetricsService inicializado com métricas do OpenTelemetry");
    }

    public void recordTaskCreated(String priority) {
        taskCreatedCounter.add(1, io.opentelemetry.api.common.Attributes.builder()
                .put("priority", priority)
                .build());
    }

    public void recordTaskCompleted() {
        taskCompletedCounter.add(1);
    }

    public void recordTaskDeleted() {
        taskDeletedCounter.add(1);
    }

    public void recordAIAnalysis(boolean success) {
        aiAnalysisCounter.add(1, io.opentelemetry.api.common.Attributes.builder()
                .put("success", success)
                .build());
    }

    public void recordWhatsAppMessage(String messageType) {
        whatsappMessageCounter.add(1, io.opentelemetry.api.common.Attributes.builder()
                .put("type", messageType)
                .build());
    }

    public void recordAuthenticationSuccess(String method) {
        authenticationCounter.add(1, io.opentelemetry.api.common.Attributes.builder()
                .put("method", method)
                .build());
    }

    public void recordAuthenticationFailure(String reason) {
        authenticationFailureCounter.add(1, io.opentelemetry.api.common.Attributes.builder()
                .put("reason", reason)
                .build());
    }

    public void recordTaskDuration(long durationMs, String operation) {
        taskDurationHistogram.record(durationMs, io.opentelemetry.api.common.Attributes.builder()
                .put("operation", operation)
                .build());
    }

    public void recordAIAnalysisDuration(long durationMs) {
        aiAnalysisDurationHistogram.record(durationMs);
    }
}
