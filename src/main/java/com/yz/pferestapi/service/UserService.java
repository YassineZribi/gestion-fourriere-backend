package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.RegisterDto;
import com.yz.pferestapi.dto.UserCriteriaRequest;
import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.RoleRepository;
import com.yz.pferestapi.repository.UserRepository;
import com.yz.pferestapi.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User createUser(RegisterDto registerDto, String roleName) {
        if (!roleName.equalsIgnoreCase(RoleEnum.MANAGER.name()) && !roleName.equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(roleName.toUpperCase());

        if (userRepository.existsByEmail(registerDto.getEmail())) {
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

    public Page<User> findUsersByCriteria(UserCriteriaRequest userCriteria) {
        Specification<User> spec = Specification.where(null);

        if (userCriteria.getFirstName() != null) {
            spec = spec.and(UserSpecifications.firstNameContains(userCriteria.getFirstName()));
        }

        if (userCriteria.getLastName() != null) {
            spec = spec.and(UserSpecifications.lastNameContains(userCriteria.getLastName()));
        }

        if (userCriteria.getEmail() != null) {
            spec = spec.and(UserSpecifications.emailContains(userCriteria.getEmail()));
        }

        if (userCriteria.getPhoneNumber() != null) {
            spec = spec.and(UserSpecifications.phoneNumberContains(userCriteria.getPhoneNumber()));
        }

        // If no sorting criteria provided by the client, sort by "createdAt" DESC by default
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if (userCriteria.getSort() != null && !userCriteria.getSort().isEmpty()) {
            List<Sort.Order> orders = userCriteria.getSort().stream()
                    .map(param -> {
                        String[] parts = param.split(";");
                        String property = parts[0];
                        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
                        return new Sort.Order(direction, property);
                    })
                    .collect(Collectors.toList());
            sort = Sort.by(orders).and(sort); // orders will be the primary sorting criteria, and sort object will be the secondary sorting criterion.
        }

        Pageable pageable = PageRequest.of(userCriteria.getPage(), userCriteria.getSize(), sort);

        return userRepository.findAll(spec, pageable);
    }
}
