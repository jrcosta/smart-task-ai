package com.smarttask.service;

import com.smarttask.dto.AIAnalysisRequest;
import com.smarttask.dto.AIAnalysisResponse;
import com.smarttask.model.Task.TaskPriority;
import com.smarttask.observability.MetricsService;
import com.smarttask.observability.Traced;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Fornece integrações com a OpenAI para análise de tarefas e geração de
 * relatórios, sempre garantindo um fallback local quando não há credenciais.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AIService {

    /** Tempo padrão, em segundos, para timeout das requisições ao ChatGPT. */
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    /** Temperatura padrão utilizada na geração das respostas. */
    private static final double DEFAULT_TEMPERATURE = 0.7d;

    /** Tamanho limite das mensagens simuladas de resumo. */
    private static final int MOCK_SUMMARY_LIMIT = 50;

    /** Papel de sistema nas mensagens enviadas ao endpoint de chat. */
    private static final String SYSTEM_ROLE = "system";

    /** Papel atribuído às mensagens do usuário. */
    private static final String USER_ROLE = "user";

    /** Tag genérica aplicada nas respostas de fallback. */
    private static final String FALLBACK_TAG_PRIMARY = "geral";

    /** Tag adicional aplicada nas respostas de fallback. */
    private static final String FALLBACK_TAG_SECONDARY = "pendente";

    /** Estimativa de horas utilizada quando não há dados da IA. */
    private static final int FALLBACK_ESTIMATED_HOURS = 2;

    /** Comprimento do prefixo "RESUMO:" para facilitar parsing. */
    private static final int RESUMO_PREFIX_LENGTH = "RESUMO:".length();

    /** Comprimento do prefixo "PRIORIDADE:". */
    private static final int PRIORIDADE_PREFIX_LENGTH = "PRIORIDADE:".length();

    /** Comprimento do prefixo "HORAS:". */
    private static final int HORAS_PREFIX_LENGTH = "HORAS:".length();

    /** Comprimento do prefixo "TAGS:". */
    private static final int TAGS_PREFIX_LENGTH = "TAGS:".length();

    /** Comprimento do prefixo "SUBTAREFAS:". */
    private static final int SUBTAREFAS_PREFIX_LENGTH = "SUBTAREFAS:".length();

    /** Comprimento do prefixo "ANÁLISE:". */
    private static final int ANALISE_PREFIX_LENGTH = "ANÁLISE:".length();

    /** Separador de tags esperado na resposta da IA. */
    private static final String TAG_SEPARATOR = ",";

    /** Separador de subtarefas esperado na resposta da IA. */
    private static final String SUBTASK_SEPARATOR = ";";

    /** Mensagem exibida quando a análise acontece em modo offline. */
    private static final String MOCK_ANALYSIS_MESSAGE =
        "Configure a chave da API OpenAI para obter análises detalhadas"
            + " com IA.";

    /** Prompt base utilizado para orientar o papel do assistente. */
    private static final String SYSTEM_PROMPT_MESSAGE =
        String.join(
            System.lineSeparator(),
            "Você é um assistente especializado em análise de tarefas e "
                + "produtividade.",
            "Analise as informações fornecidas e retorne uma resposta "
                + "estruturada.");

    /** Template utilizado para solicitar relatórios de produtividade. */
    private static final String REPORT_PROMPT_TEMPLATE =
        String.join(
            System.lineSeparator(),
            "Gere um relatório de produtividade baseado nos seguintes dados:",
            "Tarefas concluídas: %d",
            "Total de horas trabalhadas: %d",
            "Tarefas: %s",
            "",
            "Forneça insights sobre produtividade, padrões e sugestões de "
                + "melhoria.");

    /** Template para mensagens exibidas no relatório simplificado offline. */
    private static final String REPORT_TEMPLATE =
        String.join(
            System.lineSeparator(),
            "Relatório de Produtividade",
            "",
            "Você completou %d tarefas em %d horas.",
            "Média de %.1f horas por tarefa.",
            "",
            "Configure a API OpenAI para relatórios detalhados com insights "
                + "de IA.");

    /**
     * Chave global utilizada quando o usuário não possui chave própria.
     */
    @Value("${openai.api-key:}")
    private String defaultApiKey;

    /**
     * Modelo padrão usado nos pedidos ao endpoint de chat da OpenAI.
     */
    @Value("${openai.model}")
    private String model;

    /**
     * Quantidade máxima de tokens solicitados por requisição.
     */
    @Value("${openai.max-tokens}")
    private Integer maxTokens;

    /**
     * Servico de metricas para registrar o comportamento das chamadas a OpenAI.
     */
    private final MetricsService metricsService;

    /**
     * Servico de configuracoes do usuario, responsavel pelas chaves
     * individuais.
     */
    private final SettingsService settingsService;

    /**
     * Analisa uma tarefa via OpenAI, caindo para um resultado simulado quando
     * não há credenciais válidas.
     *
     * @param request dados da tarefa a ser analisada
     * @param userId identificador do usuário solicitante
     * @return resposta estruturada com sugestões da IA ou fallback local
     */
    @Traced(value = "AIService.analyzeTask", captureParameters = true)
    public AIAnalysisResponse analyzeTask(final AIAnalysisRequest request,
            final Long userId) {
        final long startTime = System.currentTimeMillis();

        try {
        final String apiKey = resolveApiKey(userId);

        if (needsFallback(apiKey)) {
        return createMockAnalysis(request.getText());
        }

        final OpenAiService service = new OpenAiService(
            apiKey,
            Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));

        final String prompt = buildAnalysisPrompt(
            request.getText(),
            request.getContext());

            final ChatCompletionRequest completionRequest =
                    ChatCompletionRequest.builder()
                            .model(model)
                            .messages(buildMessages(prompt))
                            .maxTokens(maxTokens)
                            .temperature(DEFAULT_TEMPERATURE)
                            .build();

        final String response = service
            .createChatCompletion(completionRequest)
            .getChoices()
            .get(0)
            .getMessage()
            .getContent();

            metricsService.recordAIAnalysis(true);
            return parseAIResponse(response);
        } catch (Exception exception) {
            log.error("Erro ao chamar a API OpenAI", exception);
            metricsService.recordAIAnalysis(false);
            return createMockAnalysis(request.getText());
        } finally {
            metricsService.recordAIAnalysisDuration(
                    System.currentTimeMillis() - startTime);
        }
    }

    private String resolveApiKey(final Long userId) {
        final String userKey = settingsService.getDecryptedOpenAIKey(userId);
        if (userKey == null || userKey.isEmpty()) {
            return defaultApiKey;
        }
        return userKey;
    }

    private boolean needsFallback(final String apiKey) {
        return apiKey == null || apiKey.isEmpty()
                || "your-api-key-here".equals(apiKey);
    }

    private List<ChatMessage> buildMessages(final String prompt) {
        final ChatMessage systemMessage = new ChatMessage(SYSTEM_ROLE,
                SYSTEM_PROMPT_MESSAGE);
        final ChatMessage userMessage = new ChatMessage(USER_ROLE, prompt);
        return Arrays.asList(systemMessage, userMessage);
    }

    private String buildAnalysisPrompt(final String taskText,
            final String context) {
        final StringBuilder prompt = new StringBuilder();
        prompt.append("Analise a seguinte tarefa e forneca:\n");
        prompt.append("1. Um resumo conciso\n");
        prompt.append("2. Prioridade sugerida (LOW, MEDIUM, HIGH, URGENT)\n");
        prompt.append("3. Estimativa de horas necessarias\n");
        prompt.append("4. Tags relevantes (maximo 5)\n");
        prompt.append("5. Sugestoes de subtarefas (se aplicavel)\n\n");
        prompt.append("Tarefa: ")
                .append(taskText)
                .append("\n");

        if (context != null && !context.isEmpty()) {
            prompt.append("Contexto adicional: ")
                    .append(context)
                    .append("\n");
        }

        prompt.append("\nFormato de resposta esperado:\n");
        prompt.append("RESUMO: [resumo]\n");
        prompt.append("PRIORIDADE: [LOW|MEDIUM|HIGH|URGENT]\n");
        prompt.append("HORAS: [numero]\n");
        prompt.append("TAGS: [tag1, tag2, tag3]\n");
        prompt.append("SUBTAREFAS: [subtarefa1; subtarefa2; subtarefa3]\n");
        prompt.append("ANALISE: [analise detalhada]");

        return prompt.toString();
    }

    private AIAnalysisResponse parseAIResponse(final String response) {
        final AIAnalysisResponse analysis = new AIAnalysisResponse();

        try {
            final String[] lines = response.split("\n");

            for (final String line : lines) {
                final String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                final String normalized =
                        trimmed.toUpperCase(Locale.ROOT);

                if (normalized.startsWith("RESUMO:")) {
                    analysis.setSummary(trimmed
                            .substring(RESUMO_PREFIX_LENGTH).trim());
                } else if (normalized.startsWith("PRIORIDADE:")) {
                    final String priority = trimmed
                            .substring(PRIORIDADE_PREFIX_LENGTH).trim();
                    analysis.setSuggestedPriority(parsePriority(priority));
                } else if (normalized.startsWith("HORAS:")) {
                    parseEstimatedHours(trimmed
                            .substring(HORAS_PREFIX_LENGTH).trim())
                            .ifPresent(analysis::setEstimatedHours);
                } else if (normalized.startsWith("TAGS:")) {
                    final String tagsValue = trimmed
                            .substring(TAGS_PREFIX_LENGTH)
                            .trim();
                    analysis.setSuggestedTags(
                            parseList(tagsValue, TAG_SEPARATOR));
                } else if (normalized.startsWith("SUBTAREFAS:")) {
                    final String subtasksValue = trimmed
                            .substring(SUBTAREFAS_PREFIX_LENGTH)
                            .trim();
                    analysis.setSuggestedSubtasks(
                            parseList(subtasksValue, SUBTASK_SEPARATOR));
                } else if (normalized.startsWith("ANÁLISE:")) {
                    analysis.setAnalysis(trimmed
                            .substring(ANALISE_PREFIX_LENGTH).trim());
                }
            }
        } catch (Exception exception) {
            log.error("Erro ao interpretar a resposta da IA", exception);
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

    private TaskPriority parsePriority(final String rawPriority) {
        if (rawPriority == null || rawPriority.isBlank()) {
            return TaskPriority.MEDIUM;
        }

        final String normalized = rawPriority.replaceAll("[^A-Za-z]", "")
                .toUpperCase(Locale.ROOT);

        return Arrays.stream(TaskPriority.values())
                .filter(priority -> normalized.startsWith(priority.name()))
                .findFirst()
                .orElse(TaskPriority.MEDIUM);
    }

    private Optional<Integer> parseEstimatedHours(final String rawHours) {
        if (rawHours == null || rawHours.isBlank()) {
            return Optional.empty();
        }

        final String digits = rawHours.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(digits));
        } catch (NumberFormatException exception) {
            log.warn(
                    "Horas estimadas invalidas retornadas pela IA: {}",
                    rawHours,
                    exception);
            return Optional.empty();
        }
    }

    private List<String> parseList(final String raw, final String separator) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(raw.split(separator))
                .map(String::trim)
                .filter(item -> !item.isEmpty() && !"-".equalsIgnoreCase(item))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private AIAnalysisResponse createMockAnalysis(final String taskText) {
        final AIAnalysisResponse response = new AIAnalysisResponse();
    final int summaryLimit = Math.min(
        MOCK_SUMMARY_LIMIT,
        taskText.length());
        final String summarySegment = taskText.substring(0, summaryLimit);
        response.setSummary("Tarefa analisada: " + summarySegment);
        response.setSuggestedPriority(TaskPriority.MEDIUM);
        response.setEstimatedHours(FALLBACK_ESTIMATED_HOURS);
        response.setSuggestedTags(
                Arrays.asList(FALLBACK_TAG_PRIMARY, FALLBACK_TAG_SECONDARY));
        response.setSuggestedSubtasks(new ArrayList<>());
        response.setAnalysis(MOCK_ANALYSIS_MESSAGE);
        return response;
    }

    /**
     * Gera um relatorio de produtividade consolidado via OpenAI.
     *
     * @param completedTasks tarefas concluidas no periodo
     * @param totalHours horas totais de esforco registrado
     * @param userId identificador do usuario solicitante
     * @return texto com resumo de produtividade ou fallback local
     */
    public String generateProductivityReport(final List<String> completedTasks,
            final int totalHours, final Long userId) {
        try {
            final String apiKey = resolveApiKey(userId);

            if (needsFallback(apiKey)) {
                return generateMockReport(completedTasks.size(), totalHours);
            }

        final OpenAiService service = new OpenAiService(
            apiKey,
            Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));

        final String joinedTasks = String.join(", ", completedTasks);
        final String prompt = String.format(
            REPORT_PROMPT_TEMPLATE,
            completedTasks.size(),
            totalHours,
            joinedTasks);

        final ChatCompletionRequest completionRequest =
            ChatCompletionRequest.builder()
                .model(model)
                .messages(
                    List.of(new ChatMessage(USER_ROLE, prompt)))
                .maxTokens(maxTokens)
                .temperature(DEFAULT_TEMPERATURE)
                .build();

            return service
                    .createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();
        } catch (Exception exception) {
            log.error("Erro ao gerar relatorio de produtividade", exception);
            return generateMockReport(completedTasks.size(), totalHours);
        }
    }

    private String generateMockReport(final int taskCount, final int hours) {
        final double average = hours / (double) Math.max(taskCount, 1);
        return String.format(REPORT_TEMPLATE, taskCount, hours, average);
    }
}
