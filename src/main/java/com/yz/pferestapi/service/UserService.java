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
import com.yz.pferestapi.util.PasswordGenerator;
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
import java.util.Optional;
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

    public User createUser(RegisterDto registerDto) {
        if (!registerDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !registerDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(registerDto.getRoleName().toUpperCase());

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        String generatedPassword = PasswordGenerator.generate(10);

        System.out.println("generatedPassword = " + generatedPassword);
        
        var user = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .phoneNumber(registerDto.getPhoneNumber())
                .password(passwordEncoder.encode(generatedPassword))
                .role(role)
                .build();

        // TODO: send email (login: email, password: generatedPassword)

        return userRepository.save(user);
    }

    public User updateUser(RegisterDto registerDto, Long id) {
        if (!registerDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !registerDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(registerDto.getRoleName().toUpperCase());

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        Optional<User> optionalUser = userRepository.findByEmail(registerDto.getEmail());

        if (optionalUser.isPresent() && !optionalUser.get().getId().equals(id)) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setRole(role);

        // TODO: send SMS id email has been modified

        return userRepository.save(user);
    }

    public Page<User> findUsersByCriteria(UserCriteriaRequest userCriteria) {
        Specification<User> spec = Specification.where(null);

        // Get non admins
        spec = spec.and(UserSpecifications.notHasRole(RoleEnum.ADMIN.toString()));

        // Get users with specific role
        if (userCriteria.getRoleName() != null) {
            spec = spec.and(UserSpecifications.hasRole(userCriteria.getRoleName()));
        }

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
                        String[] parts = param.split("-");
                        String property = parts[0];
                        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
                        return new Sort.Order(direction, property);
                    })
                    .collect(Collectors.toList());
            sort = Sort.by(orders).and(sort); // orders will be the primary sorting criteria, and sort object will be the secondary sorting criterion.
        }

        Pageable pageable = PageRequest.of(userCriteria.getPage() - 1, userCriteria.getSize(), sort);

        return userRepository.findAll(spec, pageable);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.delete(user);
    }
}
