package com.smarttask.repository;

import com.smarttask.model.PomodoroSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para monitorar sessões de pomodoro vinculadas aos usuários.
 */
@Repository
public interface PomodoroSessionRepository extends JpaRepository<PomodoroSession, Long> {
    
    List<PomodoroSession> findByUserId(Long userId);
    
    List<PomodoroSession> findByTaskId(Long taskId);
    
    @Query("SELECT p FROM PomodoroSession p WHERE p.user.id = :userId AND p.createdAt BETWEEN :start AND :end")
    List<PomodoroSession> findByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT COUNT(p) FROM PomodoroSession p WHERE p.user.id = :userId AND p.completed = true")
    Long countCompletedSessionsByUserId(@Param("userId") Long userId);
}
