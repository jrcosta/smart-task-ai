package com.smarttask.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.smarttask.model.Task;
import com.smarttask.model.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA responsavel por persistir e consultar tarefas e suas
 * variacoes de status.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
    * Lista todas as tarefas pertencentes a um usuario.
     *
    * @param userId identificador do usuario
     * @return lista de tarefas associadas
     */
    List<Task> findByUserId(Long userId);

    /**
    * Lista tarefas de um usuario filtrando por status.
     *
    * @param userId identificador do usuario
     * @param status status desejado
     * @return tarefas que atendem ao filtro
     */
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);

    /**
    * Lista tarefas raiz (sem pai) de um usuario.
     *
    * @param userId identificador do usuario
    * @return tarefas principais do usuario
     */
    List<Task> findByUserIdAndParentTaskIsNull(Long userId);

    /**
    * Recupera as subtarefas relacionadas a uma tarefa pai.
     *
     * @param parentTaskId identificador da tarefa pai
     * @return subtarefas vinculadas
     */
    List<Task> findByParentTaskId(Long parentTaskId);

    /**
     * Busca tarefas com vencimento dentro de um intervalo.
     *
     * @param userId identificador do usuario
     * @param start inicio do intervalo
     * @param end fim do intervalo
     * @return tarefas com vencimento no periodo
     */
    @Query(
            "SELECT t FROM Task t WHERE t.user.id = :userId "
            + "AND t.dueDate BETWEEN :start AND :end")
    List<Task> findByUserIdAndDueDateBetween(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * Localiza tarefas atrasadas que ainda nao foram concluidas.
     *
     * @param userId identificador do usuario
     * @param now data de referencia para atraso
     * @return tarefas atrasadas e nao concluidas
     */
    @Query(
        "SELECT t FROM Task t WHERE t.user.id = :userId "
        + "AND t.status != 'COMPLETED' AND t.dueDate < :now")
    List<Task> findOverdueTasks(
        @Param("userId") Long userId,
        @Param("now") LocalDateTime now);

    /**
     * Conta tarefas de um usuario agrupadas por status.
     *
     * @param userId identificador do usuario
     * @param status status que sera contado
     * @return quantidade de tarefas no status informado
     */
    @Query(
        "SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId "
        + "AND t.status = :status")
    Long countByUserIdAndStatus(
        @Param("userId") Long userId,
        @Param("status") TaskStatus status);
}
