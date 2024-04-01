package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.RoleRepository;
import com.yz.pferestapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User createUser(RegisterDto registerDto, RoleEnum roleEnum) {
        if (userRepository.existsByEmail(registerDto.getEmail())){
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        Role optionalRole = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));


        var user = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .phoneNumber(registerDto.getPhoneNumber())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .roles(List.of(optionalRole))
                .build();

        return userRepository.save(user);
    }

}
