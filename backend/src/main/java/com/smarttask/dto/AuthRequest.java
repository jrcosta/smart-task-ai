package com.smarttask.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para receber credenciais de login submetidas pelo usuário.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    /** Nome de usuário informado no processo de login. */
    @NotBlank
    private String username;

    /** Senha em texto plano submetida para autenticação. */
    @NotBlank
    private String password;
}
