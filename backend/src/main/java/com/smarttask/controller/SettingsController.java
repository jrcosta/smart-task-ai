package com.smarttask.controller;

import com.smarttask.dto.SettingsRequest;
import com.smarttask.dto.SettingsResponse;
import com.smarttask.security.JwtTokenProvider;
import com.smarttask.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Expõe endpoints REST para gerenciamento de configurações de usuário,
 * incluindo chaves de API para integração com serviços externos.
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Settings", description = "Gerenciamento de configurações do usuário")
@SecurityRequirement(name = "Bearer Authentication")
public class SettingsController {

    private final SettingsService settingsService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Obtém as configurações do usuário autenticado.
     */
    @GetMapping
    @Operation(summary = "Obter configurações", description = "Retorna as configurações do usuário autenticado")
    public ResponseEntity<SettingsResponse> getUserSettings(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        SettingsResponse response = settingsService.getUserSettings(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza as configurações do usuário autenticado.
     */
    @PutMapping
    @Operation(summary = "Atualizar configurações", description = "Atualiza as chaves de API e outras configurações")
    public ResponseEntity<SettingsResponse> updateUserSettings(
            @RequestHeader("Authorization") String token,
            @RequestBody SettingsRequest request) {
        Long userId = getUserIdFromToken(token);
        log.info("Atualizando configurações para o usuário {}", userId);
        SettingsResponse response = settingsService.updateUserSettings(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Extrai o ID do usuário a partir do token JWT.
     */
    private Long getUserIdFromToken(String token) {
        String jwt = token.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(jwt);
    }
}
