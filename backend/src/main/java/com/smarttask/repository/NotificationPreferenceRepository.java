package com.smarttask.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.smarttask.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositorio dedicado ao gerenciamento das preferencias de notificacao dos
 * usuarios.
 */
@Repository
public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, Long> {

    /**
     * Localiza a configuracao de notificacao pelo identificador do usuario.
     *
     * @param userId identificador do usuario
     * @return preferencia configurada, caso exista
     */
    Optional<NotificationPreference> findByUserId(Long userId);

        /**
         * Busca registros que possuem lembrete diario habilitado para o horario
         * informado.
     *
     * @param time horario alvo a ser consultado
     * @return lista com preferencias prontas para disparo no horario
     */
    @Query(
            "SELECT np FROM NotificationPreference np "
            + "WHERE np.enabled = true AND np.dailyReminderTime = :time")
    List<NotificationPreference> findByDailyReminderTime(LocalTime time);

    /**
     * Recupera todas as configuracoes atualmente habilitadas.
     *
     * @return lista de preferencias com envio ativo
     */
    @Query("SELECT np FROM NotificationPreference np WHERE np.enabled = true")
    List<NotificationPreference> findAllEnabled();
}
