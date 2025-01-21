package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.InputOperationLine;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class InputOperationLineSpecifications {
    public static Specification<InputOperationLine> hasArticleFamily(Long articleFamilyId) {
        return (root, query, builder) ->
                builder.equal(root.join("article").join("articleFamily").get("id"), articleFamilyId);
    }

    public static Specification<InputOperationLine> hasRegister(Long registerId) {
        return (root, query, builder) ->
                builder.equal(root.join("input").join("register").get("id"), registerId);
    }

    public static Specification<InputOperationLine> isWithinDateRange(Date startDate, Date endDate) {
        return (root, query, builder) ->
                builder.between(root.join("input").get("dateTime"), startDate.toInstant(), endDate.toInstant());
    }

    public static Specification<InputOperationLine> hasSource(Long sourceId) {
        return (root, query, builder) ->
                builder.equal(root.join("input").join("source").get("id"), sourceId);
    }
}
