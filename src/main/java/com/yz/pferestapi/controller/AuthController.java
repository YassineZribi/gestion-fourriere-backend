package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.LoginDto;
import com.yz.pferestapi.dto.LoginResponseDto;
import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private  final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterDto registerDto) {
        User registeredUser = authService.register(registerDto);

        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        LoginResponseDto loginResponseDto = authService.login(loginDto);

        return ResponseEntity.ok(loginResponseDto);
    }

    @GetMapping("/greeting")
    public String getLoginInfo(Principal principal) {
        return "hello " + principal.getName();
    }

    @GetMapping("/profile")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }
}
