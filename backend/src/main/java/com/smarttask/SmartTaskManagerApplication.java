package com.smarttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point da aplicação Smart Task AI.
 *
 * <p>Inicializa o contexto Spring Boot habilitando autoconfiguração, auditoria
 * JPA e agendamentos periódicos. A aplicação oferece API REST com autenticação
 * JWT, integrações com OpenAI e Twilio, além de observabilidade via
 * OpenTelemetry.</p>
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public final class SmartTaskManagerApplication {

    private SmartTaskManagerApplication() {
        // Impede instanciação acidental desta classe utilitária.
    }

    /**
     * Método principal que inicia a aplicação Spring Boot.
     *
     * @param args argumentos de linha de comando
     */
    public static void main(final String[] args) {
        SpringApplication.run(SmartTaskManagerApplication.class, args);
    }
}
