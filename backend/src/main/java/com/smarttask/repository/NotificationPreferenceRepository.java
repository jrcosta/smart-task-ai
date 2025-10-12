package com.smarttask.repository;

import com.smarttask.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório dedicado ao gerenciamento das preferências de notificação dos usuários.
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    
    Optional<NotificationPreference> findByUserId(Long userId);
    
    @Query("SELECT np FROM NotificationPreference np WHERE np.enabled = true AND np.dailyReminderTime = :time")
    List<NotificationPreference> findByDailyReminderTime(LocalTime time);
    
    @Query("SELECT np FROM NotificationPreference np WHERE np.enabled = true")
    List<NotificationPreference> findAllEnabled();
}
