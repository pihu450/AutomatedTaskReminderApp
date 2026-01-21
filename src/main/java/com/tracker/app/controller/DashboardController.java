package com.tracker.app.controller;

import com.tracker.app.entity.Task;
import com.tracker.app.enums.TaskPriority;
import com.tracker.app.enums.TaskStatus;
import com.tracker.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private TaskService taskService;

    // ================= DASHBOARD PAGE =================
    @GetMapping
    public String dashboardPage(HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        String name = (String) session.getAttribute("name");

        if (userId == null) {
            return "redirect:/login";
        }


        List<Task> recentTasks = taskService.getRecentTasks(userId);

        long overdue = recentTasks.stream().filter(t -> t.getDueDate() != null &&
                t.getDueDate().isBefore(LocalDate.now()) &&
                t.getStatus() != TaskStatus.DONE).count();
        long dueToday = recentTasks.stream().filter(t -> t.getDueDate() != null &&
                t.getDueDate().isEqual(LocalDate.now())).count();

        List<Task> recent = recentTasks.stream().sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).limit(5).collect(Collectors.toList());


        model.addAttribute("totalTasks", taskService.countByUser_Id(userId));
        model.addAttribute("completedTasks", taskService.countCompleted(userId));
        model.addAttribute("pendingTasks", taskService.countPending(userId));
        model.addAttribute("highPriorityTasks", taskService.countByUser_IdAndPriority(userId, TaskPriority.HIGH));
        model.addAttribute("overdue",overdue);
        model.addAttribute("dueToday",dueToday);
        model.addAttribute("recentTasks", recentTasks);
        model.addAttribute("name", name);
        return "dashboard";
    }

    // ================= DASHBOARD STATS API =================
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getStats(HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("User not logged in");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTasks", taskService.countByUser_Id(userId));
        stats.put("completedTasks", taskService.countCompleted(userId));
        stats.put("pendingTasks", taskService.countPending(userId));
        stats.put("overdue", taskService.countOverdue(userId));

        return stats;
    }
}
