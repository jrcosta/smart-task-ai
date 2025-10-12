package com.smarttask.controller;

import com.smarttask.dto.NotificationPreferenceRequest;
import com.smarttask.dto.NotificationPreferenceResponse;
import com.smarttask.security.UserPrincipal;
import com.smarttask.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Gerencia as preferências de notificação via WhatsApp e expõe endpoints para
 * testes e persistência das configurações do usuário.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(notificationService.getPreferences(currentUser));
    }

    @PostMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> savePreferences(
            @Valid @RequestBody NotificationPreferenceRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(notificationService.savePreferences(request, currentUser));
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        notificationService.sendTestNotification(currentUser);
        return ResponseEntity.ok(Map.of(
            "message", "Mensagem de teste enviada com sucesso! Verifique seu WhatsApp."
        ));
    }
}
