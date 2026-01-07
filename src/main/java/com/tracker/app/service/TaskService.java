package com.tracker.app.service;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    // PAGINATION
    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }
    public Page<Task> filterTasks(String keyword, TaskStatus status,TaskPriority priority, LocalDate dueDate,Pageable pageable) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        return taskRepository.filterTasks(keyword, status, priority,dueDate, pageable);
    }


    // SAVE / UPDATE TASK
    public Task saveTask(Task task) {
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


}
