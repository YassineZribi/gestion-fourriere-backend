package com.yz.pferestapi.repository;

import com.yz.pferestapi.entity.MeasurementUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long>, JpaSpecificationExecutor<MeasurementUnit> {
    Boolean existsByNameIgnoreCaseAndSymbolIgnoreCase(String name, String symbol);

    Boolean existsByNameIgnoreCaseAndSymbolIgnoreCaseAndIdNot(String name, String symbol, Long id);
}
