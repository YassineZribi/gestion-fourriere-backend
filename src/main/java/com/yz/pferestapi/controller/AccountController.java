package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.ChangePasswordDto;
import com.yz.pferestapi.dto.UpdateProfileDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/account")
@RestController
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        User profile = accountService.getProfile();
        return ResponseEntity.ok(profile);
    }

    @PatchMapping(value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<User> updateProfile(@RequestPart("data") @Valid UpdateProfileDto updateProfileDto,
                                              @RequestPart(value = "media", required = false) MultipartFile photoFile) throws IOException {
        User profile = accountService.updateProfile(updateProfileDto, photoFile);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        accountService.changePassword(changePasswordDto);
        return ResponseEntity.noContent().build();
    }
}
