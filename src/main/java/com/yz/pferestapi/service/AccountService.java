package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.ChangePasswordDto;
import com.yz.pferestapi.dto.UpdateProfileDto;
import com.yz.pferestapi.dto.UserDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.UserMapper;
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

    public UserDto getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserMapper.toDto(user);
    }

    public UserDto updateProfile(UpdateProfileDto updateProfileDto) throws IOException {
        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User profile = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        if (updateProfileDto.getPhotoFile() != null && !updateProfileDto.getPhotoFile().isEmpty()) {
            String photoPath = fileService.uploadFile(updateProfileDto.getPhotoFile(), "users-photos");

            String oldPhotoPath = profile.getPhotoPath();
            if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                fileService.deleteFile(oldPhotoPath);
            }

            profile.setPhotoPath(photoPath);
        }

        profile.setFirstName(updateProfileDto.getFirstName());
        profile.setLastName(updateProfileDto.getLastName());
        profile.setPhoneNumber(updateProfileDto.getPhoneNumber());

        User updatedUser = userRepository.save(profile);
        return UserMapper.toDto(updatedUser);
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
