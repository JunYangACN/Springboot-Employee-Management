package com.azid.springboot.assessment.employee_management.service;

import com.azid.springboot.assessment.employee_management.entity.User;
import com.azid.springboot.assessment.employee_management.exception.*;
import com.azid.springboot.assessment.employee_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(User user) {
        try {
            log.info("Saving user with username: {}", user.getUsername());
            if (userRepository.existsByUsername(user.getUsername())) {
                log.warn("User with username '{}' already exists", user.getUsername());
                throw new UserAlreadyExistsException("User with username '" + user.getUsername() + "' already exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            log.info("User with username '{}' saved successfully", savedUser.getUsername());
            return savedUser;
        } catch (DataAccessException e) {
            log.error("Database error while saving user: {}", e.getMessage());
            throw new DatabaseException("Database error occurred while saving user", e);
        } catch (Exception e) {
            log.error("Unexpected error while saving user: {}", e.getMessage());
            throw new UnexpectedServiceException("Unexpected error occurred while saving user", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.info("Loading user by username: {}", username);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("User with username '{}' not found", username);
                        return new UserNotFoundException("User with username '" + username + "' not found");
                    });
            log.info("User with username '{}' loaded successfully", username);
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(), user.getPassword(), new ArrayList<>()
            );
        } catch (DataAccessException e) {
            log.error("Database error while loading user: {}", e.getMessage());
            throw new DatabaseException("Database error occurred while loading user", e);
        } catch (Exception e) {
            log.error("Unexpected error while loading user: {}", e.getMessage());
            throw new UnexpectedServiceException("Unexpected error occurred while loading user", e);
        }
    }
}