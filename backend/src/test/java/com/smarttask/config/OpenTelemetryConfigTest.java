package com.smarttask.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Testes unitários para OpenTelemetryConfig.
 * Valida a configuração do Tracer com Spring Boot auto-configuration.
 */
class OpenTelemetryConfigTest {

    private OpenTelemetryConfig config;

    @BeforeEach
    void setUp() {
        config = new OpenTelemetryConfig();
        ReflectionTestUtils.setField(config, "applicationName", "smart-task-manager");
    }

    @Test
    void tracer_deveSerConfiguradoComNomeDaAplicacao() {
        // Given
        OpenTelemetry openTelemetry = Mockito.mock(OpenTelemetry.class);
        Tracer mockTracer = Mockito.mock(Tracer.class);
        
        // When - usando mock pois OpenTelemetry.getTracer() retorna um tracer configurado
        Mockito.when(openTelemetry.getTracer("smart-task-manager", "1.0.0")).thenReturn(mockTracer);
        Tracer tracer = config.tracer(openTelemetry);

        // Then
        assertThat(tracer).isNotNull();
    }

    @Test
    void tracer_deveUsarOpenTelemetryAutoConfigurado() {
        // Given
        OpenTelemetry openTelemetry = Mockito.mock(OpenTelemetry.class);
        Tracer mockTracer = Mockito.mock(Tracer.class);
        Mockito.when(openTelemetry.getTracer("smart-task-manager", "1.0.0")).thenReturn(mockTracer);

        // When
        Tracer result = config.tracer(openTelemetry);

        // Then - verifica que o OpenTelemetry auto-configurado foi usado
        assertThat(result).isEqualTo(mockTracer);
    }

    @Test
    void tracer_deveUsarVersaoCorreta() {
        // Given
        OpenTelemetry openTelemetry = Mockito.mock(OpenTelemetry.class);
        Tracer mockTracer = Mockito.mock(Tracer.class);
        
        // When
        Mockito.when(openTelemetry.getTracer("smart-task-manager", "1.0.0")).thenReturn(mockTracer);
        Tracer tracer = config.tracer(openTelemetry);

        // Then - verifica que a versão 1.0.0 está sendo usada
        assertThat(tracer).isNotNull();
        assertThat(tracer).isEqualTo(mockTracer);
    }

    @Test
    void config_deveSerInstanciadaSemErros() {
        // When
        OpenTelemetryConfig newConfig = new OpenTelemetryConfig();

        // Then
        assertThat(newConfig).isNotNull();
    }
}
