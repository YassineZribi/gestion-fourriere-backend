package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.UpdateProfileDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;

    public User getProfile(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found"));
    }

    public User updateProfile(UpdateProfileDto updateProfileDto) {
        User profile = userRepository.findByEmail(updateProfileDto.getEmail())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        profile.setFirstName(updateProfileDto.getFirstName());
        profile.setLastName(updateProfileDto.getLastName());
        profile.setPhoneNumber(updateProfileDto.getPhoneNumber());

        return userRepository.save(profile);
    }
}
