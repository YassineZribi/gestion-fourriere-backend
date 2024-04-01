package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<User>> getAllUsers() {
        List <User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PostMapping("/operator")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<User> createOperator(@Valid @RequestBody RegisterDto registerDto) {
        User createdSimpleUser = userService.createUser(registerDto, RoleEnum.OPERATOR);
        return new ResponseEntity<>(createdSimpleUser, HttpStatus.CREATED);
    }

    @PostMapping("/manager")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<User> createManager(@Valid @RequestBody RegisterDto registerDto) {
        User createdManager = userService.createUser(registerDto, RoleEnum.MANAGER);
        return new ResponseEntity<>(createdManager, HttpStatus.CREATED);
    }
}
