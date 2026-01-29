package com.tracker.app.repository;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ================= FILTER TASKS =================
    @Query("""
        SELECT t FROM Task t
        WHERE t.user.id = :userId
          AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:status IS NULL OR t.status = :status)
          AND (:priority IS NULL OR t.priority = :priority)
          AND (:dueDate IS NULL OR t.dueDate = :dueDate)
    """)
    Page<Task> filterTasks(
            @Param("userId") Integer userId,
            @Param("keyword") String keyword,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("dueDate") LocalDate dueDate,
            Pageable pageable
    );

    // ================= DASHBOARD COUNTS =================
    long countByUser_Id(Integer userId);

    long countByUser_IdAndStatus(Integer userId, TaskStatus status);

    long countByUser_IdAndPriority(Integer userId, TaskPriority priority);

    @Query("""
SELECT COUNT(t)
FROM Task t
WHERE t.user.id = :userId
AND t.dueDate < :now
AND t.status <> :doneStatus
""")
    long countOverdueTasks(
            @Param("userId") Integer userId,
            @Param("now") LocalDateTime now,
            @Param("doneStatus") TaskStatus doneStatus
    );


    // ================= RECENT TASKS =================
    List<Task> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);

    List<Task> findByUserId(Integer userId);
    @Query("""
SELECT t FROM Task t
JOIN FETCH t.user
WHERE t.dueDate BETWEEN :now AND :next30Min
AND t.reminderSent = false
""")
    List<Task> findTasksForReminder(
            @Param("now") LocalDateTime now,
            @Param("next30Min") LocalDateTime next30Min
    );

    @Query("""
        SELECT t FROM Task t
        JOIN FETCH t.user
        WHERE t.dueDate < :now
          AND t.status <> 'DONE'
          AND t.missedNotified = false
    """)
    List<Task> findMissedTasks(
            @Param("now") LocalDateTime now
    );
}
