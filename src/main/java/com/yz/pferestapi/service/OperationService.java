package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.OperationCriteriaRequest;
import com.yz.pferestapi.entity.Operation;
import com.yz.pferestapi.specification.OperationSpecifications;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class OperationService {
    public static <T extends OperationCriteriaRequest, U extends Operation> Specification<U> filter(T criteria, Specification<U> spec) {
        if (criteria.getNumber() != null) {
            spec = spec.and(OperationSpecifications.numberEquals(criteria.getNumber()));
        }

        if (criteria.getYear() != null) {
            spec = spec.and(OperationSpecifications.yearEquals(criteria.getYear()));
        }

        if (criteria.getDateTime() != null) {
            // Parse the string to Instant
            Instant dateTime = Instant.parse(criteria.getDateTime());
            // Convert Instant to LocalDate
            LocalDate date = dateTime.atZone(ZoneId.systemDefault()).toLocalDate();

            spec = spec.and(OperationSpecifications.dateTimeEquals(date));
        }

        return spec;
    }
}
