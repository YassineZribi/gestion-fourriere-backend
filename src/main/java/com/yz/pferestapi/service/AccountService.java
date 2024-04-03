package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.ChangePasswordDto;
import com.yz.pferestapi.dto.UpdateProfileDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    public User getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User updateProfile(UpdateProfileDto updateProfileDto, MultipartFile photoFile) throws IOException {
        User profile = userRepository.findByEmail(updateProfileDto.getEmail())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.uploadFile(photoFile, "photos");

            String oldPhotoPath = profile.getPhotoPath();
            if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                fileService.deleteFile(oldPhotoPath);
            }

            profile.setPhotoPath(photoPath);
        }

        profile.setFirstName(updateProfileDto.getFirstName());
        profile.setLastName(updateProfileDto.getLastName());
        profile.setPhoneNumber(updateProfileDto.getPhoneNumber());

        return userRepository.save(profile);
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())){
            throw new AppException(HttpStatus.BAD_REQUEST, "newPassword and confirmNewPassword did not match");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        boolean passwordsMatch = passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword());

        if (!passwordsMatch) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Current passwords did not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));

        userRepository.save(user);
    }
}
