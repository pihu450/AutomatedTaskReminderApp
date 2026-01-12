package com.tracker.app.service;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // ================= EXISTING METHODS =================

    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> filterTasks( Integer userId,
                                   String keyword,
                                    TaskStatus status,
                                    TaskPriority priority,
                                     LocalDate dueDate,
                                   Pageable pageable) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        return taskRepository.filterTasks(userId,keyword, status, priority, dueDate, pageable);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    // ================= DASHBOARD METHODS (ADD THESE) =================

    public long countByUser(Integer userId) {
        return taskRepository.countByUserId(userId);
    }

    public long countCompleted(Integer userId) {
        return taskRepository.countByUserIdAndStatus(userId, TaskStatus.DONE);
    }

    public long countPending(Integer userId) {
        return taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);
    }

    public long countHighPriority(Integer userId) {
        return taskRepository.countByUserIdAndPriority(userId, TaskPriority.HIGH);
    }

    public List<Task> getRecentTasks(Integer userId) {
        return taskRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
    }
}
