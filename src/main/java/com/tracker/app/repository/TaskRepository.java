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
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ================= FILTER TASKS (USER-SPECIFIC) =================
    @Query("""
        SELECT t FROM Task t
        WHERE t.userId = :userId
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
    long countByUserId(Integer userId);

    long countByUserIdAndStatus(Integer userId, TaskStatus status);

    long countByUserIdAndPriority(Integer userId, TaskPriority priority);

    // ================= RECENT TASKS =================
    List<Task> findTop5ByUserIdOrderByCreatedAtDesc(Integer userId);
}
