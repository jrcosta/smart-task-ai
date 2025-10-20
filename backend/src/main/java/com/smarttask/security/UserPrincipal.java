package com.smarttask.security;

import com.smarttask.model.User;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementacao de {@link UserDetails} que encapsula dados essenciais do
 * usuario para o contexto de seguranca da aplicacao.
 */
@Data
@AllArgsConstructor
public final class UserPrincipal implements UserDetails {

    /** Identificador unico do usuario. */
    private Long id;

    /** Nome de usuario utilizado na autenticacao. */
    private String username;

    /** E-mail principal associado ao usuario. */
    private String email;

    /** Senha criptografada do usuario. */
    private String password;

    /** Indica se o usuario esta habilitado para autenticacao. */
    private boolean active;

    /** Autoridades atribuidas ao usuario (roles). */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Constroi um {@link UserPrincipal} a partir da entidade {@link User}.
     *
     * @param user entidade de usuario
     * @return representacao utilizada pelo Spring Security
     */
    public static UserPrincipal create(final User user) {
        final Collection<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Boolean.TRUE.equals(user.getActive()),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
