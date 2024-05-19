package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertArticleDto;
import com.yz.pferestapi.entity.Article;
import com.yz.pferestapi.entity.ArticleFamily;

public class ArticleMapper {
    public static Article toEntity(UpsertArticleDto upsertArticleDto, ArticleFamily articleFamily) {
        Article article = new Article();
        return map(upsertArticleDto, articleFamily, article);
    }

    public static Article toEntity(UpsertArticleDto upsertArticleDto, ArticleFamily articleFamily, Article article) {
        return map(upsertArticleDto, articleFamily, article);
    }

    private static Article map(UpsertArticleDto upsertArticleDto, ArticleFamily articleFamily, Article article) {
        article.setName(upsertArticleDto.getName());
        article.setTransportFee(upsertArticleDto.getTransportFee());
        article.setArticleFamily(articleFamily);
        return article;
    }
}
