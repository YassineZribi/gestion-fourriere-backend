package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.EmployeeWithSubordinatesDto;
import com.yz.pferestapi.dto.SaveInstitutionDto;
import com.yz.pferestapi.entity.Institution;
import com.yz.pferestapi.service.InstitutionService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/institution")
@RestController
public class InstitutionController {
    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Institution> getInstitution() {
        Institution institution = institutionService.getInstitution();
        return ResponseEntity.ok(institution);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Institution> saveInstitution(@RequestPart("data") @Valid SaveInstitutionDto saveInstitutionDto,
                                                     @RequestPart(value = "media", required = false) MultipartFile logoFile) throws IOException {
        Institution institution = institutionService.saveInstitution(saveInstitutionDto, logoFile);
        return ResponseEntity.ok(institution);
    }

    @GetMapping("/organizational-chart")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<EmployeeWithSubordinatesDto> getOrganizationalChart() {
        EmployeeWithSubordinatesDto chiefExecutiveWithRecursiveSubordinates = institutionService.getOrganizationalChart();
        return ResponseEntity.ok(chiefExecutiveWithRecursiveSubordinates);
    }
}
