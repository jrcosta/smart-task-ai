package com.smarttask.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Responsavel por gerar e validar tokens JWT utilizados na autenticacao
 * stateless.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    /** Segredo utilizado na assinatura HMAC dos tokens. */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** Tempo de expiracao do token em milissegundos. */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Constroi a chave criptografica para assinar e validar tokens.
     *
     * @return chave HMAC derivada do segredo configurado
     */
    private SecretKey getSigningKey() {
        final byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gera um token JWT a partir da autenticacao do usuario corrente.
     *
     * @param authentication contexto autenticado retornado pelo Spring Security
     * @return token JWT assinado contendo o identificador do usuario
     */
    public String generateToken(final Authentication authentication) {
    final UserPrincipal userPrincipal =
        (UserPrincipal) authentication.getPrincipal();
        final Date now = new Date();
        final Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Recupera o identificador do usuario presente no token JWT.
     *
     * @param token token JWT previamente emitido pela aplicacao
     * @return identificador numerico do usuario autenticado
     */
    public Long getUserIdFromToken(final String token) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * Valida um token garantindo assinatura e expiracao consistentes.
     *
     * @param authToken token JWT recebido na requisicao
     * @return {@code true} quando o token e valido e ainda nao expirou
     */
    public boolean validateToken(final String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (ExpiredJwtException exception) {
            log.debug("Token JWT expirado: {}", exception.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            log.warn(
                    "Token JWT invalido recebido: {}",
                    exception.getClass().getSimpleName());
        } catch (IllegalArgumentException exception) {
            log.warn("Token JWT vazio ou malformado recebido");
        } catch (JwtException exception) {
            log.warn(
                    "Falha ao validar token JWT: {}",
                    exception.getClass().getSimpleName());
        }
        return false;
    }
}
