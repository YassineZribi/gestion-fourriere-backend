package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List <User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_SUPER_ADMIN', 'SCOPE_ADMIN')")
    public ResponseEntity<?> createSimpleUser(@RequestBody RegisterDto registerDto) {
        User createdSimpleUser = userService.createUser(registerDto, RoleEnum.USER);
        return new ResponseEntity<>(createdSimpleUser, HttpStatus.CREATED);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('SCOPE_SUPER_ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody RegisterDto registerDto) {
        User createdAdmin = userService.createUser(registerDto, RoleEnum.ADMIN);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }
}
