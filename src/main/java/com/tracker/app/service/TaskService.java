package com.tracker.app.service;

import com.tracker.app.entity.Task;
import com.tracker.app.entity.User;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.repository.TaskRepository;
import com.tracker.app.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private JavaMailSender mailSender;

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

    public List<Task> getTasksByUser(Integer userId){
        return taskRepository.findByUserId(userId);
    }


    public long countOverdue(Integer userId) {
        return taskRepository.countOverdueTasks(
                userId,
                LocalDateTime.now(),
                TaskStatus.DONE
        );
    }


    public List<Task> getRecentTasks(Integer userId) {
        return taskRepository.findTop5ByUser_IdOrderByCreatedAtDesc(userId);
    }

    public void sendTasksCsvByEmail(List<Task> tasks, Integer userId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String csvData = generateCsv(tasks);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("Your Task List (CSV Export)");
        helper.setText("Please find attached your task list.");

        helper.addAttachment("tasks.csv",
                new ByteArrayResource(csvData.getBytes()));

        mailSender.send(message);
    }

    // ================= CSV GENERATOR =================
    private String generateCsv(List<Task> tasks) {

        StringBuilder sb = new StringBuilder();
        sb.append("ID,Title,Description,Status,Priority,Due Date,Created At,Completed At\n");

        for (Task task : tasks) {
            sb.append(task.getId()).append(",")
                    .append(escape(task.getTitle())).append(",")
                    .append(escape(task.getDescription())).append(",")
                    .append(task.getStatus()).append(",")
                    .append(task.getPriority()).append(",")
                    .append(task.getDueDate()).append(",")
                    .append(task.getCreatedAt()).append(",")
                    .append(task.getCompletedAt()).append("\n");
        }

        return sb.toString();
    }

    private String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }


}

