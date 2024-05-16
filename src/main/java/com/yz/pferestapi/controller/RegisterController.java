package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.RegisterCriteriaRequest;
import com.yz.pferestapi.dto.UpsertRegisterDto;
import com.yz.pferestapi.entity.Register;
import com.yz.pferestapi.service.RegisterService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/registers")
@RestController
public class RegisterController {
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<Register>> getAllRegistersByName(@RequestParam(required = false, defaultValue = "") String name) {
        List<Register> registers = registerService.findRegistersByName(name);
        return ResponseEntity.ok(registers);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Register> getRegister(@PathVariable Long id) {
        System.out.println("registerId = " + id);
        Register register = registerService.getRegister(id);
        return ResponseEntity.ok(register);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Register>> findRegistersByCriteria(RegisterCriteriaRequest registerCriteria) {
        System.out.println("registerCriteria = " + registerCriteria);
        Page<Register> registers = registerService.findRegistersByCriteria(registerCriteria);
        return ResponseEntity.ok(registers);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Register> createRegister(@Valid @RequestBody UpsertRegisterDto upsertRegisterDto) {
        Register createdRegister = registerService.createRegister(upsertRegisterDto);
        return new ResponseEntity<>(createdRegister, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Register> updateRegister(@Valid @RequestBody UpsertRegisterDto upsertRegisterDto, @PathVariable Long id) {
        Register updatedRegister = registerService.updateRegister(upsertRegisterDto, id);
        return ResponseEntity.ok(updatedRegister);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteRegister(@PathVariable Long id) {
        registerService.deleteRegister(id);
        return ResponseEntity.noContent().build();
    }


}
