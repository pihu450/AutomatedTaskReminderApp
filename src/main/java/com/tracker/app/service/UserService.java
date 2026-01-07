package com.tracker.app.service;

import com.tracker.app.entity.User;
import com.tracker.app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final EmailService emailService;
    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }


    public User register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        user.setVerified(false);

        int otp = 100000 + (int)(Math.random() * 900000);
        user.setOtp(String.valueOf(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        emailService.sendOTP(user.getEmail(), user.getOtp());

        return userRepository.save(user);
    }

    public String verifyOtp(String email, String otp) {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return "User not found";
        }

        if (user.get().isVerified()) {
            return "User already verified";
        }

        if (user.get().getOtp() == null) {
            return "Otp not generated";
        }

        if (!user.get().getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        if (user.get().getOtpExpiry().isBefore(LocalDateTime.now())) {
            return "OTP Expired";
        }

        user.get().setVerified(true);
        user.get().setOtp(null);
        user.get().setOtpExpiry(null);

        userRepository.save(user.get());

        return "OTP verified successfully!";
    }

    public void loginUser(String email, String password, HttpSession session) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("User is not verified. Please verify OTP first.");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("email", user.getEmail());
    }
}

