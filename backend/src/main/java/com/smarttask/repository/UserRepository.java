package com.smarttask.repository;

import java.util.Optional;

import com.smarttask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provê operações de acesso a dados para entidades de usuário.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo username informado.
     *
     * @param username identificador textual único
     * @return usuário correspondente, caso exista
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca um usuário pelo e-mail cadastrado.
     *
     * @param email endereço de e-mail
     * @return usuário correspondente, caso exista
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se já existe usuário com o username informado.
     *
     * @param username identificador textual único
     * @return {@code true} quando o username já está em uso
     */
    Boolean existsByUsername(String username);

    /**
     * Verifica se já existe usuário com o e-mail informado.
     *
     * @param email endereço de e-mail
     * @return {@code true} quando o e-mail já está em uso
     */
    Boolean existsByEmail(String email);
}
