package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Operation;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OperationSpecifications {
    public static <T extends Operation> Specification<T> numberEquals(Long number) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("number"), number);
    }

    public static <T extends Operation> Specification<T> numberFrom(Long startNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("number"), startNumber);
    }

    public static <T extends Operation> Specification<T> numberTo(Long endNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("number"), endNumber);
    }

    public static <T extends Operation> Specification<T> yearEquals(Integer year) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("year"), year);
    }

    public static <T extends Operation> Specification<T> yearFrom(Integer startYear) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("year"), startYear);
    }

    public static <T extends Operation> Specification<T> yearTo(Integer endYear) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("year"), endYear);
    }

    public static <T extends Operation> Specification<T> dateTimeEquals(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            // Convert the Instant to LocalDate in the same timezone
            Expression<LocalDate> registerDate = criteriaBuilder.function(
                    "DATE", LocalDate.class, root.get("dateTime")
            );
            return criteriaBuilder.equal(registerDate, date);
        };
    }

    public static <T extends Operation> Specification<T> dateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            // Convert the Instant to LocalDate in the same timezone
            Expression<LocalDate> dateTime = criteriaBuilder.function(
                    "DATE", LocalDate.class, root.get("dateTime")
            );
            Predicate greaterThanOrEqualTo = criteriaBuilder.greaterThanOrEqualTo(dateTime, startDate);
            Predicate lessThanOrEqualTo = criteriaBuilder.lessThanOrEqualTo(dateTime, endDate);
            return criteriaBuilder.and(greaterThanOrEqualTo, lessThanOrEqualTo);
        };
    }

    public static <T extends Operation> Specification<T> dateFrom(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> {
            // Convert the Instant to LocalDate in the same timezone
            Expression<LocalDate> dateTime = criteriaBuilder.function(
                    "DATE", LocalDate.class, root.get("dateTime")
            );
            Predicate greaterThanOrEqualTo = criteriaBuilder.greaterThanOrEqualTo(dateTime, startDate);
            return criteriaBuilder.and(greaterThanOrEqualTo);
        };
    }

    public static <T extends Operation> Specification<T> dateTo(LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            // Convert the Instant to LocalDate in the same timezone
            Expression<LocalDate> dateTime = criteriaBuilder.function(
                    "DATE", LocalDate.class, root.get("dateTime")
            );
            Predicate lessThanOrEqualTo = criteriaBuilder.lessThanOrEqualTo(dateTime, endDate);
            return criteriaBuilder.and(lessThanOrEqualTo);
        };
    }
}
