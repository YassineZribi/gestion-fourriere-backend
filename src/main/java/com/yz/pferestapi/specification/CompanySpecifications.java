package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Company;
import org.springframework.data.jpa.domain.Specification;

public class CompanySpecifications {
    public static Specification<Company> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<Company> taxIdContains(String taxId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("taxId")), "%" + taxId.trim().toLowerCase() + "%");
    }
}
