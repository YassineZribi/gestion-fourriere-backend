package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.ArticleFamilyCriteriaRequest;
import com.yz.pferestapi.dto.UpsertArticleFamilyDto;
import com.yz.pferestapi.entity.ArticleFamily;
import com.yz.pferestapi.service.ArticleFamilyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/article-families")
@RestController
public class ArticleFamilyController {
    private final ArticleFamilyService articleFamilyService;

    public ArticleFamilyController(ArticleFamilyService articleFamilyService) {
        this.articleFamilyService = articleFamilyService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<ArticleFamily>> getAllArticleFamiliesByName(@RequestParam(required = false, defaultValue = "") String name) {
        List<ArticleFamily> articleFamilies = articleFamilyService.findArticleFamiliesByName(name);
        return ResponseEntity.ok(articleFamilies);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ArticleFamily> getArticleFamily(@PathVariable Long id) {
        ArticleFamily articleFamily = articleFamilyService.getArticleFamily(id);
        return ResponseEntity.ok(articleFamily);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<ArticleFamily>> findArticleFamiliesByCriteria(ArticleFamilyCriteriaRequest articleFamilyCriteria) {
        System.out.println("articleFamilyCriteria = " + articleFamilyCriteria);
        Page<ArticleFamily> articleFamilies = articleFamilyService.findArticleFamiliesByCriteria(articleFamilyCriteria);
        return ResponseEntity.ok(articleFamilies);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ArticleFamily> createArticleFamily(@RequestPart("data") @Valid UpsertArticleFamilyDto upsertArticleFamilyDto,
                                                             @RequestPart(value = "media", required = false) MultipartFile photoFile) throws IOException {
        ArticleFamily articleFamily = articleFamilyService.createArticleFamily(upsertArticleFamilyDto, photoFile);
        return ResponseEntity.ok(articleFamily);
    }

    @PatchMapping(value="/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ArticleFamily> updateArticleFamily(@RequestPart("data") @Valid UpsertArticleFamilyDto upsertArticleFamilyDto,
                                                   @RequestPart(value = "media", required = false) MultipartFile photoFile,
                                                   @PathVariable("id") Long articleFamilyId) throws IOException {
        ArticleFamily articleFamily = articleFamilyService.updateArticleFamily(upsertArticleFamilyDto, articleFamilyId, photoFile);
        return ResponseEntity.ok(articleFamily);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteArticleFamily(@PathVariable("id") Long articleFamilyId) throws IOException {
        articleFamilyService.deleteArticleFamily(articleFamilyId);
        return ResponseEntity.noContent().build();
    }
}
