package com.smarttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal (entry point) da aplicação Smart Task Manager.
 * 
 * <p>Responsável por inicializar o contexto Spring Boot com as seguintes configurações:</p>
 * <ul>
 *   <li>{@link SpringBootApplication} - Ativa autoconfiguração Spring Boot</li>
 *   <li>{@link EnableJpaAuditing} - Ativa rastreamento automático de criação/modificação de entidades</li>
 *   <li>{@link EnableScheduling} - Ativa suporte a tarefas agendadas com @Scheduled</li>
 * </ul>
 * 
 * <p>A aplicação gerencia:</p>
 * <ul>
 *   <li>API REST para gerenciamento de tarefas com autenticação JWT</li>
 *   <li>Integração com OpenAI para análise inteligente de tarefas</li>
 *   <li>Notificações via Twilio WhatsApp</li>
 *   <li>Observabilidade com OpenTelemetry (Jaeger, Prometheus, Grafana)</li>
 *   <li>Persistência em PostgreSQL com Spring Data JPA</li>
 * </ul>
 * 
 * <p>Para iniciar: {@code mvn spring-boot:run}</p>
 * 
 * @author Smart Task AI Team
 * @version 1.0.0
 * @since 2025-10
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see org.springframework.data.jpa.repository.config.EnableJpaAuditing
 * @see org.springframework.scheduling.annotation.EnableScheduling
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class SmartTaskManagerApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot.
     * 
     * @param args Argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(SmartTaskManagerApplication.class, args);
    }
}
