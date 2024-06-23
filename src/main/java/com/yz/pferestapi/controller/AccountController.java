package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.ChangePasswordDto;
import com.yz.pferestapi.dto.UpdateProfileDto;
import com.yz.pferestapi.dto.UserDto;
import com.yz.pferestapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/account")
@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile() {
        UserDto profile = accountService.getProfile();
        return ResponseEntity.ok(profile);
    }

    @PatchMapping(value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserDto> updateProfile(@Validated @ModelAttribute UpdateProfileDto updateProfileDto) throws IOException {
        UserDto profile = accountService.updateProfile(updateProfileDto);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        accountService.changePassword(changePasswordDto);
        return ResponseEntity.noContent().build();
    }
}
