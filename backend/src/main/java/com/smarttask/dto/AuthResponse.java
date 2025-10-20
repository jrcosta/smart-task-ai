package com.smarttask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Estrutura de resposta emitida após autenticação bem-sucedida, contendo token
 * JWT e metadados do usuário autenticado.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    /** Token JWT emitido pelo backend. */
    private String token;

    /** Tipo do token de autenticação retornado. */
    private String type = "Bearer";

    /** Identificador do usuário autenticado. */
    private Long id;

    /** Nome de usuário. */
    private String username;

    /** Endereço de e-mail associado ao usuário. */
    private String email;

    /** Conjunto de papéis concedidos ao usuário. */
    private Set<String> roles;
}
