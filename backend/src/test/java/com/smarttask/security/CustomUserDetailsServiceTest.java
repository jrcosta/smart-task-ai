package com.smarttask.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.smarttask.model.User;
import com.smarttask.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Testes unitários para CustomUserDetailsService.
 * Valida o carregamento de usuários para autenticação.
 */
class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_deveCarregarUsuarioPorUsername() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .active(true)
                .roles(Set.of("USER"))
                .build();
        
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.isEnabled()).isTrue();
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        when(userRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("not found")
                .hasMessageContaining("nonexistent");
    }

    @Test
    void loadUserById_deveCarregarUsuarioPorId() {
        // Given
        User user = User.builder()
                .id(3L)
                .username("iduser")
                .email("id@example.com")
                .password("hashedPassword")
                .active(true)
                .roles(Set.of("USER", "ADMIN"))
                .build();
        
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserById(3L);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("iduser");
        assertThat(userDetails.getAuthorities()).hasSize(2);
        verify(userRepository).findById(3L);
    }

    @Test
    void loadUserById_deveLancarExcecaoQuandoUsuarioNaoEncontradoPorId() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserById(999L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("not found")
                .hasMessageContaining("999");
    }

    @Test
    void loadUserByUsername_deveConverterUsernameParaMinusculasNaMensagemDeErro() {
        // Given
        when(userRepository.findByUsername("TestUser"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("TestUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("username");
    }
}
