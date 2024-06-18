package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Employee;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecifications {
    public static Specification<Employee> positionContains(String position) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), "%" + position.trim().toLowerCase() + "%");
    }

    public static Specification<Employee> hasManager(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            Join<Employee, Employee> managerJoin = root.join("manager");
            return criteriaBuilder.equal(managerJoin.get("id"), managerId);
        };
    }
}
