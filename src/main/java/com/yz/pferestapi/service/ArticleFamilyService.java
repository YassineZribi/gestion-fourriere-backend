package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.ArticleFamilyCriteriaRequest;
import com.yz.pferestapi.dto.UpsertArticleFamilyDto;
import com.yz.pferestapi.entity.ArticleFamily;
import com.yz.pferestapi.entity.MeasurementUnit;
import com.yz.pferestapi.entity.Register;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.ArticleFamilyMapper;
import com.yz.pferestapi.repository.ArticleFamilyRepository;
import com.yz.pferestapi.repository.MeasurementUnitRepository;
import com.yz.pferestapi.repository.RegisterRepository;
import com.yz.pferestapi.specification.ArticleFamilySpecifications;
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
public class ArticleFamilyService {
    private static final String ARTICLE_FAMILIES_PHOTOS_SUB_FOLDER_NAME = "article-families-photos";
    private final ArticleFamilyRepository articleFamilyRepository;
    private final RegisterRepository registerRepository;
    private final MeasurementUnitRepository measurementUnitRepository;
    private final FileService fileService;

    public List<ArticleFamily> getAllArticleFamilies() {
        return articleFamilyRepository.findAll();
    }

    public ArticleFamily getArticleFamily(Long articleFamilyId) {
        return articleFamilyRepository.findById(articleFamilyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article family not found"));
    }

    public List<ArticleFamily> findArticleFamiliesByName(String search) {
        Specification<ArticleFamily> spec = Specification.where(null);

        spec = spec.and(ArticleFamilySpecifications.nameContains(search));

        return articleFamilyRepository.findAll(spec);
    }

    public Page<ArticleFamily> findArticleFamiliesByCriteria(ArticleFamilyCriteriaRequest articleFamilyCriteria) {
        Specification<ArticleFamily> spec = Specification.where(null);

        // Get article families belong to a specific register
        if (articleFamilyCriteria.getRegisterId() != null) {
            spec = spec.and(ArticleFamilySpecifications.hasRegister(articleFamilyCriteria.getRegisterId()));
        }

        // Get article families having a specific measurement unit
        if (articleFamilyCriteria.getMeasurementUnitId() != null) {
            spec = spec.and(ArticleFamilySpecifications.hasMeasurementUnit(articleFamilyCriteria.getMeasurementUnitId()));
        }

        if (articleFamilyCriteria.getName() != null) {
            spec = spec.and(ArticleFamilySpecifications.nameContains(articleFamilyCriteria.getName()));
        }

        if (articleFamilyCriteria.getDescription() != null) {
            spec = spec.and(ArticleFamilySpecifications.descriptionContains(articleFamilyCriteria.getDescription()));
        }

        if (articleFamilyCriteria.getNightlyAmount() != null) {
            spec = spec.and(ArticleFamilySpecifications.nightlyAmountStartsWith(articleFamilyCriteria.getNightlyAmount()));
        }

        if (articleFamilyCriteria.getUnitCalculation() != null) {
            spec = spec.and(ArticleFamilySpecifications.unitCalculationIs(articleFamilyCriteria.getUnitCalculation()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(articleFamilyCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(articleFamilyCriteria, sort);

        return articleFamilyRepository.findAll(spec, pageable);
    }

    public ArticleFamily createArticleFamily(UpsertArticleFamilyDto upsertArticleFamilyDto, MultipartFile photoFile) throws IOException {
        if (articleFamilyRepository.existsByNameIgnoreCase(upsertArticleFamilyDto.getName())) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Register register = null;
        if (upsertArticleFamilyDto.getRegisterId() != null) {
            register = registerRepository.findById(upsertArticleFamilyDto.getRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));
        }

        MeasurementUnit measurementUnit = null;
        if (upsertArticleFamilyDto.getMeasurementUnitId() != null) {
            measurementUnit = measurementUnitRepository.findById(upsertArticleFamilyDto.getMeasurementUnitId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Measurement unit not found"));
        }

        ArticleFamily articleFamily = ArticleFamilyMapper.toEntity(upsertArticleFamilyDto, register, measurementUnit);

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.uploadFile(photoFile, ARTICLE_FAMILIES_PHOTOS_SUB_FOLDER_NAME);
            articleFamily.setPhotoPath(photoPath);
        }

        return articleFamilyRepository.save(articleFamily);
    }

    public ArticleFamily updateArticleFamily(UpsertArticleFamilyDto upsertArticleFamilyDto, Long articleFamilyId, MultipartFile photoFile) throws IOException {
        ArticleFamily articleFamily = articleFamilyRepository.findById(articleFamilyId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article family not found"));

        if (articleFamilyRepository.existsByNameIgnoreCaseAndIdNot(upsertArticleFamilyDto.getName(), articleFamilyId)) {
            throw new AppException(HttpStatus.CONFLICT, "Name already exists!");
        }

        Register register = null;
        if (upsertArticleFamilyDto.getRegisterId() != null) {
            register = registerRepository.findById(upsertArticleFamilyDto.getRegisterId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Register not found"));
        }

        MeasurementUnit measurementUnit = null;
        if (upsertArticleFamilyDto.getMeasurementUnitId() != null) {
            measurementUnit = measurementUnitRepository.findById(upsertArticleFamilyDto.getMeasurementUnitId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Measurement unit not found"));
        }

        ArticleFamily updatedArticleFamily = ArticleFamilyMapper.toEntity(upsertArticleFamilyDto, register, measurementUnit, articleFamily);

        if (photoFile != null && !photoFile.isEmpty()) {
            String photoPath = fileService.uploadFile(photoFile, ARTICLE_FAMILIES_PHOTOS_SUB_FOLDER_NAME);

            String oldPhotoPath = articleFamily.getPhotoPath();
            if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                fileService.deleteFile(oldPhotoPath);
            }

            updatedArticleFamily.setPhotoPath(photoPath);
        }

        return articleFamilyRepository.save(updatedArticleFamily);
    }

    public void deleteArticleFamily(Long id) throws IOException {
        ArticleFamily articleFamily = articleFamilyRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Article family not found"));

        // TODO: choisir un des deux cas
        // cas 1: La suppression d'un article family entrainnera la suppression de la référence de tous ses articles -> exemple "EmployeeService"
        // cas 2: On peut pas supprimer un article family rérérencé par au moins un article -> exemple "RegisterService"

        articleFamilyRepository.delete(articleFamily);

        String photoPath = articleFamily.getPhotoPath();
        System.out.println("photoPath = " + photoPath);
        if (photoPath != null && !photoPath.isEmpty()) {
            fileService.deleteFile(photoPath);
        }
    }
}
