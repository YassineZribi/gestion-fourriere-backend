package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Role;
import com.yz.pferestapi.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.trim().toLowerCase() + "%");
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.trim().toLowerCase() + "%");
    }

    public static Specification<User> emailContains(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.trim().toLowerCase() + "%");
    }

    public static Specification<User> phoneNumberContains(String phoneNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("phoneNumber"), "%" + phoneNumber.trim() + "%");
    }

    public static Specification<User> notHasRole(String roleName) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> roles = root.join("roles");
            return criteriaBuilder.notEqual(criteriaBuilder.lower(roles.get("name")), roleName.trim().toLowerCase());
        };
    }

    public static Specification<User> hasRole(String roleName) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> roles = root.join("roles");
            return criteriaBuilder.equal(criteriaBuilder.lower(roles.get("name")), roleName.trim().toLowerCase());
        };
    }
}
