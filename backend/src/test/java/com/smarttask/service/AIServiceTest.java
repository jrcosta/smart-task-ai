package com.smarttask.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AIService.class })
@TestPropertySource(properties = {
        "openai.apiKey="
})
class AIServiceTest {

    // Quando AIService estiver anotado como @Service e usar injeção por @Value,
    // prefira um teste com Spring Boot:
    // @SpringBootTest(classes = AIService.class, properties = "openai.apiKey=")
    // @Autowired AIService aiService;

    @Test
    void analyzeTask_quandoSemApiKey_deveRetornarAnaliseMock() {
        AIService aiService = new AIService(); // Requer construtor padrão; ajuste se necessário
        String desc = "Implementar login com JWT";
        String analysis = aiService.analyzeTask(desc);

        assertThat(analysis).isNotBlank();
        assertThat(analysis).contains("PRIORIDADE:");
        assertThat(analysis).contains("TAGS:");
        assertThat(analysis).contains("SUBTAREFAS:");
        assertThat(analysis).contains("ANÁLISE:");
        assertThat(analysis).contains("HORAS:");
    }
}
