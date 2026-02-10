package com.assessment.demo.repository;

import com.assessment.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username);
}
