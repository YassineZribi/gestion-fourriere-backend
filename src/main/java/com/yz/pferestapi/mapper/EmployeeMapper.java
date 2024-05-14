package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.EmployeeDto;
import com.yz.pferestapi.dto.UpsertEmployeeDto;
import com.yz.pferestapi.entity.Employee;

import java.util.ArrayList;

public class EmployeeMapper {
    public static Employee toEntity(UpsertEmployeeDto upsertEmployeeDto, Employee manager) {
        Employee employee = new Employee();
        return map(upsertEmployeeDto, manager, employee);
    }

    public static Employee toEntity(UpsertEmployeeDto upsertEmployeeDto, Employee manager, Employee employee) {
        return map(upsertEmployeeDto, manager, employee);
    }

    private static Employee map(UpsertEmployeeDto upsertEmployeeDto, Employee manager, Employee employee) {
        employee.setFirstName(upsertEmployeeDto.getFirstName());
        employee.setLastName(upsertEmployeeDto.getLastName());
        employee.setPosition(upsertEmployeeDto.getPosition());
        employee.setManager(manager);
        return employee;
    }

    public static EmployeeDto toDto(Employee employee) {
        return EmployeeDto.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .position(employee.getPosition())
                .photoPath(employee.getPhotoPath())
                .subordinates(new ArrayList<>())
                .build();
    };
}
