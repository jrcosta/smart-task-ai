package com.smarttask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dados necessarios para criacao de um novo usuario na plataforma.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    /** Tamanho minimo aceito para o campo username. */
    private static final int MIN_USERNAME_LENGTH = 3;

    /** Tamanho maximo permitido para o campo username. */
    private static final int MAX_USERNAME_LENGTH = 50;

    /** Tamanho minimo aceito para o campo password. */
    private static final int MIN_PASSWORD_LENGTH = 6;

    /** Tamanho maximo permitido para o campo password. */
    private static final int MAX_PASSWORD_LENGTH = 100;

    /** Nome de usuario unico utilizado para autenticacao. */
    @NotBlank
    @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH)
    private String username;

    /** Endereco de e-mail valido do usuario. */
    @NotBlank
    @Email
    private String email;

    /** Senha que sera armazenada de forma segura pela aplicacao. */
    @NotBlank
    @Size(min = MIN_PASSWORD_LENGTH, max = MAX_PASSWORD_LENGTH)
    private String password;

    /** Nome completo opcional para personalizar respostas. */
    private String fullName;
}
