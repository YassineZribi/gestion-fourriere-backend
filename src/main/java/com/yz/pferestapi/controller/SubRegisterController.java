package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.SubRegisterCriteriaRequest;
import com.yz.pferestapi.dto.UpsertSubRegisterDto;
import com.yz.pferestapi.entity.SubRegister;
import com.yz.pferestapi.service.SubRegisterService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/sub-registers")
@RestController
public class SubRegisterController {
    private  final SubRegisterService subRegisterService;

    public SubRegisterController(SubRegisterService subRegisterService) {
        this.subRegisterService = subRegisterService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<SubRegister>> getAllSubRegistersByName(@RequestParam(required = false, defaultValue = "") String name) {
        List<SubRegister> subRegisters = subRegisterService.findSubRegistersByName(name);
        return ResponseEntity.ok(subRegisters);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<SubRegister> getSubRegister(@PathVariable Long id) {
        System.out.println("subRegisterId = " + id);
        SubRegister subRegister = subRegisterService.getSubRegister(id);
        return ResponseEntity.ok(subRegister);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<SubRegister>> findSubRegistersByCriteria(SubRegisterCriteriaRequest subRegisterCriteria) {
        System.out.println("subRegisterCriteria = " + subRegisterCriteria);
        Page<SubRegister> subRegisters = subRegisterService.findSubRegistersByCriteria(subRegisterCriteria);
        return ResponseEntity.ok(subRegisters);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<SubRegister> createSubRegister(@Valid @RequestBody UpsertSubRegisterDto upsertSubRegisterDto) {
        SubRegister createdSubRegister = subRegisterService.createSubRegister(upsertSubRegisterDto);
        return new ResponseEntity<>(createdSubRegister, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<SubRegister> updateSubRegister(@Valid @RequestBody UpsertSubRegisterDto upsertSubRegisterDto, @PathVariable Long id) {
        SubRegister updatedSubRegister = subRegisterService.updateSubRegister(upsertSubRegisterDto, id);
        return ResponseEntity.ok(updatedSubRegister);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteSubRegister(@PathVariable Long id) {
        subRegisterService.deleteSubRegister(id);
        return ResponseEntity.noContent().build();
    }

}
