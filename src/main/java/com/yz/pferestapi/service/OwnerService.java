package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.OwnerCriteriaRequest;
import com.yz.pferestapi.entity.Owner;
import com.yz.pferestapi.specification.OwnerSpecifications;
import com.yz.pferestapi.specification.RegisterSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {
    public static <T extends OwnerCriteriaRequest, U extends Owner> Specification<U> filter(T criteria, Specification<U> spec) {
        if (criteria.getAddress() != null) {
            spec = spec.and(OwnerSpecifications.addressContains(criteria.getAddress()));
        }

        if (criteria.getEmail() != null) {
            spec = spec.and(OwnerSpecifications.emailContains(criteria.getEmail()));
        }

        if (criteria.getPhoneNumber() != null) {
            spec = spec.and(OwnerSpecifications.phoneNumberContains(criteria.getPhoneNumber()));
        }

        return spec;
    }
}
