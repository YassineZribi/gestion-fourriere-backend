package com.yz.pferestapi.service;

import com.yz.pferestapi.dto.UpsertWarehouseDto;
import com.yz.pferestapi.dto.WarehouseCriteriaRequest;
import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.entity.Warehouse;
import com.yz.pferestapi.exception.AppException;
import com.yz.pferestapi.mapper.WarehouseMapper;
import com.yz.pferestapi.repository.EmployeeRepository;
import com.yz.pferestapi.repository.WarehouseRepository;
import com.yz.pferestapi.specification.WarehouseSpecifications;
import com.yz.pferestapi.util.CriteriaRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final EmployeeRepository employeeRepository;

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAllByDeletedIsFalse();

    }

    public Page<Warehouse> findWarehousesByCriteria(WarehouseCriteriaRequest warehouseCriteria) {
        Specification<Warehouse> spec = Specification.where(null);

        // Get not deleted warehouses only
        spec = spec.and(WarehouseSpecifications.isNotDeleted());

        // Get warehouses having a specific manager
        if (warehouseCriteria.getManagerId() != null) {
            spec = spec.and(WarehouseSpecifications.hasManager(warehouseCriteria.getManagerId()));
        }

        if (warehouseCriteria.getName() != null) {
            spec = spec.and(WarehouseSpecifications.nameContains(warehouseCriteria.getName()));
        }

        if (warehouseCriteria.getAddress() != null) {
            spec = spec.and(WarehouseSpecifications.addressContains(warehouseCriteria.getAddress()));
        }

        if (warehouseCriteria.getLatitude() != null) {
            spec = spec.and(WarehouseSpecifications.latitudeStartsWith(warehouseCriteria.getLatitude()));
        }

        if (warehouseCriteria.getLongitude() != null) {
            spec = spec.and(WarehouseSpecifications.longitudeStartsWith(warehouseCriteria.getLongitude()));
        }

        Sort sort = CriteriaRequestUtil.buildSortCriteria(warehouseCriteria);

        Pageable pageable = CriteriaRequestUtil.createPageable(warehouseCriteria, sort);

        return warehouseRepository.findAll(spec, pageable);
    }

    public Warehouse createWarehouse(UpsertWarehouseDto upsertWarehouseDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee createdByEmployee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Employee manager = null;
        if (upsertWarehouseDto.getManagerId() != null) {
            manager = employeeRepository.findById(upsertWarehouseDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        Warehouse warehouse = WarehouseMapper.toEntity(upsertWarehouseDto, manager);

        warehouse.setCreatedByEmployee(createdByEmployee);
        warehouse.setLastUpdatedByEmployee(createdByEmployee);
        return warehouseRepository.save(warehouse);
    }

    public Warehouse updateWarehouse(UpsertWarehouseDto upsertWarehouseDto, Long warehouseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee updatedByEmployee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Warehouse warehouse = warehouseRepository.findByDeletedIsFalseAndId(warehouseId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Warehouse not found"));

        Employee manager = null;
        if (upsertWarehouseDto.getManagerId() != null) {
            manager = employeeRepository.findById(upsertWarehouseDto.getManagerId())
                    .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Employee (Manager) not found"));
        }

        Warehouse mappedWarehouse = WarehouseMapper.toEntity(upsertWarehouseDto, manager, warehouse);

        mappedWarehouse.setLastUpdatedByEmployee(updatedByEmployee);
        return warehouseRepository.save(mappedWarehouse);
    }

    public void deleteWarehouse(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Employee deletedByEmployee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Warehouse warehouse = warehouseRepository.findByDeletedIsFalseAndId(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "Warehouse not found"));

        warehouse.setDeleted(true);

        warehouse.setDeletedByEmployee(deletedByEmployee);
        warehouseRepository.save(warehouse);
    }
}
