package com.tracker.app.service;

import com.tracker.app.entity.Task;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendOTP(String toEmail, String otp) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Automatic task Reminder & Tracking System - Email Verification");
            message.setText(
                    "Your OTP for email verification is: " + otp +
                            "\n\nPlease do not share this OTP with anyone."
            );

            Transport.send(message);
            System.out.println("OTP sent to " + toEmail);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP", e);
        }
    }

    public void sendTaskReminder(Task task){
        String to = task.getUser().getEmail();
        String subject = "Task Reminder : "+ task.getTitle();
        String body = "Hi "+ task.getUser().getName() + ",\n\n" +
                "Your task \"" +task.getTitle() + "\" is due at " +
                task.getDueDate() + ".\nPlease complete it on time.\n\n" +
                "Task Reminder App";
    }
}
