package com.tracker.app.service;

import com.tracker.app.entity.Task;
import com.tracker.app.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskReminderScheduler {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    @Scheduled(fixedRate = 30000)

    public void sendTaskReminders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next30Minutes = now.plusMinutes(30);

        List<Task> tasks =
                taskRepository.findTasksForReminder(now, next30Minutes);

        for (Task task : tasks) {
            emailService.sendTaskReminder(task);
            task.setReminderSent(true);
            taskRepository.save(task);
        }
    }
    @Scheduled(fixedRate = 60000)
    public void sendMissedTaskNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> missedTasks = taskRepository.findMissedTasks(now);

        for (Task task : missedTasks) {
            emailService.sendMissedTaskEmail(task);
            task.setMissedNotified(true);
            taskRepository.save(task);
        }
    }

}
