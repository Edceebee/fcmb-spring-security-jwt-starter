package com.assessment.demo.service;

import com.assessment.demo.model.User;
import com.assessment.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service for user management operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Builds the admin users response payload.
     */
    public Map<String, Object> getAllUsersResponse() {
        List<User> users = getAllUsers();
        List<Map<String, String>> userList = users.stream()
                .map(user -> Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                ))
                .toList();

        return Map.of(
                "total", users.size(),
                "users", userList
        );
    }

    /**
     * Builds the current user response payload.
     */
    public Map<String, Object> getCurrentUserResponse(Authentication authentication) {
        String username = authentication.getName();
        User user = getUserByUsername(username);
        return Map.of(
                "userId", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
        );
    }
}
