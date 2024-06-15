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
        if (criteria.getStartNumber() != null) {
            spec = spec.and(OperationSpecifications.numberFrom(criteria.getStartNumber()));
        }
        if (criteria.getEndNumber() != null) {
            spec = spec.and(OperationSpecifications.numberTo(criteria.getEndNumber()));
        }

        if (criteria.getYear() != null) {
            spec = spec.and(OperationSpecifications.yearEquals(criteria.getYear()));
        }
        if (criteria.getStartYear() != null) {
            spec = spec.and(OperationSpecifications.yearFrom(criteria.getStartYear()));
        }
        if (criteria.getEndYear() != null) {
            spec = spec.and(OperationSpecifications.yearTo(criteria.getEndYear()));
        }

        if (criteria.getDateTime() != null) {
            // Parse the string to Instant
            Instant dateTime = Instant.parse(criteria.getDateTime());
            // Convert Instant to LocalDate
            LocalDate date = dateTime.atZone(ZoneId.systemDefault()).toLocalDate();

            spec = spec.and(OperationSpecifications.dateTimeEquals(date));
        }

        if (criteria.getStartDate() != null) {
            // Parse the string to Instant
            Instant startInstant = Instant.parse(criteria.getStartDate());
            // Convert Instant to LocalDate
            LocalDate startDate = startInstant.atZone(ZoneId.systemDefault()).toLocalDate();

            spec = spec.and(OperationSpecifications.dateFrom(startDate));
        }

        if (criteria.getEndDate() != null) {
            // Parse the string to Instant
            Instant endInstant = Instant.parse(criteria.getEndDate());
            // Convert Instant to LocalDate
            LocalDate endDate = endInstant.atZone(ZoneId.systemDefault()).toLocalDate();

            spec = spec.and(OperationSpecifications.dateTo(endDate));
        }

        return spec;
    }
}
