package com.smarttask.config;

import com.smarttask.security.CustomUserDetailsService;
import com.smarttask.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configura seguranca HTTP, autenticacao e CORS da aplicacao com JWT sem
 * estado, integrando-se ao {@link CustomUserDetailsService} para carregar
 * credenciais.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public final class SecurityConfig {

    /** Servico responsavel por carregar usuarios autenticaveis. */
    private final CustomUserDetailsService userDetailsService;

    /** Filtro que valida tokens JWT em cada requisicao. */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configura o codificador de senhas padrao (BCrypt).
     *
     * @return implementacao de {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura o provedor de autenticacao baseado em banco de dados.
     *
     * @return provedor com servico de usuarios e encoder registrados
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Expoe o gerenciador de autenticacao do Spring Security.
     *
     * @param authConfig configuracao compartilhada de autenticacao
     * @return instancia configurada de {@link AuthenticationManager}
     * @throws Exception quando a resolucao do gerenciador falha
     */
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Define a cadeia de filtros HTTP com JWT sem estado e CORS permissivo.
     *
     * @param http construtor de configuracoes HTTP
     * @return cadeia de filtros configurada
     * @throws Exception quando a construcao falha
     */
    @Bean
    public SecurityFilterChain filterChain(
            final HttpSecurity http)
            throws Exception {
        http.csrf(configurer -> configurer.disable());
        http.cors(configurer ->
                configurer.configurationSource(
                        corsConfigurationSource()));
        http.sessionManagement(configurer ->
                configurer.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(registry ->
                registry.requestMatchers(
                        "/auth/**",
                        "/h2-console/**",
                        "/actuator/**")
                .permitAll()
                .anyRequest()
                .authenticated());
        http.authenticationProvider(
                authenticationProvider());
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);
        http.headers(headers ->
                headers.frameOptions(frame ->
                        frame.sameOrigin()));

        return http.build();
    }

    /**
     * Configura o CORS para liberar o acesso do frontend local.
     *
     * @return fonte de configuracoes CORS registrada
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173"));
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
