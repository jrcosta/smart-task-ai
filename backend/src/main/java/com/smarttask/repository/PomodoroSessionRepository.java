package com.smarttask.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.smarttask.model.PomodoroSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para monitorar sessoes de pomodoro vinculadas aos usuarios.
 */
@Repository
public interface PomodoroSessionRepository
        extends JpaRepository<PomodoroSession, Long> {

    /**
     * Recupera todas as sessoes registradas por um usuario especifico.
     *
     * @param userId identificador do usuario
     * @return lista de sessoes relacionadas ao usuario
     */
    List<PomodoroSession> findByUserId(Long userId);

    /**
     * Recupera as sessoes associadas a uma tarefa.
     *
     * @param taskId identificador da tarefa
     * @return lista de sessoes vinculadas a tarefa
     */
    List<PomodoroSession> findByTaskId(Long taskId);

    /**
     * Busca sessoes de um usuario dentro de um intervalo de datas.
     *
     * @param userId identificador do usuario
     * @param start data inicial do intervalo
     * @param end data final do intervalo
     * @return sessoes executadas no periodo informado
     */
    @Query(
            "SELECT p FROM PomodoroSession p WHERE p.user.id = :userId "
            + "AND p.createdAt BETWEEN :start AND :end")
    List<PomodoroSession> findByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Conta a quantidade de sessoes concluidas por um usuario.
     *
     * @param userId identificador do usuario
     * @return total de sessoes finalizadas
     */
    @Query(
            "SELECT COUNT(p) FROM PomodoroSession p WHERE p.user.id = :userId "
            + "AND p.completed = true")
    Long countCompletedSessionsByUserId(@Param("userId") Long userId);
}
