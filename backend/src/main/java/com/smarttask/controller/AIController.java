package com.smarttask.controller;

import com.smarttask.dto.AIAnalysisRequest;
import com.smarttask.dto.AIAnalysisResponse;
import com.smarttask.security.JwtTokenProvider;
import com.smarttask.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Disponibiliza o endpoint responsável por delegar análises de IA para as
 * tarefas, servindo como fachada HTTP do {@link com.smarttask.service.AIService}.
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/analyze")
    public ResponseEntity<AIAnalysisResponse> analyzeTask(
            @Valid @RequestBody AIAnalysisRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(aiService.analyzeTask(request, userId));
    }

    /**
     * Extrai o ID do usuário a partir do token JWT.
     */
    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(jwt);
    }
}
