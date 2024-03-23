package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.LoginDto;
import com.yz.pferestapi.dto.LoginResponseDto;
import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserService userService;

    public User register(RegisterDto registerDto) {

        if (userRepository.existsByEmail(registerDto.getEmail())){
            // TODO: add check for email exists in database
            //throw new AppException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        User user = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(new ArrayList<>())
                .build();

        return userRepository.save(user);
    }

    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        User user = userService.getUserByEmail(loginDto.getEmail());

        String accessToken = jwtService.generateAccessToken(authentication);

        return LoginResponseDto.builder()
                .user(user)
                .accessToken(accessToken)
                .build();
    }
}
