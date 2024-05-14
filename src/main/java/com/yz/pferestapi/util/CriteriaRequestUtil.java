package com.yz.pferestapi.util;

import com.yz.pferestapi.dto.CriteriaRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

public class CriteriaRequestUtil {
    public static <T extends CriteriaRequest> Sort buildSortCriteria(T criteria) {
        // If no sorting criteria provided by the client, sort by "createdAt" DESC by default
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        if (criteria.getSort() != null && !criteria.getSort().isEmpty()) {
            List<Sort.Order> orders = criteria.getSort().stream()
                    .map(param -> {
                        String[] parts = param.split("-");
                        String property = parts[0];
                        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
                        return new Sort.Order(direction, property);
                    })
                    .collect(Collectors.toList());
            sort = Sort.by(orders).and(sort); // orders will be the primary sorting criteria, and sort object will be the secondary sorting criterion.
        }

        return sort;
    }

    public static <T extends CriteriaRequest> Pageable createPageable(T criteria, Sort sort) {
        return PageRequest.of(criteria.getPage() - 1, criteria.getSize(), sort);
    }
}
