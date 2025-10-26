package com.smarttask.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para OpenApiConfig.
 * Valida a configuração do Swagger/OpenAPI.
 */
class OpenApiConfigTest {

    private OpenApiConfig config;

    @BeforeEach
    void setUp() {
        config = new OpenApiConfig();
    }

    @Test
    void customOpenAPI_deveRetornarConfiguracaoValida() {
        // When
        OpenAPI openAPI = config.customOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Smart Task Manager API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void customOpenAPI_deveConterDescricao() {
        // When
        OpenAPI openAPI = config.customOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getDescription()).isNotBlank();
        assertThat(openAPI.getInfo().getDescription()).contains("RESTful");
    }

    @Test
    void customOpenAPI_deveConterInformacoesDeContato() {
        // When
        OpenAPI openAPI = config.customOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("jrcosta");
    }

    @Test
    void customOpenAPI_deveConterLicenca() {
        // When
        OpenAPI openAPI = config.customOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getLicense()).isNotNull();
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("MIT License");
    }

    @Test
    void customOpenAPI_deveConterSegurancaJWT() {
        // When
        OpenAPI openAPI = config.customOpenAPI();

        // Then
        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearer-jwt");
    }

    @Test
    void customOpenAPI_deveAplicarSegurancaGlobal() {
        // When
        OpenAPI openAPI = config.customOpenAPI();

        // Then
        assertThat(openAPI.getSecurity()).isNotNull();
        assertThat(openAPI.getSecurity()).isNotEmpty();
    }
}
