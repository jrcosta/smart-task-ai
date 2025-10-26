package com.smarttask.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

    @Test
    void tokenGerado_deveSerValido_eConterIdDoUsuario() {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(
                provider,
                "jwtSecret",
                "test-secret-key-para-jwt-123456789012345");
        ReflectionTestUtils.setField(provider, "jwtExpiration", 3_600_000L);

        UserPrincipal principal = new UserPrincipal(
                7L,
                "usuario",
                "user@smarttask.ai",
                "senha",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        String token = provider.generateToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUserIdFromToken(token)).isEqualTo(7L);
    }
}
