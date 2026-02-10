package com.assessment.demo.service;

import com.assessment.demo.model.User;
import com.assessment.demo.repository.UserRepository;
import com.assessment.security.dto.LoginRequest;
import com.assessment.security.dto.LoginResponse;
import com.assessment.security.exception.AuthenticationException;
import com.assessment.security.util.JwtUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling user authentication.
 * Validates credentials and generates JWT tokens.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user and generates a JWT token.
     * 
     * @param request Login credentials
     * @return Login response with JWT token
     * @throws AuthenticationException if credentials are invalid
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        // Create UserDetails for JWT generation
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getRole()))
                .build();

        // Generate JWT token
        String token = jwtUtil.generateToken(userDetails, user.getId());

        // Build response
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                List.of(user.getRole()),
                jwtUtil.extractExpiration(token).getTime()
        );
    }
}
