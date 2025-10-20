package com.smarttask.controller;

import com.smarttask.dto.AIAnalysisRequest;
import com.smarttask.dto.AIAnalysisResponse;
import com.smarttask.security.JwtTokenProvider;
import com.smarttask.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expoe endpoint REST que redireciona analises de IA para o
 * {@link com.smarttask.service.AIService}.
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public final class AIController {

    /** Servico de analise IA responsavel pela logica de negocios. */
    private final AIService aiService;

    /** Provedor de tokens JWT utilizado para extrair informacoes do usuario. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Processa a requisicao de analise de uma tarefa utilizando IA.
     *
     * @param request dados necessarios para gerar a analise
     * @param token cabecalho Authorization contendo o token JWT
     * @return resultado estruturado da analise de IA
     */
    @PostMapping("/analyze")
    public ResponseEntity<AIAnalysisResponse> analyzeTask(
            @Valid @RequestBody final AIAnalysisRequest request,
            @RequestHeader("Authorization") final String token) {
        final Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(aiService.analyzeTask(request, userId));
    }

    /**
     * Extrai o ID do usuario a partir do token JWT.
     *
     * @param token valor completo do cabecalho Authorization
     * @return identificador do usuario autenticado
     */
    private Long getUserIdFromToken(final String token) {
        final String jwt = token.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(jwt);
    }
}
