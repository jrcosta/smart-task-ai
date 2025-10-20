package com.smarttask.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro que intercepta requisicoes HTTP para validar tokens JWT e popular o
 * contexto de seguranca com o usuario autenticado.
 */
@Component
@RequiredArgsConstructor
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Numero de caracteres do prefixo "Bearer ". */
    private static final int BEARER_PREFIX_LENGTH = 7;

    /** Provedor responsavel por validar e extrair dados do token JWT. */
    private final JwtTokenProvider tokenProvider;

    /** Servico que carrega detalhes do usuario autenticado. */
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                final Long userId = tokenProvider.getUserIdFromToken(jwt);
        final UserDetails userDetails = customUserDetailsService
            .loadUserById(userId);

                final UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
        authentication.setDetails(
            new WebAuthenticationDetailsSource()
                .buildDetails(request));

        SecurityContextHolder.getContext()
            .setAuthentication(authentication);
            }
        } catch (Exception exception) {
        logger.error(
            "Falha ao definir autenticacao do usuario no contexto "
                + "de seguranca",
            exception);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(final HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(BEARER_PREFIX_LENGTH);
        }
        return null;
    }
}
