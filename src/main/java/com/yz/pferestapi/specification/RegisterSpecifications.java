package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Register;
import org.springframework.data.jpa.domain.Specification;

public class RegisterSpecifications {
    public static Specification<Register> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<Register> observationContains(String observation) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("observation")), "%" + observation.trim().toLowerCase() + "%");
    }
}
