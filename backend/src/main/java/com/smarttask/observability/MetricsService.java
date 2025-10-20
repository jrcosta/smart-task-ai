package com.smarttask.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Servico para registrar metricas customizadas do OpenTelemetry.
 * Fornece contadores e histogramas para monitorar as operacoes da aplicacao.
 */
@Component
@Slf4j
public class MetricsService {

    /** Instancia de {@link Meter} usada para criar contadores e histogramas. */
    private final Meter meter;

    /** Contador de tarefas criadas. */
    private final LongCounter taskCreatedCounter;

    /** Contador de tarefas concluidas. */
    private final LongCounter taskCompletedCounter;

    /** Contador de tarefas removidas. */
    private final LongCounter taskDeletedCounter;

    /** Contador de solicitacoes de analise por IA. */
    private final LongCounter aiAnalysisCounter;

    /** Contador de mensagens de WhatsApp enviadas. */
    private final LongCounter whatsappMessageCounter;

    /** Contador de autenticacoes bem-sucedidas. */
    private final LongCounter authenticationCounter;

    /** Contador de falhas de autenticacao. */
    private final LongCounter authenticationFailureCounter;

    /** Histograma de duracao das operacoes com tarefas. */
    private final LongHistogram taskDurationHistogram;

    /** Histograma de duracao das analises de IA. */
    private final LongHistogram aiAnalysisDurationHistogram;

        /**
         * Cria a instancia do servico a partir do {@link OpenTelemetry}
         * configurado.
         *
                                * @param openTelemetry cliente OpenTelemetry
                                *     fornecido pelo contexto Spring
         */
    public MetricsService(final OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter("smart-task-manager");
        this.taskCreatedCounter = buildCounter(
                "tasks.created",
                "Numero total de tarefas criadas",
                "tasks");
        this.taskCompletedCounter = buildCounter(
                "tasks.completed",
                "Numero total de tarefas completadas",
                "tasks");
        this.taskDeletedCounter = buildCounter(
                "tasks.deleted",
                "Numero total de tarefas deletadas",
                "tasks");
        this.aiAnalysisCounter = buildCounter(
                "ai.analysis.requests",
                "Numero total de analises de IA solicitadas",
                "requests");
        this.whatsappMessageCounter = buildCounter(
                "whatsapp.messages.sent",
                "Numero total de mensagens WhatsApp enviadas",
                "messages");
        this.authenticationCounter = buildCounter(
                "auth.success",
                "Numero total de autenticacoes bem-sucedidas",
                "authentications");
        this.authenticationFailureCounter = buildCounter(
                "auth.failures",
                "Numero total de falhas de autenticacao",
                "failures");

        this.taskDurationHistogram = buildHistogram(
                "tasks.duration",
                "Duracao de operacoes com tarefas");
        this.aiAnalysisDurationHistogram = buildHistogram(
                "ai.analysis.duration",
                "Duracao de analises de IA");

        log.info(
                "MetricsService inicializado com metricas de telemetria");
    }

    /**
     * Registra a criacao de uma nova tarefa para a prioridade informada.
     *
     * @param priority prioridade da tarefa criada
     */
    public void recordTaskCreated(final String priority) {
        taskCreatedCounter.add(1, attributes("priority", priority));
    }

    /**
     * Registra a conclusao de uma tarefa.
     */
    public void recordTaskCompleted() {
        taskCompletedCounter.add(1);
    }

    /**
     * Registra a remocao de uma tarefa.
     */
    public void recordTaskDeleted() {
        taskDeletedCounter.add(1);
    }

    /**
     * Registra solicitacoes de analise IA com resultado de sucesso ou falha.
     *
     * @param success indica se a analise foi concluida com exito
     */
    public void recordAIAnalysis(final boolean success) {
                aiAnalysisCounter.add(
                                1,
                                attributes("success", success));
    }

    /**
     * Registra o envio de uma mensagem via WhatsApp.
     *
     * @param messageType categoria da mensagem enviada
     */
    public void recordWhatsAppMessage(final String messageType) {
                whatsappMessageCounter.add(
                                1,
                                attributes("type", messageType));
    }

    /**
     * Registra um login bem-sucedido.
     *
     * @param method metodo de autenticacao utilizado
     */
    public void recordAuthenticationSuccess(final String method) {
        authenticationCounter.add(
                1,
                attributes("method", method));
    }

    /**
     * Registra uma falha de autenticacao indicando o motivo.
     *
     * @param reason motivo da falha
     */
    public void recordAuthenticationFailure(final String reason) {
        authenticationFailureCounter.add(
                1,
                attributes("reason", reason));
    }

    /**
     * Registra a duracao de uma operacao com tarefas.
     *
     * @param durationMs duracao em milissegundos
     * @param operation operacao monitorada
     */
    public void recordTaskDuration(
            final long durationMs,
            final String operation) {
        taskDurationHistogram.record(
                durationMs,
                attributes("operation", operation));
    }

    /**
     * Registra a duracao total de uma analise executada pela IA.
     *
     * @param durationMs duracao em milissegundos
     */
    public void recordAIAnalysisDuration(final long durationMs) {
        aiAnalysisDurationHistogram.record(durationMs);
    }

    private LongCounter buildCounter(
            final String name,
            final String description,
            final String unit) {
        return meter.counterBuilder(name)
                .setDescription(description)
                .setUnit(unit)
                .build();
    }

    private LongHistogram buildHistogram(
            final String name,
            final String description) {
        return meter.histogramBuilder(name)
                .setDescription(description)
                .setUnit("ms")
                .ofLongs()
                .build();
    }

    private Attributes attributes(final String key, final String value) {
        return Attributes.builder()
                .put(key, value)
                .build();
    }

    private Attributes attributes(final String key, final boolean value) {
        return Attributes.builder()
                .put(key, value)
                .build();
    }
}
