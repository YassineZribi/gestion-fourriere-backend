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
            Join<User, Role> roleJoin = root.join("role");
            return criteriaBuilder.notEqual(criteriaBuilder.lower(roleJoin.get("name")), roleName.trim().toLowerCase());
        };
    }

    public static Specification<User> hasRole(String roleName) {
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> roleJoin = root.join("role");
            return criteriaBuilder.equal(criteriaBuilder.lower(roleJoin.get("name")), roleName.trim().toLowerCase());
        };
    }

    public static Specification<User> fullNameContains(String search) {
        System.out.println("search = " + search);
        return (root, query, criteriaBuilder) -> {
            String searchString = "%" + search.trim().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.concat(root.get("firstName"), criteriaBuilder.concat(" ", root.get("lastName")))), searchString),
                    criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.concat(root.get("lastName"), criteriaBuilder.concat(" ", root.get("firstName")))), searchString)
            );
        };
    }

    public static Specification<User> positionContains(String position) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), "%" + position.trim().toLowerCase() + "%");
    }

    public static Specification<User> hasManager(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            Join<User, User> managerJoin = root.join("manager");
            return criteriaBuilder.equal(managerJoin.get("id"), managerId);
        };
    }
}
