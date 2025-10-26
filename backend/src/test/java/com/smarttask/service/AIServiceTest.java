package com.smarttask.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smarttask.dto.AIAnalysisRequest;
import com.smarttask.dto.AIAnalysisResponse;
import com.smarttask.model.Task.TaskPriority;
import com.smarttask.observability.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @Mock
    private MetricsService metricsService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private AIService aiService;

    @BeforeEach
    void configurarCampos() {
        ReflectionTestUtils.setField(aiService, "defaultApiKey", "");
        ReflectionTestUtils.setField(aiService, "model", "gpt-4o-mini");
        ReflectionTestUtils.setField(aiService, "maxTokens", 256);
    }

    @Test
    void analyzeTask_quandoNaoHaChave_deveRetornarFallback() {
        when(settingsService.getDecryptedOpenAIKey(1L)).thenReturn(null);

        AIAnalysisRequest request = new AIAnalysisRequest();
        request.setText("Implementar login com JWT");

        AIAnalysisResponse response = aiService.analyzeTask(request, 1L);

        assertThat(response.getSuggestedPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(response.getSuggestedTags()).contains("geral");
        assertThat(response.getEstimatedHours()).isEqualTo(2);
    verify(metricsService, never()).recordAIAnalysis(anyBoolean());
        verify(metricsService).recordAIAnalysisDuration(anyLong());
    }
}
