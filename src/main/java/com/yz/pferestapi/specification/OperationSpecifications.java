package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Operation;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OperationSpecifications {
    public static <T extends Operation> Specification<T> numberEquals(Long number) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("number"), number);
    }

    public static <T extends Operation> Specification<T> yearEquals(Integer year) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("year"), year);
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
}
