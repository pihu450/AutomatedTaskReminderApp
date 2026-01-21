package com.tracker.app.service;

import com.tracker.app.entity.Task;
import com.tracker.app.entity.User;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.repository.TaskRepository;
import com.tracker.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Autowired
    private UserRepository userRepository;
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

    public Task addTask(Task task, Integer userId){
        Optional<User> user = userRepository.findById(userId);
        task.setUser(user.get());
        if(task.getStatus()== null){
            task.setStatus(TaskStatus.PENDING);
        }
        task.setCreatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }
    // ================= DASHBOARD METHODS (ADD THESE) =================

    public long countByUser_Id(Integer userId) {
        return taskRepository.countByUser_Id(userId);
    }

    public long countCompleted(Integer userId) {
        return taskRepository.countByUser_IdAndStatus(userId, TaskStatus.DONE);
    }

    public long countPending(Integer userId) {
        return taskRepository.countByUser_IdAndStatus(userId, TaskStatus.PENDING);
    }
    public long countByUser_IdAndPriority(Integer userId, TaskPriority priority) {
        return taskRepository.countByUser_IdAndPriority(userId, priority);
    }


    public long countOverdue(Integer userId) {
        return taskRepository.countOverdueTasks(
                userId,
                LocalDate.now(),
                TaskStatus.DONE
        );
    }


    public List<Task> getRecentTasks(Integer userId) {
        return taskRepository.findTop5ByUser_IdOrderByCreatedAtDesc(userId);
    }
}
