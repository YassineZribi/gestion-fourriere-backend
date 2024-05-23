package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Owner;
import org.springframework.data.jpa.domain.Specification;

public class OwnerSpecifications {
    public static <T extends Owner> Specification<T> addressContains(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + address.trim().toLowerCase() + "%");
    }

    public static <T extends Owner> Specification<T> emailContains(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.trim().toLowerCase() + "%");
    }

    public static <T extends Owner> Specification<T> phoneNumberContains(String phoneNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("phoneNumber"), "%" + phoneNumber.trim() + "%");
    }
}
