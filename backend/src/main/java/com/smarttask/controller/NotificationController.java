package com.smarttask.controller;

import com.smarttask.dto.NotificationPreferenceRequest;
import com.smarttask.dto.NotificationPreferenceResponse;
import com.smarttask.security.UserPrincipal;
import com.smarttask.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Gerencia preferencias de notificacao via WhatsApp.
 * Expoe endpoints para testes e persistencia das configuracoes.
 * Cada usuario pode ajustar suas preferencias de notificacao.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public final class NotificationController {

    /** Servico que centraliza regras das notificacoes WhatsApp. */
    private final NotificationService notificationService;

    /**
     * Recupera as preferencias de notificacao do usuario autenticado.
     *
        * @param currentUser contexto de autenticacao fornecido pelo Spring
        *                    Security
     * @return preferencias persistidas para o usuario
     */
    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse>
            getPreferences(
                    @AuthenticationPrincipal final UserPrincipal currentUser) {
        return ResponseEntity.ok(
                notificationService.getPreferences(currentUser));
    }

    /**
     * Persiste as preferencias de envio de notificacoes do usuario atual.
     *
     * @param request dados de configuracao submetidos pelo frontend
     * @param currentUser usuario autenticado associado ao token
     * @return preferencias atualizadas apos a persistencia
     */
    @PostMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse>
            savePreferences(
                    @Valid @RequestBody final NotificationPreferenceRequest
                            request,
                    @AuthenticationPrincipal final UserPrincipal currentUser) {
        return ResponseEntity.ok(
                notificationService.savePreferences(request, currentUser));
    }

    /**
     * Envia mensagem de teste via WhatsApp para validar a integracao.
     * Tambem confirma que as credenciais estao configuradas.
     *
     * @param currentUser usuario autenticado que recebera a notificacao
     * @return mensagem de confirmacao do envio
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification(
            @AuthenticationPrincipal final UserPrincipal currentUser) {
        notificationService.sendTestNotification(currentUser);
        return ResponseEntity.ok(Map.of(
                "message",
                "Mensagem de teste enviada com sucesso! Verifique seu "
                        + "WhatsApp."));
    }
}
