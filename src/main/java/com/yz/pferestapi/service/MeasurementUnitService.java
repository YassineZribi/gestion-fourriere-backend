package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.MeasurementUnitCriteriaRequest;
import com.yz.pferestapi.dto.UpsertMeasurementUnitDto;
import com.yz.pferestapi.entity.MeasurementUnit;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.MeasurementUnitMapper;
import com.yz.pferestapi.repository.MeasurementUnitRepository;
import com.yz.pferestapi.specification.MeasurementUnitSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementUnitService {
    private  final MeasurementUnitRepository measurementUnitRepository;

    public MeasurementUnit getMeasurementUnit(Long measurementUnitId) {
        return measurementUnitRepository.findById(measurementUnitId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Measurement unit not found"));
    }

    public List<MeasurementUnit> findMeasurementUnitsByNameOrSymbol(String search) {
        Specification<MeasurementUnit> spec = Specification.where(null);

        spec = spec.or(MeasurementUnitSpecifications.nameContains(search))
                .or(MeasurementUnitSpecifications.symbolContains(search));

        return measurementUnitRepository.findAll(spec);
    }

    public Page<MeasurementUnit> findMeasurementUnitsByCriteria(MeasurementUnitCriteriaRequest measurementUnitCriteria) {
        Specification<MeasurementUnit> spec = Specification.where(null);

        if (measurementUnitCriteria.getName() != null) {
            spec = spec.and(MeasurementUnitSpecifications.nameContains(measurementUnitCriteria.getName()));
        }

        if (measurementUnitCriteria.getSymbol() != null) {
            spec = spec.and(MeasurementUnitSpecifications.symbolContains(measurementUnitCriteria.getSymbol()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(measurementUnitCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(measurementUnitCriteria, sort);

        return measurementUnitRepository.findAll(spec, pageable);
    }

    public MeasurementUnit createMeasurementUnit(UpsertMeasurementUnitDto upsertMeasurementUnitDto) {
        if (measurementUnitRepository.existsByNameIgnoreCaseAndSymbolIgnoreCase(upsertMeasurementUnitDto.getName(), upsertMeasurementUnitDto.getSymbol())) {
            throw new AppException(HttpStatus.CONFLICT, "Measurement unit already exists!");
        }

        MeasurementUnit measurementUnit = MeasurementUnitMapper.toEntity(upsertMeasurementUnitDto);

        return measurementUnitRepository.save(measurementUnit);
    }

    public MeasurementUnit updateMeasurementUnit(UpsertMeasurementUnitDto upsertMeasurementUnitDto, Long measurementUnitId) {
        MeasurementUnit measurementUnit = measurementUnitRepository.findById(measurementUnitId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Measurement unit not found"));

        if (measurementUnitRepository.existsByNameIgnoreCaseAndSymbolIgnoreCaseAndIdNot(upsertMeasurementUnitDto.getName(), upsertMeasurementUnitDto.getSymbol(), measurementUnitId)) {
            throw new AppException(HttpStatus.CONFLICT, "Measurement unit already exists!");
        }

        MeasurementUnit updatedMeasurementUnit = MeasurementUnitMapper.toEntity(upsertMeasurementUnitDto, measurementUnit);

        return measurementUnitRepository.save(updatedMeasurementUnit);
    }

    public void deleteMeasurementUnit(Long id) {
        MeasurementUnit measurementUnit = measurementUnitRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Measurement unit not found"));

        // TODO: for articleFamily (plural: article families)
        /*
        if (articleFamilyRepository.existsByMeasurementUnitId(id)) {
            throw new AppException(HttpStatus.CONFLICT, "It is not possible to delete a Measurement unit that is used in Article family.");
        }
         */

        measurementUnitRepository.delete(measurementUnit);
    }
}
