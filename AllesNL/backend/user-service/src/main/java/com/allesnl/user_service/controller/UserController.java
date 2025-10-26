package com.allesnl.user_service.controller;

import com.allesnl.user_service.dto.LoginRequest;
import com.allesnl.user_service.dto.LoginResponse;
import com.allesnl.user_service.model.User;
import com.allesnl.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Test endpoint
    @GetMapping("/hello")
    public String hello() {
        return "Hello World, this is User Service.";
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        if (userService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword())) {
            Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                LoginResponse response = new LoginResponse(
                        true,
                        "Login successful",
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber()
                );
                return ResponseEntity.ok(response);
            }
        }

        LoginResponse response = new LoginResponse(
                false,
                "Invalid email or password",
                null,
                null,
                null
        );
        return ResponseEntity.status(401).body(response);
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        // Set password to null before returning to avoid exposing hashed password
        savedUser.setPassword(null);
        return ResponseEntity.status(201).body(savedUser);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // Remove passwords from response
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setPassword(null);
            return ResponseEntity.ok(foundUser);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}