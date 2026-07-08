package com.dinehub.service;

import java.util.List;

import com.dinehub.dto.request.ChangePasswordRequest;
import com.dinehub.dto.request.UpdateProfileRequest;
import com.dinehub.dto.request.UserRegistrationRequest;
import com.dinehub.dto.response.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse getUserByEmail(String email);

    UserResponse updateProfile(
            String email,
            UpdateProfileRequest request);

    void changePassword(
            String email,
            ChangePasswordRequest request);

}