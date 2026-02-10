package com.assessment.demo.service;

import com.assessment.demo.model.User;
import com.assessment.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes sample users on application startup.
 * Creates test users for different roles.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create admin user
        User admin = new User(
                "admin",
                passwordEncoder.encode("admin123"),
                "ROLE_ADMIN"
        );
        userRepository.save(admin);
        logger.info("Created admin user: username=admin, password=admin123");

        // Create regular user
        User user = new User(
                "user",
                passwordEncoder.encode("user123"),
                "ROLE_USER"
        );
        userRepository.save(user);
        logger.info("Created regular user: username=user, password=user123");
    }
}
