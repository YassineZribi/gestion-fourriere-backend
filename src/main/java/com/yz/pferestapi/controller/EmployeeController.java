package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.EmployeeCriteriaRequest;
import com.yz.pferestapi.dto.EmployeeDto;
import com.yz.pferestapi.dto.UpsertEmployeeDto;
import com.yz.pferestapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping("/users/employees")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<EmployeeDto>> getAllEmployeesByFullName(@RequestParam(required = false, defaultValue = "") String fullName) {
        List<EmployeeDto> employees = employeeService.findEmployeesByFullName(fullName);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        System.out.println("employeeId = " + id);
        EmployeeDto employee = employeeService.getEmployee(id);
        return ResponseEntity.ok(employee);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<EmployeeDto>> findEmployeesByCriteria(EmployeeCriteriaRequest employeeCriteria) {
        System.out.println("employeeCriteria = " + employeeCriteria);
        Page<EmployeeDto> employees = employeeService.findEmployeesByCriteria(employeeCriteria);
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<EmployeeDto> addEmployee(@Valid @RequestBody UpsertEmployeeDto upsertEmployeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(upsertEmployeeDto);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<EmployeeDto> updateEmployee(@Valid @RequestBody UpsertEmployeeDto upsertEmployeeDto, @PathVariable Long id) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(upsertEmployeeDto, id);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) throws IOException {
        System.out.println("id = " + id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
