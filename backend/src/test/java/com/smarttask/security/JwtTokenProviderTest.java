package com.smarttask.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    @Test
    void tokenGerado_deveSerValido_eConterSubject() {
        // Ajuste a construção conforme sua implementação real
        // Exemplos comuns:
        // JwtTokenProvider provider = new JwtTokenProvider("test-secret-key-please-change", 3600000L);
        // ou injete via setters/métodos de fábrica, se existirem
        JwtTokenProvider provider = new JwtTokenProvider();

        String username = "user@test.com";
        String token = provider.generateToken(username);

        assertThat(token).isNotBlank();
        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUsernameFromToken(token)).isEqualTo(username);
    }
}
