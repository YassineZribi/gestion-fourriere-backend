package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Employee;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {
    public static Specification<Employee> firstNameContains(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.trim().toLowerCase() + "%");
    }

    public static Specification<Employee> lastNameContains(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.trim().toLowerCase() + "%");
    }

    public static Specification<Employee> fullNameContains(String search) {
        System.out.println("search = " + search);
        return (root, query, criteriaBuilder) -> {
            String searchString = "%" + search.trim().toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.concat(root.get("firstName"), criteriaBuilder.concat(" ", root.get("lastName")))), searchString),
                    criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.concat(root.get("lastName"), criteriaBuilder.concat(" ", root.get("firstName")))), searchString)
            );
        };
    }

    public static Specification<Employee> positionContains(String position) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), "%" + position.trim().toLowerCase() + "%");
    }

    public static Specification<Employee> hasManager(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            Join<Employee, Employee> roleJoin = root.join("manager");
            return criteriaBuilder.equal(roleJoin.get("id"), managerId);
        };
    }
}
