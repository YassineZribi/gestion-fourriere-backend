package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.UserCriteriaRequest;
import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<User>> findUsersByCriteria(UserCriteriaRequest userCriteria) {
        System.out.println("userCriteria = " + userCriteria);
        Page<User> users = userService.findUsersByCriteria(userCriteria);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody RegisterDto registerDto,
                                        @RequestParam String roleName) {
        User createdUser = userService.createUser(registerDto, roleName);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
