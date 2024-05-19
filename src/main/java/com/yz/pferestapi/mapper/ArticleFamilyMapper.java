package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertArticleFamilyDto;
import com.yz.pferestapi.entity.*;

public class ArticleFamilyMapper {
    public static ArticleFamily toEntity(UpsertArticleFamilyDto upsertArticleFamilyDto, Register register, MeasurementUnit measurementUnit) {
        ArticleFamily articleFamily = new ArticleFamily();
        return map(upsertArticleFamilyDto, register, measurementUnit, articleFamily);
    }

    public static ArticleFamily toEntity(UpsertArticleFamilyDto upsertArticleFamilyDto, Register register, MeasurementUnit measurementUnit, ArticleFamily articleFamily) {
        return map(upsertArticleFamilyDto, register, measurementUnit, articleFamily);
    }

    private static ArticleFamily map(UpsertArticleFamilyDto upsertArticleFamilyDto, Register register, MeasurementUnit measurementUnit, ArticleFamily articleFamily) {
        articleFamily.setName(upsertArticleFamilyDto.getName());
        articleFamily.setDescription(upsertArticleFamilyDto.getDescription());
        articleFamily.setNightlyAmount(upsertArticleFamilyDto.getNightlyAmount());
        articleFamily.setUnitCalculation(upsertArticleFamilyDto.getUnitCalculation());
        articleFamily.setRegister(register);
        articleFamily.setMeasurementUnit(measurementUnit);
        return articleFamily;
    }
}
