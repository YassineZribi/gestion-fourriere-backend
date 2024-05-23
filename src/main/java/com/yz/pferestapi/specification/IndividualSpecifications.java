package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Individual;
import org.springframework.data.jpa.domain.Specification;

public class IndividualSpecifications {
    public static Specification<Individual> firstNameContains(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.trim().toLowerCase() + "%");
    }

    public static Specification<Individual> lastNameContains(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.trim().toLowerCase() + "%");
    }

    public static Specification<Individual> nationalIdContains(String nationalId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nationalId")), "%" + nationalId.trim().toLowerCase() + "%");
    }
}
