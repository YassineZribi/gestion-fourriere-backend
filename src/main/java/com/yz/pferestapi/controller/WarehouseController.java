package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.UpsertWarehouseDto;
import com.yz.pferestapi.dto.WarehouseCriteriaRequest;
import com.yz.pferestapi.entity.Warehouse;
import com.yz.pferestapi.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/warehouses")
@RestController
@PreAuthorize("isAuthenticated()")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

//    @GetMapping
//    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
//        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
//        return ResponseEntity.ok(warehouses);
//    }

    @GetMapping
    public ResponseEntity<Page<Warehouse>> findWarehousesByCriteria(WarehouseCriteriaRequest warehouseCriteria) {
        System.out.println("warehouseCriteria = " + warehouseCriteria);
        Page<Warehouse> warehouses = warehouseService.findWarehousesByCriteria(warehouseCriteria);
        return ResponseEntity.ok(warehouses);
    }

    @PostMapping
    public ResponseEntity<Warehouse> createWarehouse(@Valid @RequestBody UpsertWarehouseDto upsertWarehouseDto) {
        Warehouse warehouse = warehouseService.createWarehouse(upsertWarehouseDto);
        return ResponseEntity.ok(warehouse);
    }

    @PatchMapping(value="/{id}")
    public ResponseEntity<Warehouse> updateWarehouse(@Valid @RequestBody UpsertWarehouseDto upsertWarehouseDto,
                                                   @PathVariable("id") Long warehouseId) {
        Warehouse warehouse = warehouseService.updateWarehouse(upsertWarehouseDto, warehouseId);
        return ResponseEntity.ok(warehouse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable("id") Long warehouseId) {
        warehouseService.deleteWarehouse(warehouseId);
        return ResponseEntity.noContent().build();
    }
}
