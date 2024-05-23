package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.CompanyCriteriaRequest;
import com.yz.pferestapi.dto.UpsertCompanyDto;
import com.yz.pferestapi.entity.Company;
import com.yz.pferestapi.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Company> getCompany(@PathVariable Long id) {
        Company company = companyService.getCompany(id);
        return ResponseEntity.ok(company);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Company>> findCompaniesByCriteria(CompanyCriteriaRequest companyCriteria) {
        System.out.println("companyCriteria = " + companyCriteria);
        Page<Company> companies = companyService.findCompaniesByCriteria(companyCriteria);
        return ResponseEntity.ok(companies);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody UpsertCompanyDto upsertCompanyDto) {
        Company createdCompany = companyService.createCompany(upsertCompanyDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody UpsertCompanyDto upsertCompanyDto, @PathVariable Long id) {
        Company updatedCompany = companyService.updateCompany(upsertCompanyDto, id);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
