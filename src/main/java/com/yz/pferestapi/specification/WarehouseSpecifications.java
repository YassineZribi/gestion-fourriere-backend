package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.User;
import com.yz.pferestapi.entity.Warehouse;
import com.yz.pferestapi.util.NumberUtil;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class WarehouseSpecifications {
    public static Specification<Warehouse> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<Warehouse> addressContains(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + address.trim().toLowerCase() + "%");
    }

    public static Specification<Warehouse> latitudeContains(Double latitude) {
        return (root, query, criteriaBuilder) -> {
            String latitudeString = NumberUtil.format(latitude);
            return criteriaBuilder.like(root.get("latitude").as(String.class), "%" + latitudeString + "%");
        };
    }

    public static Specification<Warehouse> latitudeStartsWith(Double latitude) {
        return (root, query, criteriaBuilder) -> {
            String latitudeString = NumberUtil.format(latitude);
            return criteriaBuilder.like(root.get("latitude").as(String.class), latitudeString + "%");
        };
    }

    public static Specification<Warehouse> longitudeContains(Double longitude) {
        return (root, query, criteriaBuilder) -> {
            String longitudeString = NumberUtil.format(longitude);
            return criteriaBuilder.like(root.get("longitude").as(String.class), "%" + longitudeString + "%");
        };
    }

    public static Specification<Warehouse> longitudeStartsWith(Double longitude) {
        return (root, query, criteriaBuilder) -> {
            String longitudeString = NumberUtil.format(longitude);
            return criteriaBuilder.like(root.get("longitude").as(String.class), longitudeString + "%");
        };
    }

    public static Specification<Warehouse> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }

    public static Specification<Warehouse> hasManager(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            Join<Warehouse, User> managerJoin = root.join("manager");
            return criteriaBuilder.equal(managerJoin.get("id"), managerId);
        };
    }
}
