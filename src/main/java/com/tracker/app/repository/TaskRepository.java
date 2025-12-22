package com.tracker.app.repository;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByTitleContainingIgnoreCase(String keyword);

    List<Task> findByDueDate(LocalDate dueDate);
}

