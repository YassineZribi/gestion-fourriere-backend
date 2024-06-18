package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.UserCriteriaRequest;
import com.yz.pferestapi.entity.*;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.repository.UserRepository;
import com.yz.pferestapi.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();

    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public static <T extends UserCriteriaRequest, U extends User> Specification<U> filter(T criteria, Specification<U> spec) {
        // Get non admins
        spec = spec.and(UserSpecifications.notHasRole(RoleEnum.ADMIN.toString()));

        if (criteria.getFirstName() != null) {
            spec = spec.and(UserSpecifications.firstNameContains(criteria.getFirstName()));
        }

        if (criteria.getLastName() != null) {
            spec = spec.and(UserSpecifications.lastNameContains(criteria.getLastName()));
        }

        if (criteria.getEmail() != null) {
            spec = spec.and(UserSpecifications.emailContains(criteria.getEmail()));
        }

        if (criteria.getPhoneNumber() != null) {
            spec = spec.and(UserSpecifications.phoneNumberContains(criteria.getPhoneNumber()));
        }

        // Get users with specific role
        if (criteria.getRoleName() != null) {
            spec = spec.and(UserSpecifications.hasRole(criteria.getRoleName()));
        }

        return spec;
    }

    public static <U extends User> Specification<U> findAllByFullName(String search) {
        Specification<U> spec = Specification.where(null);

        // Get non admins
        spec = spec.and(UserSpecifications.notHasRole(RoleEnum.ADMIN.toString()));

        spec = spec.and(UserSpecifications.fullNameContains(search));

        return spec;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
