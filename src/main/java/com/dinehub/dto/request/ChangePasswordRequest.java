package com.dinehub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Old Password is required")
    private String oldPassword;

    @NotBlank(message = "New Password is required")
    @Size(min = 8, max = 50,
          message = "Password must be between 8 and 50 characters")
    private String newPassword;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

}