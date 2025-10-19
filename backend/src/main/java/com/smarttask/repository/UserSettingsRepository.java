package com.smarttask.repository;

import com.smarttask.model.UserSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de persistência de {@link UserSettings}.
 */
@Repository
public interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {
    
    /**
     * Busca as configurações de um usuário específico.
     * 
     * @param userId ID do usuário
     * @return Optional contendo as configurações se encontradas
     */
    Optional<UserSettings> findByUserId(Long userId);
    
    /**
     * Verifica se existem configurações para um usuário.
     * 
     * @param userId ID do usuário
     * @return true se existem configurações, false caso contrário
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Remove as configurações de um usuário.
     * 
     * @param userId ID do usuário
     */
    void deleteByUserId(Long userId);
}
