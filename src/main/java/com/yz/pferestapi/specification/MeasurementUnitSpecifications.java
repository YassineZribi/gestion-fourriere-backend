package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.MeasurementUnit;
import org.springframework.data.jpa.domain.Specification;

public class MeasurementUnitSpecifications {
    public static Specification<MeasurementUnit> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<MeasurementUnit> symbolContains(String symbol) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("symbol")), "%" + symbol.trim().toLowerCase() + "%");
    }
}
