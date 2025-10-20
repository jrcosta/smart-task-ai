package com.smarttask.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao do OpenTelemetry para observabilidade completa da aplicacao.
 * Configura traces (Jaeger), metrics (Prometheus) e propagacao de contexto.
 * Usa autoconfigure para simplificar a configuracao via variaveis de ambiente.
 */
@Configuration
@Slf4j
public final class OpenTelemetryConfig {

    /** Nome da propriedade que identifica o servico no OpenTelemetry. */
    private static final String OTEL_SERVICE_NAME = "otel.service.name";

    /** Propriedade que define os exportadores de traces. */
    private static final String OTEL_TRACES_EXPORTER = "otel.traces.exporter";

    /** Propriedade que define os exportadores de metricas. */
    private static final String OTEL_METRICS_EXPORTER = "otel.metrics.exporter";

    /** Propriedade que aponta para o endpoint do Jaeger. */
    private static final String OTEL_EXPORTER_JAEGER_ENDPOINT =
            "otel.exporter.jaeger.endpoint";

    /** Propriedade que aponta para o endpoint OTLP. */
    private static final String OTEL_EXPORTER_OTLP_ENDPOINT =
            "otel.exporter.otlp.endpoint";

    /** Endpoint padrao utilizado para o Jaeger quando nao configurado. */
    private static final String DEFAULT_JAEGER_ENDPOINT =
            "http://localhost:14250";

    /** Endpoint padrao utilizado para OTLP quando nao configurado. */
    private static final String DEFAULT_OTLP_ENDPOINT =
            "http://localhost:4317";

    /** Versao do tracer utilizado para identificacao do componente. */
    private static final String TRACER_VERSION = "1.0.0";

    /** Nome da aplicacao registrado no OpenTelemetry. */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Configura o OpenTelemetry SDK usando autoconfigure.
     * As configuracoes sao feitas via variaveis de ambiente ou application.yml:
     * - otel.service.name
     * - otel.traces.exporter
     * - otel.metrics.exporter
     * - otel.exporter.jaeger.endpoint
     * - otel.exporter.otlp.endpoint
     *
     * @return instancia inicializada do {@link OpenTelemetry}
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        System.setProperty(OTEL_SERVICE_NAME, applicationName);
        System.setProperty(OTEL_TRACES_EXPORTER, "jaeger,otlp");
        System.setProperty(OTEL_METRICS_EXPORTER, "otlp");

        if (System.getProperty(OTEL_EXPORTER_JAEGER_ENDPOINT) == null) {
            System.setProperty(
                    OTEL_EXPORTER_JAEGER_ENDPOINT,
                    DEFAULT_JAEGER_ENDPOINT);
        }
        if (System.getProperty(OTEL_EXPORTER_OTLP_ENDPOINT) == null) {
            System.setProperty(
                    OTEL_EXPORTER_OTLP_ENDPOINT,
                    DEFAULT_OTLP_ENDPOINT);
        }

        final OpenTelemetry openTelemetry = AutoConfiguredOpenTelemetrySdk
                .initialize()
                .getOpenTelemetrySdk();

        log.info(
                "OpenTelemetry configurado para o servico: {}",
                applicationName);
        log.info("Jaeger endpoint: {}",
                System.getProperty(OTEL_EXPORTER_JAEGER_ENDPOINT));
        log.info("OTLP endpoint: {}",
                System.getProperty(OTEL_EXPORTER_OTLP_ENDPOINT));

        return openTelemetry;
    }

    /**
     * Configura o Tracer para criar spans.
     *
     * @param openTelemetry instancia principal do OpenTelemetry
     * @return tracer configurado com o nome da aplicacao e versao da API
     */
    @Bean
    public Tracer tracer(final OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(applicationName, TRACER_VERSION);
    }
}
