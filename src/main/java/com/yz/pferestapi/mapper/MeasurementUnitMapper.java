package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertMeasurementUnitDto;
import com.yz.pferestapi.entity.MeasurementUnit;

public class MeasurementUnitMapper {
    public static MeasurementUnit toEntity(UpsertMeasurementUnitDto upsertMeasurementUnitDto) {
        MeasurementUnit measurementUnit = new MeasurementUnit();
        return map(upsertMeasurementUnitDto, measurementUnit);
    }

    public static MeasurementUnit toEntity(UpsertMeasurementUnitDto upsertMeasurementUnitDto, MeasurementUnit measurementUnit) {
        return map(upsertMeasurementUnitDto, measurementUnit);
    }

    private static MeasurementUnit map(UpsertMeasurementUnitDto upsertMeasurementUnitDto, MeasurementUnit measurementUnit) {
        measurementUnit.setName(upsertMeasurementUnitDto.getName());
        measurementUnit.setSymbol(upsertMeasurementUnitDto.getSymbol());
        return measurementUnit;
    }
}
