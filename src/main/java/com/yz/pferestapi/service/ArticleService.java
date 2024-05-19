package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.ArticleCriteriaRequest;
import com.yz.pferestapi.dto.UpsertArticleDto;
import com.yz.pferestapi.entity.Article;
import com.yz.pferestapi.entity.ArticleFamily;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.ArticleMapper;
import com.yz.pferestapi.repository.ArticleFamilyRepository;
import com.yz.pferestapi.repository.ArticleRepository;
import com.yz.pferestapi.specification.ArticleSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private static final String ARTICLES_PHOTOS_SUB_FOLDER_NAME = "articles-photos";
    private final ArticleRepository articleRepository;
    private final ArticleFamilyRepository articleFamilyRepository;
    private final FileService fileService;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));
    }

    public List<Article> findArticlesByName(String search) {
        Specification<Article> spec = Specification.where(null);

        spec = spec.and(ArticleSpecifications.nameContains(search));

        return articleRepository.findAll(spec);
    }

    public Page<Article> findArticlesByCriteria(ArticleCriteriaRequest articleCriteria) {
        Specification<Article> spec = Specification.where(null);

        // Get articles belong to a specific article family
        if (articleCriteria.getArticleFamilyId() != null) {
            spec = spec.and(ArticleSpecifications.hasArticleFamily(articleCriteria.getArticleFamilyId()));
        }

        if (articleCriteria.getName() != null) {
            spec = spec.and(ArticleSpecifications.nameContains(articleCriteria.getName()));
        }

        if (articleCriteria.getTransportFee() != null) {
            spec = spec.and(ArticleSpecifications.transportFeeStartsWith(articleCriteria.getTransportFee()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(articleCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(articleCriteria, sort);

        return articleRepository.findAll(spec, pageable);
    }

    public Article createArticle(UpsertArticleDto upsertArticleDto, MultipartFile photoFile) throws IOException {
        ArticleFamily articleFamily = null;
        if (upsertArticleDto.getArticleFamilyId() != null) {
            articleFamily = articleFamilyRepository.findById(upsertArticleDto.getArticleFamilyId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article family not found"));
        }

        Article article = ArticleMapper.toEntity(upsertArticleDto, articleFamily);

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.uploadFile(photoFile, ARTICLES_PHOTOS_SUB_FOLDER_NAME);
            article.setPhotoPath(photoPath);
        }

        return articleRepository.save(article);
    }

    public Article updateArticle(UpsertArticleDto upsertArticleDto, Long articleId, MultipartFile photoFile) throws IOException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));

        ArticleFamily articleFamily = null;
        if (upsertArticleDto.getArticleFamilyId() != null) {
            articleFamily = articleFamilyRepository.findById(upsertArticleDto.getArticleFamilyId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article family not found"));
        }

        Article updatedArticle = ArticleMapper.toEntity(upsertArticleDto, articleFamily, article);

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.uploadFile(photoFile, ARTICLES_PHOTOS_SUB_FOLDER_NAME);

            String oldPhotoPath = article.getPhotoPath();
            if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                fileService.deleteFile(oldPhotoPath);
            }

            updatedArticle.setPhotoPath(photoPath);
        }

        return articleRepository.save(updatedArticle);
    }

    public void deleteArticle(Long id) throws IOException {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article not found"));

        articleRepository.delete(article);

        String photoPath = article.getPhotoPath();
        System.out.println("photoPath = " + photoPath);
        if (photoPath != null && !photoPath.isEmpty()) {
            fileService.deleteFile(photoPath);
        }
    }
}
