package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.*;
import com.yz.pferestapi.util.NumberUtil;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ArticleFamilySpecifications {
    public static Specification<ArticleFamily> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<ArticleFamily> descriptionContains(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.trim().toLowerCase() + "%");
    }

    public static Specification<ArticleFamily> nightlyAmountContains(Double nightlyAmount) {
        return (root, query, criteriaBuilder) -> {
            String nightlyAmountString = NumberUtil.format(nightlyAmount);
            return criteriaBuilder.like(root.get("nightlyAmount").as(String.class), "%" + nightlyAmountString + "%");
        };
    }

    public static Specification<ArticleFamily> nightlyAmountStartsWith(Double nightlyAmount) {
        return (root, query, criteriaBuilder) -> {
            String nightlyAmountString = NumberUtil.format(nightlyAmount);
            return criteriaBuilder.like(root.get("nightlyAmount").as(String.class), nightlyAmountString + "%");
        };
    }

    public static Specification<ArticleFamily> unitCalculationIs(Boolean unitCalculation) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("unitCalculation"), unitCalculation);
    }

    public static Specification<ArticleFamily> hasRegister(Long registerId) {
        return (root, query, criteriaBuilder) -> {
            Join<ArticleFamily, Register> registerJoin = root.join("register");
            return criteriaBuilder.equal(registerJoin.get("id"), registerId);
        };
    }

    public static Specification<ArticleFamily> hasMeasurementUnit(Long measurementUnitId) {
        return (root, query, criteriaBuilder) -> {
            Join<ArticleFamily, MeasurementUnit> measurementUnitJoin = root.join("measurementUnit");
            return criteriaBuilder.equal(measurementUnitJoin.get("id"), measurementUnitId);
        };
    }
}
