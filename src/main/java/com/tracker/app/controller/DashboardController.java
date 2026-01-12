package com.tracker.app.controller;

import com.tracker.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final TaskService taskService;

    @Autowired
    public DashboardController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login";
        }

        // 📊 Dashboard metrics
        model.addAttribute("totalTasks", taskService.countByUser(userId));
        model.addAttribute("completedTasks", taskService.countCompleted(userId));
        model.addAttribute("pendingTasks", taskService.countPending(userId));
        model.addAttribute("highPriorityTasks", taskService.countHighPriority(userId));
        model.addAttribute("recentTasks", taskService.getRecentTasks(userId));

        return "dashboard";
    }
}
