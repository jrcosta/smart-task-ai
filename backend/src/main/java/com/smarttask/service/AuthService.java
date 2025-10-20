package com.smarttask.service;

import com.smarttask.dto.AuthRequest;
import com.smarttask.dto.AuthResponse;
import com.smarttask.dto.RegisterRequest;
import com.smarttask.exception.ResourceAlreadyExistsException;
import com.smarttask.exception.ResourceNotFoundException;
import com.smarttask.model.User;
import com.smarttask.observability.MetricsService;
import com.smarttask.observability.Traced;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.JwtTokenProvider;
import com.smarttask.security.UserPrincipal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Centraliza regras de autenticacao e registro de usuarios, emitindo tokens JWT
 * e garantindo consistencia das credenciais.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    /** Repositorio responsavel pela persistencia de usuarios. */
    private final UserRepository userRepository;

    /** Codificador de senha utilizado no fluxo de registro. */
    private final PasswordEncoder passwordEncoder;

    /** Componente do Spring Security que realiza a autenticacao. */
    private final AuthenticationManager authenticationManager;

    /** Fornecedor de tokens JWT emitidos apos sucesso na autenticacao. */
    private final JwtTokenProvider tokenProvider;

    /**
     * Servico de metricas que registra sucesso ou falha no login ou registro.
     */
    private final MetricsService metricsService;

    /**
     * Registra um novo usuario e retorna um token JWT imediatamente apos a
     * autenticacao.
     *
     * @param request dados necessarios para criar o usuario
     * @return token JWT e informacoes resumidas do usuario registrado
     */
    @Transactional
    @Traced(value = "AuthService.register", captureParameters = false)
    public AuthResponse register(final RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        final User user = userRepository.save(User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .active(true)
                .roles(Set.of("USER"))
                .build());

    final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));

    SecurityContextHolder.getContext()
        .setAuthentication(authentication);
        final String token = tokenProvider.generateToken(authentication);

        metricsService.recordAuthenticationSuccess("register");

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }

    /**
     * Autentica um usuario existente e retorna um token JWT valido.
     *
     * @param request credenciais de acesso (usuario e senha)
     * @return dados do usuario autenticado com token JWT
     */
    @Traced(value = "AuthService.login", captureParameters = false)
    public AuthResponse login(final AuthRequest request) {
        try {
        final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));

        SecurityContextHolder.getContext()
            .setAuthentication(authentication);

        final String token = tokenProvider.generateToken(authentication);

        final Long userId = ((UserPrincipal) authentication.getPrincipal())
            .getId();

        final User user = userRepository.findById(userId)
            .orElseThrow(() -> {
            log.warn(
                "Usuario autenticado nao encontrado ao finalizar "
                    + "login (id={})",
                userId);
            return new ResourceNotFoundException(
                "Usuario nao encontrado");
            });

        metricsService.recordAuthenticationSuccess("login");

        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(user.getRoles())
            .build();
        } catch (Exception exception) {
            metricsService.recordAuthenticationFailure(
                    exception.getClass().getSimpleName());
            throw exception;
        }
    }
}
