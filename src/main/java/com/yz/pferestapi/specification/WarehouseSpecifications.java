package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.entity.Warehouse;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class WarehouseSpecifications {
    public static Specification<Warehouse> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<Warehouse> addressContains(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + address.trim().toLowerCase() + "%");
    }

    public static Specification<Warehouse> latitudeContains(Double latitude) {
        return (root, query, criteriaBuilder) -> {
            String latitudeString = format(latitude);
            return criteriaBuilder.like(root.get("latitude").as(String.class), "%" + latitudeString + "%");
        };
    }

    public static Specification<Warehouse> latitudeStartsWith(Double latitude) {
        return (root, query, criteriaBuilder) -> {
            String latitudeString = format(latitude);
            return criteriaBuilder.like(root.get("latitude").as(String.class), latitudeString + "%");
        };
    }

    public static Specification<Warehouse> longitudeContains(Double longitude) {
        return (root, query, criteriaBuilder) -> {
            String longitudeString = format(longitude);
            return criteriaBuilder.like(root.get("longitude").as(String.class), "%" + longitudeString + "%");
        };
    }

    public static Specification<Warehouse> longitudeStartsWith(Double longitude) {
        return (root, query, criteriaBuilder) -> {
            String longitudeString = format(longitude);
            return criteriaBuilder.like(root.get("longitude").as(String.class), longitudeString + "%");
        };
    }

    public static Specification<Warehouse> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false);
    }

    public static Specification<Warehouse> hasManager(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            Join<Warehouse, Employee> roleJoin = root.join("manager");
            return criteriaBuilder.equal(roleJoin.get("id"), managerId);
        };
    }

    // format Double to String
    private static String format(Double v) {
        // Create a DecimalFormat instance with Locale.US to ensure dot as decimal separator
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

        // In Java, when you convert a double to a String, the .toString() method provided by the
        // Double class will append .0 to the end if the number doesn't already have a decimal point.
        // To remove this behavior, you can format the double using DecimalFormat
        DecimalFormat df = new DecimalFormat("#.###", symbols);

        return df.format(v);
    }
}
