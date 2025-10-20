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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expoe endpoints REST para gerenciamento de configuracoes de usuario.
 * Inclui chaves de API para integracoes com servicos externos.
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Settings",
        description = "Gerenciamento de configuracoes do "
                + "usuario")
@SecurityRequirement(name = "Bearer Authentication")
public final class SettingsController {

    /** Servico responsavel por persistir as configuracoes de usuario. */
    private final SettingsService settingsService;

    /** Utilitario para extrair informacoes a partir de tokens JWT. */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Obtem as configuracoes do usuario autenticado.
     *
     * @param token cabecalho Authorization contendo o token JWT
     * @return configuracoes atualmente persistidas para o usuario
     */
    @GetMapping
    @Operation(summary = "Obter configuracoes",
            description = "Retorna as configuracoes do usuario autenticado")
    public ResponseEntity<SettingsResponse> getUserSettings(
            @RequestHeader("Authorization") final String token) {
        final Long userId = getUserIdFromToken(token);
    final SettingsResponse response =
        settingsService.getUserSettings(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza as configuracoes do usuario autenticado.
     *
     * @param token cabecalho Authorization contendo o token JWT
     * @param request dados de configuracao enviados pelo frontend
     * @return configuracoes atualizadas apos a persistencia
     */
    @PutMapping
    @Operation(summary = "Atualizar configuracoes",
            description = "Atualiza as chaves de API e outras "
                    + "configuracoes")
    public ResponseEntity<SettingsResponse> updateUserSettings(
            @RequestHeader("Authorization") final String token,
            @RequestBody final SettingsRequest request) {
    final Long userId = getUserIdFromToken(token);
    log.info(
        "Atualizando configuracoes para o usuario {}",
        userId);
    final SettingsResponse response = settingsService.updateUserSettings(
        userId,
        request);
    return ResponseEntity.ok(response);
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
