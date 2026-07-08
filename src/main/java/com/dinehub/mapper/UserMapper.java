package com.dinehub.mapper;

import org.springframework.stereotype.Component;

import com.dinehub.dto.request.UpdateProfileRequest;
import com.dinehub.dto.request.UserRegistrationRequest;
import com.dinehub.dto.response.UserResponse;
import com.dinehub.entity.User;
import com.dinehub.enums.Role;

@Component
public class UserMapper {

    public User toEntity(UserRegistrationRequest request) {

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();
    }

    public UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }

    /**
     * Update existing user from profile form.
     * Email and Role are intentionally not modified.
     */
    public void updateUserFromRequest(User user,
                                      UpdateProfileRequest request) {

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
    }

}