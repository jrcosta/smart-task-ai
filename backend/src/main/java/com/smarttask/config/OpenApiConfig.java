package com.smarttask.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao do OpenAPI 3.0 (Swagger) para documentacao automatica da API.
 *
 * Acesse a documentacao em:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
 */
@Configuration
public final class OpenApiConfig {

    /** Descricao principal exibida na documentacao gerada. */
    private static final String API_DESCRIPTION =
            "API RESTful para gestao de tarefas com suporte de IA, "
                    + "notificacoes WhatsApp e observabilidade completa.";

    /**
     * Cria a especificacao base do OpenAPI 3.0 com metadados e seguranca.
     *
     * @return instancia configurada de {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final Contact contact = new Contact()
                .name("jrcosta")
                .url("https://github.com/jrcosta/smart-task-ai")
                .email("seu-email@example.com");

        final License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        return new OpenAPI()
                .info(new Info()
                        .title("Smart Task Manager API")
                        .version("1.0.0")
                        .description(API_DESCRIPTION)
                        .contact(contact)
                        .license(license))
                .addSecurityItem(
                        new SecurityRequirement().addList("bearer-jwt"))
                .components(securityComponents());
    }

    /**
     * Define os componentes de seguranca utilizados na documentacao.
     *
     * @return configuracao de componentes com o esquema bearer
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes(
                        "bearer-jwt",
                        bearerScheme());
    }

    /**
     * Descreve o esquema de seguranca Bearer (JWT) utilizado pela API.
     *
     * @return esquema configurado para autenticacao via token JWT
     */
    private SecurityScheme bearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token obtido via login");
    }
}
