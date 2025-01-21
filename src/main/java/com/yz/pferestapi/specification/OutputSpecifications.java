package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Output;
import com.yz.pferestapi.util.NumberUtil;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class OutputSpecifications {
    public static Specification<Output> discountIs(Boolean discount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("discount"), discount);
    }

    public static Specification<Output> totalPaymentAmountContains(Double totalPaymentAmount) {
        return (root, query, criteriaBuilder) -> {
            String totalPaymentAmountString = NumberUtil.format(totalPaymentAmount);
            return criteriaBuilder.like(root.get("totalPaymentAmount").as(String.class), "%" + totalPaymentAmountString + "%");
        };
    }

    public static Specification<Output> totalPaymentAmountStartsWith(Double totalPaymentAmount) {
        return (root, query, criteriaBuilder) -> {
            String totalPaymentAmountString = NumberUtil.format(totalPaymentAmount);
            return criteriaBuilder.like(root.get("totalPaymentAmount").as(String.class), totalPaymentAmountString + "%");
        };
    }

    public static Specification<Output> receiptNumberContains(Integer receiptNumber) {
        return (root, query, criteriaBuilder) -> {
            String receiptNumberString = String.valueOf(receiptNumber);
            System.out.println("receiptNumberString = " + receiptNumberString);
            return criteriaBuilder.like(root.get("receiptNumber").as(String.class), "%" + receiptNumberString + "%");
        };
    }

    public static Specification<Output> receiptNumberStartsWith(Integer receiptNumber) {
        return (root, query, criteriaBuilder) -> {
            String receiptNumberString = String.valueOf(receiptNumber);
            return criteriaBuilder.like(root.get("receiptNumber").as(String.class), receiptNumberString + "%");
        };
    }

    public static Specification<Output> hasRegister(Long registerId) {
        return (root, query, builder) ->
                builder.equal(root.join("input").join("register").get("id"), registerId);
    }

    public static Specification<Output> isWithinDateRange(Date startDate, Date endDate) {
        return (root, query, builder) ->
                builder.between(root.get("dateTime"), startDate.toInstant(), endDate.toInstant());
    }

    public static Specification<Output> hasSource(Long sourceId) {
        return (root, query, builder) ->
                builder.equal(root.join("input").join("source").get("id"), sourceId);
    }
}
