package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Source;
import org.springframework.data.jpa.domain.Specification;

public class SourceSpecifications {
    public static Specification<Source> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<Source> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.trim().toLowerCase() + "%");
    }
}
