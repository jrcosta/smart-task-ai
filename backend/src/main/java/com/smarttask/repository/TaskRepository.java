package com.smarttask.repository;

import com.smarttask.model.Task;
import com.smarttask.model.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA responsável por persistir e consultar tarefas e suas
 * variações de status.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUserId(Long userId);
    
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    
    List<Task> findByUserIdAndParentTaskIsNull(Long userId);
    
    List<Task> findByParentTaskId(Long parentTaskId);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate BETWEEN :start AND :end")
    List<Task> findByUserIdAndDueDateBetween(
        @Param("userId") Long userId, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status != 'COMPLETED' AND t.dueDate < :now")
    List<Task> findOverdueTasks(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);
}
