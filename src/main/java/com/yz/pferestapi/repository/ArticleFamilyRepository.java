package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.ArticleFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleFamilyRepository extends JpaRepository<ArticleFamily, Long>, JpaSpecificationExecutor<ArticleFamily> {
    Boolean existsByNameIgnoreCase(String name);

    Boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Boolean existsByMeasurementUnitId(Long id);
}
