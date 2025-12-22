package com.tracker.app.controller;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // LIST TASKS
    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks";
    }

    // VIEW TASK
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Task task = taskService.findById(id).orElse(null);
        if (task == null) {
            ra.addFlashAttribute("errorMessage", "Task not found");
            return "redirect:/api/tasks/tasks";
        }
        model.addAttribute("task", task);
        return "view-task";
    }

    // ADD FORM
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("task", new Task());
        return "add-task";
    }
}
