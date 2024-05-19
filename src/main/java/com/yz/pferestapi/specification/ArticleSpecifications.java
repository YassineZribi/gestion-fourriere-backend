package com.yz.pferestapi.specification;

import com.yz.pferestapi.entity.Article;
import com.yz.pferestapi.entity.ArticleFamily;
import com.yz.pferestapi.util.NumberUtil;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecifications {
    public static Specification<Article> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
    }

    public static Specification<Article> transportFeeContains(Double transportFee) {
        return (root, query, criteriaBuilder) -> {
            String transportFeeString = NumberUtil.format(transportFee);
            return criteriaBuilder.like(root.get("transportFee").as(String.class), "%" + transportFeeString + "%");
        };
    }

    public static Specification<Article> transportFeeStartsWith(Double transportFee) {
        return (root, query, criteriaBuilder) -> {
            String transportFeeString = NumberUtil.format(transportFee);
            return criteriaBuilder.like(root.get("transportFee").as(String.class), transportFeeString + "%");
        };
    }

    public static Specification<Article> hasArticleFamily(Long articleFamilyId) {
        return (root, query, criteriaBuilder) -> {
            Join<Article, ArticleFamily> articleFamilyJoin = root.join("articleFamily");
            return criteriaBuilder.equal(articleFamilyJoin.get("id"), articleFamilyId);
        };
    }
}
