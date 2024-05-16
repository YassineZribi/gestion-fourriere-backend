package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Register;
import com.yz.pferestapi.entity.SubRegister;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class SubRegisterSpecifications {
    public static Specification<SubRegister> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<SubRegister> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.trim().toLowerCase() + "%");
    }

    public static Specification<SubRegister> hasRegister(Long registerId) {
        return (root, query, criteriaBuilder) -> {
            Join<SubRegister, Register> registerJoin = root.join("register");
            return criteriaBuilder.equal(registerJoin.get("id"), registerId);
        };
    }
}
