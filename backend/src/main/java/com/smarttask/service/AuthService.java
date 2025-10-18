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
import com.smarttask.security.UserPrincipal;
import com.smarttask.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Centraliza regras de autenticação e registro de usuários, emitindo tokens JWT
 * e garantindo consistência das credenciais.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MetricsService metricsService;

    @Transactional
    @Traced(value = "AuthService.register", captureParameters = false)
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .active(true)
                .roles(Set.of("USER"))
                .build();

        user = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
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

    @Traced(value = "AuthService.login", captureParameters = false)
    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);

            Long userId = ((UserPrincipal) authentication.getPrincipal()).getId();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("Usuário autenticado não encontrado ao finalizar login (id={})", userId);
                        return new ResourceNotFoundException("Usuário não encontrado");
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
        } catch (Exception e) {
            metricsService.recordAuthenticationFailure(e.getClass().getSimpleName());
            throw e;
        }
    }
}
