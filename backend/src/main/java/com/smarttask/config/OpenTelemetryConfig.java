package com.smarttask.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenTelemetry para observabilidade completa da aplicação.
 * Configura traces (Jaeger), metrics (Prometheus) e propagação de contexto.
 * Usa autoconfigure para simplificar a configuração via variáveis de ambiente.
 */
@Configuration
@Slf4j
public class OpenTelemetryConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Configura o OpenTelemetry SDK usando autoconfigure.
     * As configurações são feitas via variáveis de ambiente ou application.yml:
     * - otel.service.name
     * - otel.traces.exporter
     * - otel.metrics.exporter
     * - otel.exporter.jaeger.endpoint
     * - otel.exporter.otlp.endpoint
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        // Configura variáveis do sistema para o autoconfigure
        System.setProperty("otel.service.name", applicationName);
        System.setProperty("otel.traces.exporter", "jaeger,otlp");
        System.setProperty("otel.metrics.exporter", "otlp");
        
        // Se as variáveis não estiverem configuradas, usa valores padrão
        if (System.getProperty("otel.exporter.jaeger.endpoint") == null) {
            System.setProperty("otel.exporter.jaeger.endpoint", "http://localhost:14250");
        }
        if (System.getProperty("otel.exporter.otlp.endpoint") == null) {
            System.setProperty("otel.exporter.otlp.endpoint", "http://localhost:4317");
        }

        OpenTelemetry openTelemetry = AutoConfiguredOpenTelemetrySdk.initialize()
                .getOpenTelemetrySdk();
        
        log.info("OpenTelemetry configurado com sucesso para o serviço: {}", applicationName);
        log.info("Jaeger endpoint: {}", System.getProperty("otel.exporter.jaeger.endpoint"));
        log.info("OTLP endpoint: {}", System.getProperty("otel.exporter.otlp.endpoint"));
        
        return openTelemetry;
    }

    /**
     * Configura o Tracer para criar spans.
     */
    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer(applicationName, "1.0.0");
    }
}
