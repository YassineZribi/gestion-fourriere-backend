package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.OutputOperationLine;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class OutputOperationLineSpecifications {
    public static Specification<OutputOperationLine> hasArticleFamily(Long articleFamilyId) {
        return (root, query, builder) ->
                builder.equal(root.join("article").join("articleFamily").get("id"), articleFamilyId);
    }

    public static Specification<OutputOperationLine> hasRegister(Long registerId) {
        return (root, query, builder) ->
                builder.equal(root.join("output").join("input").join("register").get("id"), registerId);
    }

    public static Specification<OutputOperationLine> isWithinDateRange(Date startDate, Date endDate) {
        return (root, query, builder) ->
                builder.between(root.join("output").get("dateTime"), startDate.toInstant(), endDate.toInstant());
    }

    public static Specification<OutputOperationLine> hasSource(Long sourceId) {
        return (root, query, builder) ->
                builder.equal(root.join("output").join("input").join("source").get("id"), sourceId);
    }
}
