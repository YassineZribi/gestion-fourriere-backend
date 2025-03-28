package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.UpsertWarehouseDto;
import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.entity.Warehouse;

public class WarehouseMapper {
    public static Warehouse toEntity(UpsertWarehouseDto upsertWarehouseDto, Employee manager) {
        Warehouse warehouse = new Warehouse();
        return map(upsertWarehouseDto, manager, warehouse);
    }

    public static Warehouse toEntity(UpsertWarehouseDto upsertWarehouseDto, Employee manager, Warehouse warehouse) {
        return map(upsertWarehouseDto, manager, warehouse);
    }

    private static Warehouse map(UpsertWarehouseDto upsertWarehouseDto, Employee manager, Warehouse warehouse) {
        warehouse.setName(upsertWarehouseDto.getName());
        warehouse.setAddress(upsertWarehouseDto.getAddress());
        warehouse.setLatitude(upsertWarehouseDto.getLatitude());
        warehouse.setLongitude(upsertWarehouseDto.getLongitude());
        warehouse.setManager(manager);
        return warehouse;
    }
}
