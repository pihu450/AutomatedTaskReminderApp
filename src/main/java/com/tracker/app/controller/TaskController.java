package com.tracker.app.controller;

import com.tracker.app.entity.Task;
import com.tracker.app.entity.User;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.repository.UserRepository;
import com.tracker.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserRepository userRepository;

    // ===================== LIST TASKS =====================
    @GetMapping("/tasks")
    public String listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) LocalDate dueDate,
            Model model,
            HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/index";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage =
                taskService.filterTasks(userId, keyword, status, priority, dueDate, pageable);

        model.addAttribute("taskPage", taskPage);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", taskPage.getTotalPages());
        model.addAttribute("size", size);

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        model.addAttribute("dueDate", dueDate);

        return "tasks";
    }

    // ===================== VIEW TASK =====================
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Long id,
                           Model model,
                           RedirectAttributes ra,
                           HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Task> taskOpt = taskService.findById(id);

        if (taskOpt.isEmpty() || !taskOpt.get().getUser().getId().equals(userId)) {
            ra.addFlashAttribute("errorMessage", "Unauthorized access");
            return "redirect:/api/tasks/tasks";
        }

        model.addAttribute("task", taskOpt.get());
        return "view-task";
    }

    // ===================== ADD FORM =====================
    @GetMapping("/add")
    public String showAddForm(Model model, HttpSession session) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        model.addAttribute("task", new Task());
        return "add-task";
    }

    // ===================== SAVE TASK =====================
    @PostMapping("/add")
    public String saveTask(@ModelAttribute Task task,
                           Model model,
                           RedirectAttributes ra,
                           HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Title is required");
            model.addAttribute("task", task);
            return "add-task";
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());
        task.setStatus(TaskStatus.PENDING);

        taskService.saveTask(task);

        ra.addFlashAttribute("successMessage", "Task added successfully");
        return "redirect:/api/tasks/tasks";
    }

    // ===================== EDIT FORM =====================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes ra,
                               HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Task> taskOpt = taskService.findById(id);

        if (taskOpt.isEmpty() || !taskOpt.get().getUser().getId().equals(userId)) {
            ra.addFlashAttribute("errorMessage", "Unauthorized access");
            return "redirect:/api/tasks/tasks";
        }

        model.addAttribute("task", taskOpt.get());
        return "edit-task";
    }

    // ===================== UPDATE TASK =====================
    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute Task formTask,
                             RedirectAttributes ra,
                             HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Task existingTask = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!existingTask.getUser().getId().equals(userId)) {
            ra.addFlashAttribute("errorMessage", "Unauthorized access");
            return "redirect:/api/tasks/tasks";
        }

        // ✅ update ONLY allowed fields
        existingTask.setTitle(formTask.getTitle());
        existingTask.setDescription(formTask.getDescription());
        existingTask.setDueDate(formTask.getDueDate());
        existingTask.setPriority(formTask.getPriority());
        existingTask.setStatus(formTask.getStatus());

        taskService.saveTask(existingTask);

        ra.addFlashAttribute("successMessage", "Task updated successfully");
        return "redirect:/api/tasks/tasks";
    }

    // ===================== MARK AS DONE =====================
    @GetMapping("/markdone/{id}")
    public String markAsDone(@PathVariable Long id, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Task> taskOpt = taskService.findById(id);

        if (taskOpt.isPresent() &&
                taskOpt.get().getUser().getId().equals(userId)) {

            Task task = taskOpt.get();
            task.setStatus(TaskStatus.DONE);
            task.setCompletedAt(LocalDateTime.now());
            taskService.saveTask(task);
        }


        return "redirect:/api/tasks/tasks";
    }

    // ===================== DELETE TASK =====================
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id,
                             RedirectAttributes ra,
                             HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Task> taskOpt = taskService.findById(id);

        if (taskOpt.isEmpty() ||
                !taskOpt.get().getUser().getId().equals(userId)) {

            ra.addFlashAttribute("errorMessage", "Unauthorized access");
        } else {
            taskService.deleteTask(id);
            ra.addFlashAttribute("successMessage", "Task deleted successfully");
        }


        return "redirect:/api/tasks/tasks";
    }
}
