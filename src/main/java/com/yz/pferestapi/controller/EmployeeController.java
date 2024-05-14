package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.EmployeeCriteriaRequest;
import com.yz.pferestapi.dto.EmployeeDto;
import com.yz.pferestapi.dto.UpsertEmployeeDto;
import com.yz.pferestapi.entity.Employee;
import com.yz.pferestapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/employees")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

//    @GetMapping
//    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
//    public ResponseEntity<List<Employee>> getAllEmployees() {
//        List<Employee> employees = employeeService.getAllEmployees();
//        return ResponseEntity.ok(employees);
//    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<Employee>> getAllEmployeesByFullName(@RequestParam(required = false, defaultValue = "") String fullName) {
        System.out.println("inside getAllEmployeesByFullName before service call");
        List<Employee> employees = employeeService.findEmployeesByFullName(fullName);
        System.out.println("inside getAllEmployeesByFullName after service call");
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
        System.out.println("employeeId = " + id);
        Employee employee = employeeService.getEmployee(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/{id}/subordinates")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<EmployeeDto> getEmployeeWithRecursiveSubordinates(@PathVariable Long id) {
        EmployeeDto employees = employeeService.getEmployeeWithRecursiveSubordinates(id);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/chief-executive")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<EmployeeDto> getChiefExecutiveWithRecursiveSubordinates() {
        EmployeeDto employees = employeeService.getChiefExecutiveWithRecursiveSubordinates();
        return ResponseEntity.ok(employees);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Employee>> findEmployeesByCriteria(EmployeeCriteriaRequest employeeCriteria) {
        System.out.println("employeeCriteria = " + employeeCriteria);
        Page<Employee> employees = employeeService.findEmployeesByCriteria(employeeCriteria);
        return ResponseEntity.ok(employees);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Employee> createEmployee(@RequestPart("data") @Valid UpsertEmployeeDto upsertEmployeeDto,
                                                    @RequestPart(value = "media", required = false) MultipartFile photoFile) throws IOException {
        Employee employee = employeeService.createEmployee(upsertEmployeeDto, photoFile);
        return ResponseEntity.ok(employee);
    }

    @PatchMapping(value="/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@RequestPart("data") @Valid UpsertEmployeeDto upsertEmployeeDto,
                                                   @RequestPart(value = "media", required = false) MultipartFile photoFile,
                                                   @PathVariable("id") Long employeeId) throws IOException {
        System.out.println("employeeId = " + employeeId);
        Employee employee = employeeService.updateEmployee(upsertEmployeeDto, employeeId, photoFile);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long employeeId) throws IOException {
        System.out.println("employeeId = " + employeeId);
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }
}
