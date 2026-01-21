package com.tracker.app.controller;

import com.tracker.app.dto.UpdateProfileRequest;
import com.tracker.app.entity.User;
import com.tracker.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    // ================= VIEW PROFILE =================
    @GetMapping("/profile")
    public String viewProfile(Model model, HttpSession session) {

        Object idObj = session.getAttribute("userId");
        if (idObj == null) {
            return "redirect:/login";
        }

        Integer userId = Integer.valueOf(idObj.toString());
        User user = userService.getUserById(userId);

        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    // ================= EDIT PROFILE PAGE =================
    @GetMapping("/profile/edit")
    public String editProfile(Model model, HttpSession session) {

        Object idObj = session.getAttribute("userId");
        if (idObj == null) {
            return "redirect:/login";
        }

        Integer userId = Integer.valueOf(idObj.toString());
        User user = userService.getUserById(userId);

        UpdateProfileRequest dto = new UpdateProfileRequest();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        model.addAttribute("profile", dto);
        return "profile-edit";
    }

    // ================= UPDATE PROFILE =================
    @PostMapping("/profile/edit")
    public String updateProfile(
            @Valid UpdateProfileRequest dto,
            BindingResult result,
            @RequestParam("image") MultipartFile image,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        Object idObj = session.getAttribute("userId");
        if (idObj == null) {
            return "redirect:/login";
        }

        Integer userId = Integer.valueOf(idObj.toString());

        if (result.hasErrors()) {
            model.addAttribute("profile", dto);
            return "profile-edit";
        }

        // ================= IMAGE UPLOAD CODE (ADD HERE) =================

        String uploadDir = System.getProperty("user.dir")
                + "/src/main/resources/static/uploads";

        Path uploadPath = Paths.get(uploadDir);

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileName = null;

        if (image != null && !image.isEmpty()) {
            fileName = "user_" + userId + "_" + image.getOriginalFilename();

            try {
                Files.copy(
                        image.getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ================= SERVICE CALL =================
        userService.updateProfile(userId, dto, fileName);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Profile updated successfully!"
        );

        return "redirect:/profile";
    }

}
