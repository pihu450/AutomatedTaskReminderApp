package com.tracker.app.controller;

import com.tracker.app.dto.OtpRequest;
import com.tracker.app.entity.User;
import com.tracker.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ================= REGISTER =================

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirect) {
        try {
            userService.register(user);
            redirect.addFlashAttribute("success", "Account created. Please verify OTP!");
            return "redirect:/verify-otp";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register";
        }
    }

    // ================= OTP =================

    @GetMapping("/verify-otp")
    public String showOtpPage(Model model) {
        model.addAttribute("otpRequest", new OtpRequest());
        return "verify-otp";
    }

    @PostMapping("/verify")
    public String verify(@ModelAttribute OtpRequest request,
                         RedirectAttributes redirect) {
        try {
            String response = userService.verifyOtp(
                    request.getEmail(),
                    request.getOtp()
            );
            redirect.addFlashAttribute("success", response + " Please login.");
            return "redirect:/login";
        } catch (Exception ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/verify-otp";
        }
    }

    // ================= LOGIN =================

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            userService.loginUser(email, password, session);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }

        model.addAttribute("email", session.getAttribute("email"));
        return "dashboard";
    }


    // ================= SESSION =================

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }

        return ResponseEntity.ok(
                Map.of(
                        "loggedIn", true,
                        "id", userId,
                        "email", session.getAttribute("email")
                )
        );
    }

    // ================= LOGOUT =================

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
