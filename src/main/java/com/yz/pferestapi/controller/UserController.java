package com.yz.pferestapi.controller;

import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User me = userService.getUserByEmail(email);
        return ResponseEntity.ok(me);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List <User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }
}
