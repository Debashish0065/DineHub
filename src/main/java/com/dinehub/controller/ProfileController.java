package com.dinehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dinehub.dto.request.ChangePasswordRequest;
import com.dinehub.dto.request.UpdateProfileRequest;
import com.dinehub.dto.response.UserResponse;
import com.dinehub.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // ===========================
    // View Profile
    // ===========================
    @GetMapping("/profile")
    public String viewProfile(Authentication authentication,
                              Model model) {

        String email = authentication.getName();

        UserResponse user = userService.getUserByEmail(email);

        model.addAttribute("user", user);

        return "profile";
    }

    // ===========================
    // Edit Profile Page
    // ===========================
    @GetMapping("/profile/edit")
    public String editProfile(Authentication authentication,
                              Model model) {

        String email = authentication.getName();

        UserResponse user = userService.getUserByEmail(email);

        model.addAttribute("user", user);

        return "edit-profile";
    }

    // ===========================
    // Update Profile
    // ===========================
    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("user") UpdateProfileRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "edit-profile";
        }

        try {

            userService.updateProfile(authentication.getName(), request);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Profile updated successfully.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/profile";
    }

    // ===========================
    // Change Password Page
    // ===========================
    @GetMapping("/profile/change-password")
    public String changePasswordPage(Model model) {

        model.addAttribute(
                "changePasswordRequest",
                new ChangePasswordRequest());

        return "change-password";
    }

    // ===========================
    // Change Password
    // ===========================
    @PostMapping("/profile/change-password")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordRequest")
            ChangePasswordRequest request,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "change-password";
        }

        try {

            userService.changePassword(
                    authentication.getName(),
                    request);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Password changed successfully.");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/profile/change-password";
    }

}