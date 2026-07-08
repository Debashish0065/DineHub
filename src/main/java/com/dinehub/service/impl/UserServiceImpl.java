package com.dinehub.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dinehub.dto.request.ChangePasswordRequest;
import com.dinehub.dto.request.UpdateProfileRequest;
import com.dinehub.dto.request.UserRegistrationRequest;
import com.dinehub.dto.response.UserResponse;
import com.dinehub.entity.User;
import com.dinehub.exception.DuplicateResourceException;
import com.dinehub.exception.ResourceNotFoundException;
import com.dinehub.mapper.UserMapper;
import com.dinehub.repository.UserRepository;
import com.dinehub.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists.");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateProfile(String email,
                                      UpdateProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // Check duplicate phone number
        if (!user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {

            throw new DuplicateResourceException(
                    "Phone number already exists.");
        }

        // Update user details
        userMapper.updateUserFromRequest(user, request);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void changePassword(String email,
                               ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "Old password is incorrect.");
        }

        // New password should not be same as old password
        if (passwordEncoder.matches(request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password cannot be the same as the old password.");
        }

        // Confirm password validation
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new IllegalArgumentException(
                    "New password and confirm password do not match.");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
}