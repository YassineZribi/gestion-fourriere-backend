package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.LoginDto;
import com.yz.pferestapi.dto.LoginResponseDto;
import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.RoleRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    public User register(RegisterDto registerDto) {

        if (userRepository.existsByEmail(registerDto.getEmail())){
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        Role optionalRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        User user = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(List.of(optionalRole))
                .build();

        return userRepository.save(user);
    }

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
