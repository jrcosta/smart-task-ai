package com.smarttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada da aplicação Smart Task Manager, responsável por iniciar o
 * contexto Spring Boot com auditoria JPA e tarefas agendadas habilitadas.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class SmartTaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTaskManagerApplication.class, args);
    }
}
