package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.LoginDto;
import com.yz.pferestapi.dto.LoginResponseDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.repository.RoleRepository;
import com.yz.pferestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        // User user = userService.getUserByEmail(loginDto.getEmail());

        String accessToken = jwtService.generateAccessToken(authentication);

        return LoginResponseDto.builder()
                .user(user)
                .accessToken(accessToken)
                .build();
    }
}
