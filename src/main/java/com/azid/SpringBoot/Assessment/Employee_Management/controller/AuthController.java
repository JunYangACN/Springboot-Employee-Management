package com.azid.SpringBoot.Assessment.Employee_Management.controller;

import com.azid.SpringBoot.Assessment.Employee_Management.dto.UserRequestDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.dto.UserResponseDTO;
import com.azid.SpringBoot.Assessment.Employee_Management.entity.User;
import com.azid.SpringBoot.Assessment.Employee_Management.service.UserService;
import com.azid.SpringBoot.Assessment.Employee_Management.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public String authPage() {
        log.info("Accessing authentication page");
        return "auth";
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(@RequestBody UserRequestDTO request) {
        log.info("Registering user with username: {}", request.getUsername());
        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
        userService.save(user);
        log.info("User '{}' registered successfully", request.getUsername());
        return "User registered successfully";
    }

    @PostMapping("/login")
    @ResponseBody
    public UserResponseDTO login(@RequestBody UserRequestDTO request) {
        log.info("User attempting to log in with username: {}", request.getUsername());
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        UserDetails user = userService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(user.getUsername());
        log.info("User '{}' logged in successfully, token generated", request.getUsername());
        return new UserResponseDTO(token);
    }

    @GetMapping("/home")
    public String home() {
        log.info("Accessing home page");
        return "home";
    }
}