package com.tracker.app.controller;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/api/tasks/")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // ✅ LIST TASKS
    @GetMapping("/tasks")
    public String listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) LocalDate dueDate,
            Model model, HttpSession session) {
        if(session.getAttribute("userId")==null){
            return "redirect:/login";
        }
        List<Task> tasks=null;
        Pageable pageable = PageRequest.of(page, size);

        Page<Task> taskPage = taskService.filterTasks(keyword, status, priority,dueDate, pageable);

        model.addAttribute("taskPage", taskPage);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage",taskPage.getTotalPages());
        model.addAttribute("size",size);
        int totalPages= taskPage.getTotalPages();
        List<Integer> pageNumbers = IntStream.range(0,totalPages).boxed().toList();
        // keep values selected in UI
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        model.addAttribute("dueDate", dueDate);


        return "tasks";
    }

    // ✅ VIEW TASK
    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Long id,
                           Model model,
                           RedirectAttributes ra) {

        Optional<Task> taskOpt = taskService.findById(id);

        if (taskOpt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Task not found");
            return "redirect:/api/tasks/tasks";
        }

        model.addAttribute("task", taskOpt.get());
        return "view-task";
    }

    // ✅ ADD FORM
    @GetMapping("/add")
    public String showAddForm(Model model,HttpSession session) {
        model.addAttribute("task", new Task());
        return "add-task";
    }

    // ✅ SAVE TASK
    @PostMapping("/add")
    public String saveTask(@ModelAttribute Task task,Model model,
                           RedirectAttributes ra,HttpSession session) {
        if(session.getAttribute("userId")==null){
            return "redirect:/login";
        }

        if(task.getTitle() == null || task.getTitle().trim().isEmpty()){
            model.addAttribute("errorMessage","Title is required");
            model.addAttribute("task",task);
            return "add-task";
        }
        task.setCreatedAt(LocalDateTime.now());
        taskService.saveTask(task);

        ra.addFlashAttribute("successMessage", "Task added successfully");
        return "redirect:/api/tasks/tasks";
    }

    // ✅ EDIT FORM
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes ra) {

        Optional<Task> taskOpt = taskService.findById(id);

        if (taskOpt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Task not found");
            return "redirect:/api/tasks/tasks";
        }

        model.addAttribute("task", taskOpt.get());
        return "edit-task";
    }

    // ✅ UPDATE TASK
    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute Task task,
                             RedirectAttributes ra) {

        task.setId(id);
        taskService.saveTask(task);

        ra.addFlashAttribute("successMessage", "Task updated successfully");
        return "redirect:/api/tasks/tasks";
    }

    @GetMapping("/markdone/{id}")
    public String markAsDone(@PathVariable Long id){
        Task task = taskService.findById(id).orElse(null);
        if(task != null && task.getStatus() != TaskStatus.DONE){
            task.setStatus(TaskStatus.DONE);
            task.setCompletedAt(LocalDateTime.now());
            taskService.saveTask(task);
        }
        return "redirect:/api/tasks/tasks";

    }
    // ✅ DELETE TASK
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id,
                             RedirectAttributes ra) {

        if (taskService.findById(id).isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Task not found");
        } else {
            taskService.deleteTask(id);
            ra.addFlashAttribute("successMessage", "Task deleted successfully");
        }

        return "redirect:/api/tasks/tasks";
    }
}
