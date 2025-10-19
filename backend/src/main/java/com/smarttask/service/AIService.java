package com.smarttask.service;

import com.smarttask.dto.AIAnalysisRequest;
import com.smarttask.dto.AIAnalysisResponse;
import com.smarttask.model.Task.TaskPriority;
import com.smarttask.observability.MetricsService;
import com.smarttask.observability.Traced;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Fornece integrações com a API OpenAI para análises de tarefas e relatórios de
 * produtividade, oferecendo fallback mock quando a chave não está configurada.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AIService {

    @Value("${openai.api-key:}")
    private String defaultApiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private Integer maxTokens;
    
    private final MetricsService metricsService;
    private final SettingsService settingsService;

    @Traced(value = "AIService.analyzeTask", captureParameters = true)
    public AIAnalysisResponse analyzeTask(AIAnalysisRequest request, Long userId) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        
        try {
            // Obtém a chave do usuário ou usa a padrão
            String apiKey = settingsService.getDecryptedOpenAIKey(userId);
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = defaultApiKey;
            }
            
            // Verifica se alguma API key está configurada
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
                return createMockAnalysis(request.getText());
            }

            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));

            String prompt = buildAnalysisPrompt(request.getText(), request.getContext());

            ChatMessage systemMessage = new ChatMessage("system", 
                "Você é um assistente especializado em análise de tarefas e produtividade. " +
                "Analise a tarefa fornecida e retorne uma análise estruturada.");
            
            ChatMessage userMessage = new ChatMessage("user", prompt);

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(Arrays.asList(systemMessage, userMessage))
                    .maxTokens(maxTokens)
                    .temperature(0.7)
                    .build();

            String response = service.createChatCompletion(completionRequest)
                    .getChoices().get(0).getMessage().getContent();

            success = true;
            metricsService.recordAIAnalysis(true);
            return parseAIResponse(response);

        } catch (Exception e) {
            log.error("Error calling OpenAI API: ", e);
            metricsService.recordAIAnalysis(false);
            return createMockAnalysis(request.getText());
        } finally {
            metricsService.recordAIAnalysisDuration(System.currentTimeMillis() - startTime);
        }
    }

    private String buildAnalysisPrompt(String taskText, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analise a seguinte tarefa e forneça:\n");
        prompt.append("1. Um resumo conciso\n");
        prompt.append("2. Prioridade sugerida (LOW, MEDIUM, HIGH, URGENT)\n");
        prompt.append("3. Estimativa de horas necessárias\n");
        prompt.append("4. Tags relevantes (máximo 5)\n");
        prompt.append("5. Sugestões de subtarefas (se aplicável)\n\n");
        prompt.append("Tarefa: ").append(taskText).append("\n");
        
        if (context != null && !context.isEmpty()) {
            prompt.append("Contexto adicional: ").append(context).append("\n");
        }
        
        prompt.append("\nFormato de resposta esperado:\n");
        prompt.append("RESUMO: [resumo]\n");
        prompt.append("PRIORIDADE: [LOW|MEDIUM|HIGH|URGENT]\n");
        prompt.append("HORAS: [número]\n");
        prompt.append("TAGS: [tag1, tag2, tag3]\n");
        prompt.append("SUBTAREFAS: [subtarefa1; subtarefa2; subtarefa3]\n");
        prompt.append("ANÁLISE: [análise detalhada]");
        
        return prompt.toString();
    }

    private AIAnalysisResponse parseAIResponse(String response) {
        AIAnalysisResponse analysis = new AIAnalysisResponse();

        try {
            String[] lines = response.split("\n");

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                if (trimmed.toUpperCase(Locale.ROOT).startsWith("RESUMO:")) {
                    analysis.setSummary(trimmed.substring(7).trim());
                } else if (trimmed.toUpperCase(Locale.ROOT).startsWith("PRIORIDADE:")) {
                    String priority = trimmed.substring(11).trim();
                    analysis.setSuggestedPriority(parsePriority(priority));
                } else if (trimmed.toUpperCase(Locale.ROOT).startsWith("HORAS:")) {
                    parseEstimatedHours(trimmed.substring(6).trim()).ifPresent(analysis::setEstimatedHours);
                } else if (trimmed.toUpperCase(Locale.ROOT).startsWith("TAGS:")) {
                    analysis.setSuggestedTags(parseList(trimmed.substring(5).trim(), ","));
                } else if (trimmed.toUpperCase(Locale.ROOT).startsWith("SUBTAREFAS:")) {
                    analysis.setSuggestedSubtasks(parseList(trimmed.substring(11).trim(), ";"));
                } else if (trimmed.toUpperCase(Locale.ROOT).startsWith("ANÁLISE:")) {
                    analysis.setAnalysis(trimmed.substring(8).trim());
                }
            }
        } catch (Exception e) {
            log.error("Error parsing AI response: ", e);
        }

        if (analysis.getSuggestedPriority() == null) {
            analysis.setSuggestedPriority(TaskPriority.MEDIUM);
        }
        if (analysis.getSuggestedTags() == null) {
            analysis.setSuggestedTags(new ArrayList<>());
        }
        if (analysis.getSuggestedSubtasks() == null) {
            analysis.setSuggestedSubtasks(new ArrayList<>());
        }

        return analysis;
    }

    private TaskPriority parsePriority(String rawPriority) {
        if (rawPriority == null || rawPriority.isBlank()) {
            return TaskPriority.MEDIUM;
        }

        String normalized = rawPriority
                .replaceAll("[^A-Za-z]", "")
                .toUpperCase(Locale.ROOT);

        return Arrays.stream(TaskPriority.values())
                .filter(priority -> normalized.startsWith(priority.name()))
                .findFirst()
                .orElse(TaskPriority.MEDIUM);
    }

    private Optional<Integer> parseEstimatedHours(String rawHours) {
        if (rawHours == null || rawHours.isBlank()) {
            return Optional.empty();
        }

        String digits = rawHours.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(digits));
        } catch (NumberFormatException ex) {
            log.warn("Não foi possível converter horas estimadas retornadas pela IA: {}", rawHours, ex);
            return Optional.empty();
        }
    }

    private List<String> parseList(String raw, String separator) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>();
        }

    return Arrays.stream(raw.split(separator))
        .map(String::trim)
        .filter(item -> !item.isEmpty() && !item.equalsIgnoreCase("-"))
        .collect(Collectors.toCollection(ArrayList::new));
    }

    private AIAnalysisResponse createMockAnalysis(String taskText) {
        // Análise mock quando a API não está configurada
        AIAnalysisResponse response = new AIAnalysisResponse();
        response.setSummary("Tarefa analisada: " + taskText.substring(0, Math.min(50, taskText.length())));
        response.setSuggestedPriority(TaskPriority.MEDIUM);
        response.setEstimatedHours(2);
        response.setSuggestedTags(Arrays.asList("geral", "pendente"));
        response.setSuggestedSubtasks(new ArrayList<>());
        response.setAnalysis("Configure a chave da API OpenAI para obter análises detalhadas com IA.");
        
        return response;
    }

    public String generateProductivityReport(List<String> completedTasks, int totalHours, Long userId) {
        try {
            // Obtém a chave do usuário ou usa a padrão
            String apiKey = settingsService.getDecryptedOpenAIKey(userId);
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = defaultApiKey;
            }
            
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-api-key-here")) {
                return generateMockReport(completedTasks.size(), totalHours);
            }

            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));

            String prompt = String.format(
                "Gere um relatório de produtividade baseado nos seguintes dados:\n" +
                "Tarefas concluídas: %d\n" +
                "Total de horas trabalhadas: %d\n" +
                "Tarefas: %s\n\n" +
                "Forneça insights sobre produtividade, padrões e sugestões de melhoria.",
                completedTasks.size(), totalHours, String.join(", ", completedTasks)
            );

            ChatMessage userMessage = new ChatMessage("user", prompt);

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(userMessage))
                    .maxTokens(maxTokens)
                    .temperature(0.7)
                    .build();

            return service.createChatCompletion(completionRequest)
                    .getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            log.error("Error generating productivity report: ", e);
            return generateMockReport(completedTasks.size(), totalHours);
        }
    }

    private String generateMockReport(int taskCount, int hours) {
        return String.format(
            "Relatório de Produtividade\n\n" +
            "Você completou %d tarefas em %d horas.\n" +
            "Média de %.1f horas por tarefa.\n\n" +
            "Configure a API OpenAI para relatórios detalhados com insights de IA.",
            taskCount, hours, hours / (double) Math.max(taskCount, 1)
        );
    }
}
