package com.smarttask.repository;

import java.util.Optional;

import com.smarttask.model.UserSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operacoes de persistencia de {@link UserSettings}.
 */
@Repository
public interface UserSettingsRepository
    extends CrudRepository<UserSettings, Long> {

    /**
     * Busca as configuracoes de um usuario especifico.
     *
     * @param userId identificador do usuario
     * @return configuracoes encapsuladas em {@link Optional}
     */
    Optional<UserSettings> findByUserId(Long userId);

    /**
     * Verifica se existem configuracoes para um usuario.
     *
     * @param userId identificador do usuario
     * @return {@code true} quando ha configuracoes cadastradas
     */
    boolean existsByUserId(Long userId);

    /**
     * Remove as configuracoes de um usuario.
     *
     * @param userId identificador do usuario
     */
    void deleteByUserId(Long userId);
}
