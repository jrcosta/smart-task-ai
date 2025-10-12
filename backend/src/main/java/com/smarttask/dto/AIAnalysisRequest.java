package com.smarttask.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dados enviados ao serviço de IA, contendo texto principal e contexto
 * auxiliar para análise.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisRequest {
    
    @NotBlank
    private String text;
    
    private String context;
}
