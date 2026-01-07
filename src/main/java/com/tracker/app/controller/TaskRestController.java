package com.tracker.app.controller;

import com.tracker.app.dto.OtpRequest;
import com.tracker.app.entity.Task;
import com.tracker.app.entity.User;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.service.TaskService;
import com.tracker.app.service.UserService;
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
    private final UserService userService;
    @Autowired
    public TaskRestController(TaskService taskService,UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // ✅ GET ALL + FILTER + PAGINATION
    @GetMapping
    public ResponseEntity<Page<Task>> getTasks(
            Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {

        Page<Task> taskPage = taskService.filterTasks(
                keyword,
                status,
                priority,
                dueDate,
                pageable
        );

        return ResponseEntity.ok(taskPage);
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<Task> create(@RequestBody Task task) {
        task.setCreatedAt(LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.saveTask(task));
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Task> update(
            @PathVariable Long id,
            @RequestBody Task task) {

        Optional<Task> existing = taskService.findById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        task.setId(id);
        task.setCreatedAt(existing.get().getCreatedAt());

        return ResponseEntity.ok(taskService.saveTask(task));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        if (taskService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/userRegister")
    public ResponseEntity<?> registerApi(@RequestBody User user){
        try{
            userService.register(user);
            return ResponseEntity.ok("OTP sent to your email");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody OtpRequest request){
        String response = userService.verifyOtp(request.getEmail(),request.getOtp());
        return ResponseEntity.ok(response);
    }
}
