package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.UpsertUserDto;
import com.yz.pferestapi.dto.UserCriteriaRequest;
import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.RoleEnum;
import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.RoleRepository;
import com.yz.pferestapi.repository.UserRepository;
import com.yz.pferestapi.specification.UserSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import com.yz.pferestapi.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final FileService fileService;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User createUser(UpsertUserDto upsertUserDto) {
        if (!upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(upsertUserDto.getRoleName().toUpperCase());

        if (userRepository.existsByEmail(upsertUserDto.getEmail())) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        String generatedPassword = PasswordGenerator.generate(10);

        System.out.println("generatedPassword = " + generatedPassword);
        
        var user = User.builder()
                .firstName(upsertUserDto.getFirstName())
                .lastName(upsertUserDto.getLastName())
                .email(upsertUserDto.getEmail())
                .phoneNumber(upsertUserDto.getPhoneNumber())
                .password(passwordEncoder.encode(generatedPassword))
                .role(role)
                .build();

        // TODO: send email (login: email, password: generatedPassword)

        return userRepository.save(user);
    }

    public User updateUser(UpsertUserDto upsertUserDto, Long id) {
        if (!upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.MANAGER.name()) && !upsertUserDto.getRoleName().equalsIgnoreCase(RoleEnum.OPERATOR.name())) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Invalid role name");
        }
        RoleEnum roleEnum = RoleEnum.valueOf(upsertUserDto.getRoleName().toUpperCase());

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Role not found"));

        Optional<User> optionalUser = userRepository.findByEmail(upsertUserDto.getEmail());

        if (optionalUser.isPresent() && !optionalUser.get().getId().equals(id)) {
            throw new AppException(HttpStatus.CONFLICT, "Email already exists!");
        }

        user.setFirstName(upsertUserDto.getFirstName());
        user.setLastName(upsertUserDto.getLastName());
        user.setEmail(upsertUserDto.getEmail());
        user.setPhoneNumber(upsertUserDto.getPhoneNumber());
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

        Sort sort = CriteriaRequestUtil.buildSortCriteria(userCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(userCriteria, sort);

        return userRepository.findAll(spec, pageable);
    }

    public void deleteUser(Long id) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        String photoPath = user.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            fileService.deleteFile(photoPath);
        }

        userRepository.delete(user);
    }
}
