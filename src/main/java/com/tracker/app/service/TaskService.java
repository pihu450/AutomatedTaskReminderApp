package com.tracker.app.service;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // GET ALL (without pagination – used for UI)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // PAGINATION SUPPORT
    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    // ADD TASK
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    // UPDATE TASK
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    // DELETE TASK
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // FIND BY ID
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    // FILTER BY STATUS
    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    // FILTER BY PRIORITY
    public List<Task> findByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    // SEARCH BY TITLE
    public List<Task> searchByTitle(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // FILTER BY DUE DATE
    public List<Task> findByDueDate(LocalDate date) {
        return taskRepository.findByDueDate(date);
    }
}
