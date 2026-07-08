package com.dinehub.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {

        ex.printStackTrace();   // <-- IMPORTANT

        model.addAttribute("errorMessage", ex.toString());

        return "error";
    }
}