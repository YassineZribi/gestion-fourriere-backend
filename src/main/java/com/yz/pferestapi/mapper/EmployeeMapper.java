package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.EmployeeWithSubordinatesDto;
import com.yz.pferestapi.dto.UpsertEmployeeDto;
import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.entity.Role;

import java.util.ArrayList;

public class EmployeeMapper {
    public static Employee toEntity(UpsertEmployeeDto upsertEmployeeDto, Role role, Employee manager) {
        Employee employee = new Employee();
        return map(upsertEmployeeDto, employee, role, manager);
    }

    public static Employee toEntity(UpsertEmployeeDto upsertEmployeeDto, Employee employee, Role role, Employee manager) {
        return map(upsertEmployeeDto, employee, role, manager);
    }

    private static Employee map(UpsertEmployeeDto upsertEmployeeDto, Employee employee, Role role, Employee manager) {
        // map user properties
        Employee mappedEmployee = UserMapper.toEntity(upsertEmployeeDto, employee, role);

        mappedEmployee.setPosition(upsertEmployeeDto.getPosition());
        mappedEmployee.setManager(manager);

        return mappedEmployee;
    }

    public static EmployeeWithSubordinatesDto toDto(Employee employee) {
        EmployeeWithSubordinatesDto mappedEmployeeWithSubordinatesDto = UserMapper.toDto(employee, new EmployeeWithSubordinatesDto());
        mappedEmployeeWithSubordinatesDto.setPosition(employee.getPosition());
        mappedEmployeeWithSubordinatesDto.setSubordinates(new ArrayList<>());
        return mappedEmployeeWithSubordinatesDto;
    };
}
