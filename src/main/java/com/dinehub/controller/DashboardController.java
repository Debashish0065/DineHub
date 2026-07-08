package com.dinehub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        String username = "Guest";

        Authentication auth =
                SecurityContextHolder
                .getContext()
                .getAuthentication();

        if(auth != null && auth.isAuthenticated()) {
            username = auth.getName();
        }

        model.addAttribute("username", username);

        return "dashboard";
    }

}