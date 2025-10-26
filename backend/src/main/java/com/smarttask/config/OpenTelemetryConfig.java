package com.smarttask.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao do OpenTelemetry para observabilidade completa da aplicacao.
 * Usa o auto-configuration do Spring Boot 3.x com Micrometer Tracing.
 * As configuracoes sao feitas via application.yml em management.tracing.
 */
@Configuration
@Slf4j
public class OpenTelemetryConfig {

    /** Versao do tracer utilizado para identificacao do componente. */
    private static final String TRACER_VERSION = "1.0.0";

    /** Nome da aplicacao registrado no OpenTelemetry. */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Configura o Tracer para criar spans.
     * O OpenTelemetry SDK é automaticamente configurado pelo Spring Boot
     * através do Micrometer Tracing Bridge.
     *
     * @param openTelemetry instancia principal do OpenTelemetry (auto-configurado)
     * @return tracer configurado com o nome da aplicacao e versao da API
     */
    @Bean
    public Tracer tracer(final OpenTelemetry openTelemetry) {
        log.info("Configurando Tracer OpenTelemetry para o servico: {}", applicationName);
        return openTelemetry.getTracer(applicationName, TRACER_VERSION);
    }
}
