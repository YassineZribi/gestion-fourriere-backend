package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.MeasurementUnitCriteriaRequest;
import com.yz.pferestapi.dto.UpsertMeasurementUnitDto;
import com.yz.pferestapi.entity.MeasurementUnit;
import com.yz.pferestapi.service.MeasurementUnitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/measurement-units")
@RestController
public class MeasurementUnitController {
    private  final MeasurementUnitService measurementUnitService;

    public MeasurementUnitController(MeasurementUnitService measurementUnitService) {
        this.measurementUnitService = measurementUnitService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<MeasurementUnit>> getAllMeasurementUnitsByNameOrSymbol(@RequestParam(required = false, defaultValue = "") String value) {
        List<MeasurementUnit> measurementUnits = measurementUnitService.findMeasurementUnitsByNameOrSymbol(value);
        return ResponseEntity.ok(measurementUnits);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MeasurementUnit> getMeasurementUnit(@PathVariable Long id) {
        System.out.println("measurementUnitId = " + id);
        MeasurementUnit measurementUnit = measurementUnitService.getMeasurementUnit(id);
        return ResponseEntity.ok(measurementUnit);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<MeasurementUnit>> findMeasurementUnitsByCriteria(MeasurementUnitCriteriaRequest measurementUnitCriteria) {
        System.out.println("measurementUnitCriteria = " + measurementUnitCriteria);
        Page<MeasurementUnit> measurementUnits = measurementUnitService.findMeasurementUnitsByCriteria(measurementUnitCriteria);
        return ResponseEntity.ok(measurementUnits);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MeasurementUnit> createMeasurementUnit(@Valid @RequestBody UpsertMeasurementUnitDto upsertMeasurementUnitDto) {
        MeasurementUnit createdMeasurementUnit = measurementUnitService.createMeasurementUnit(upsertMeasurementUnitDto);
        return new ResponseEntity<>(createdMeasurementUnit, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<MeasurementUnit> updateMeasurementUnit(@Valid @RequestBody UpsertMeasurementUnitDto upsertMeasurementUnitDto, @PathVariable Long id) {
        MeasurementUnit updatedMeasurementUnit = measurementUnitService.updateMeasurementUnit(upsertMeasurementUnitDto, id);
        return ResponseEntity.ok(updatedMeasurementUnit);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteMeasurementUnit(@PathVariable Long id) {
        measurementUnitService.deleteMeasurementUnit(id);
        return ResponseEntity.noContent().build();
    }
}
