package com.ajeet.hospital.service;

import com.ajeet.hospital.dto.UserResponse;
import com.ajeet.hospital.entity.User;
import com.ajeet.hospital.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = findUser(id);
        return toResponse(user);
    }

    public void disableUser(Long id, String currentUsername) {

        User user = findUser(id);

        // Prevent admin from disabling their own account
        if (user.getUsername().equals(currentUsername)) {
            throw new IllegalStateException(
                    "You cannot disable your own account"
            );
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    public void enableUser(Long id) {

        User user = findUser(id);

        user.setEnabled(true);
        userRepository.save(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id)
                );
    }

    private UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setEnabled(user.isEnabled());

        return response;
    }
}