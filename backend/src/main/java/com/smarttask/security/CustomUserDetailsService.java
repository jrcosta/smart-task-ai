package com.smarttask.security;

import java.util.Locale;
import java.util.Optional;

import com.smarttask.model.User;
import com.smarttask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacao de {@link UserDetailsService} que carrega usuarios do banco de
 * dados para autenticacao baseada em JWT.
 */
@Service
@RequiredArgsConstructor
public final class CustomUserDetailsService implements UserDetailsService {

    /** Formato base para mensagens de usuario nao encontrado. */
    private static final String USER_NOT_FOUND_MESSAGE =
            "User not found with %s: %s";

    /** Repositorio utilizado para localizar usuarios ativos. */
    private final UserRepository userRepository;

    /**
     * Carrega um usuario pelo username para autenticacao JWT.
     *
     * @param username identificador textual unico
     * @return detalhes de autenticacao do usuario
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> buildNotFound("username", username));
        return UserPrincipal.create(user);
    }

    /**
     * Carrega um usuario pelo identificador numerico.
     *
     * @param id identificador da entidade persistida
     * @return detalhes de autenticacao do usuario
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(final Long id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> buildNotFound("id", String.valueOf(id)));
        return UserPrincipal.create(user);
    }

    private UsernameNotFoundException buildNotFound(
            final String field,
            final String value) {
        final String formattedField = Optional.ofNullable(field)
                .map(item -> item.toLowerCase(Locale.ROOT))
                .orElse("field");
        final String message = String.format(
                USER_NOT_FOUND_MESSAGE,
                formattedField,
                value);
        return new UsernameNotFoundException(message);
    }
}
