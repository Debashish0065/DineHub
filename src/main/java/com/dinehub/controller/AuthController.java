package com.dinehub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dinehub.dto.request.UserRegistrationRequest;
import com.dinehub.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegistrationRequest request,
                           Model model) {

        try {
            userService.registerUser(request);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}