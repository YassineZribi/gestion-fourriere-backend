package com.yz.pferestapi.mapper;

import com.yz.pferestapi.dto.EmployeeDto;
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

    public static EmployeeWithSubordinatesDto toEmployeeWithSubordinatesDto(Employee employee) {
        EmployeeWithSubordinatesDto mappedEmployeeWithSubordinatesDto = UserMapper.toDto(employee, new EmployeeWithSubordinatesDto());
        mappedEmployeeWithSubordinatesDto.setPosition(employee.getPosition());
        mappedEmployeeWithSubordinatesDto.setSubordinates(new ArrayList<>());
        return mappedEmployeeWithSubordinatesDto;
    };

    public static EmployeeDto toDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDto mappedEmployeeDto = UserMapper.toDto(employee, new EmployeeDto());

        mappedEmployeeDto.setPosition(employee.getPosition());

        // Recursively map manager if it exists
        if (employee.getManager() != null) {
            mappedEmployeeDto.setManager(toDto(employee.getManager()));
        }

        return mappedEmployeeDto;
    }
}
