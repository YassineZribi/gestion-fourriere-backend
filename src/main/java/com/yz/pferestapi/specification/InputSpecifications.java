package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.*;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class InputSpecifications {
    public static Specification<Input> statusEquals(String status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("status")), status.trim().toLowerCase());
    }

    public static Specification<Input> hasRegister(Long registerId) {
        return (root, query, criteriaBuilder) -> {
            Join<Input, Register> registerJoin = root.join("register");
            return criteriaBuilder.equal(registerJoin.get("id"), registerId);
        };
    }

    public static Specification<Input> hasSubRegister(Long subRegisterId) {
        return (root, query, criteriaBuilder) -> {
            Join<Input, SubRegister> subRegisterJoin = root.join("subRegister");
            return criteriaBuilder.equal(subRegisterJoin.get("id"), subRegisterId);
        };
    }

    public static Specification<Input> hasOwner(Long ownerId) {
        return (root, query, criteriaBuilder) -> {
            Join<Input, Owner> ownerJoin = root.join("owner");
            return criteriaBuilder.equal(ownerJoin.get("id"), ownerId);
        };
    }

    public static Specification<Input> hasSource(Long sourceId) {
        return (root, query, criteriaBuilder) -> {
            Join<Input, Source> sourceJoin = root.join("source");
            return criteriaBuilder.equal(sourceJoin.get("id"), sourceId);
        };
    }
}
