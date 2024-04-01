package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.UpdateProfileDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/account")
@RestController
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User profile = accountService.getProfile(authentication);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/profile")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UpdateProfileDto updateProfileDto) {
        User profile = accountService.updateProfile(updateProfileDto);
        return ResponseEntity.ok(profile);
    }
}
