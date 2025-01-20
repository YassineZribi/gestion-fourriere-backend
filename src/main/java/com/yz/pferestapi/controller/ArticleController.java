package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.ArticleCriteriaRequest;
import com.yz.pferestapi.dto.UpsertArticleDto;
import com.yz.pferestapi.entity.Article;
import com.yz.pferestapi.service.ArticleService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping("/articles")
@RestController
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<Article>> getAllArticlesByName(@RequestParam(required = false, defaultValue = "") String name) {
        List<Article> articles = articleService.findArticlesByName(name);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        Article article = articleService.getArticle(id);
        return ResponseEntity.ok(article);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Article>> findArticlesByCriteria(ArticleCriteriaRequest articleCriteria) {
        System.out.println("articleCriteria = " + articleCriteria);
        Page<Article> articles = articleService.findArticlesByCriteria(articleCriteria);
        return ResponseEntity.ok(articles);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Article> addArticle(@Validated @ModelAttribute UpsertArticleDto upsertArticleDto)
            throws IOException {
        Article article = articleService.createArticle(upsertArticleDto);
        return ResponseEntity.ok(article);
    }

    @PatchMapping(value="/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Article> updateArticle(@Validated @ModelAttribute UpsertArticleDto upsertArticleDto,
                                                 @PathVariable("id") Long articleId) throws IOException {
        Article article = articleService.updateArticle(upsertArticleDto, articleId);
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteArticle(@PathVariable("id") Long articleId) throws IOException {
        articleService.deleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }
}
