package com.smarttask.controller;

import com.smarttask.dto.AuthRequest;
import com.smarttask.dto.AuthResponse;
import com.smarttask.dto.RegisterRequest;
import com.smarttask.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expõe endpoints REST para cadastro e autenticação de usuários, delegando as
 * regras de negócio ao {@link AuthService}.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public final class AuthController {

    /** Serviço responsável pelo fluxo de autenticação e cadastro. */
    private final AuthService authService;

    /**
     * Realiza o cadastro de um novo usuário na plataforma.
     *
     * @param request dados de registro do usuário
     * @return token JWT e demais informações de autenticação
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody final RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Executa o fluxo de login e emite um token de acesso válido.
     *
     * @param request credenciais fornecidas pelo usuário
     * @return token JWT e dados de sessão
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody final AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
