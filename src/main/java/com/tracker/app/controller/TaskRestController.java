package com.tracker.app.controller;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskRestController {

    private final TaskService taskService;

    @Autowired
    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    // PAGINATION
    @GetMapping
    public ResponseEntity<Page<Task>> getAll(Pageable pageable) {
        return ResponseEntity.ok(taskService.findAll(pageable));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



    // CREATE
    @PostMapping("/add")
    public ResponseEntity<Task> create(@RequestBody Task task) {
        task.setCreatedAt(LocalDateTime.now());
        Task saved = taskService.addTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id,
                                       @RequestBody Task task) {

        Optional<Task> existing = taskService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        task.setId(id);
        task.setCreatedAt(existing.get().getCreatedAt());

        Task updated = taskService.updateTask(task);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }


    // FILTER BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(
                taskService.findByStatus(status)
        );
    }


    // FILTER BY PRIORITY
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getByPriority(@PathVariable TaskPriority priority) {
        return ResponseEntity.ok(
                taskService.findByPriority(priority)
        );
    }


    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchByTitle(@RequestParam String keyword) {
        return ResponseEntity.ok(taskService.searchByTitle(keyword));
    }

    // FILTER BY DUE DATE
    @GetMapping("/due")
    public ResponseEntity<List<Task>> getByDueDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(taskService.findByDueDate(date));
    }
}
