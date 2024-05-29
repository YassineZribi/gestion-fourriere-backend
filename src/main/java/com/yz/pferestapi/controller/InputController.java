package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.InputCriteriaRequest;
import com.yz.pferestapi.dto.UpsertInputDto;
import com.yz.pferestapi.entity.Input;
import com.yz.pferestapi.service.InputService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/operations/inputs")
@RestController
public class InputController {
    private final InputService inputService;

    public InputController(InputService inputService) {
        this.inputService = inputService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Input> getInput(@PathVariable Long id) {
        Input input = inputService.getInput(id);
        return ResponseEntity.ok(input);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Input>> findInputsByCriteria(InputCriteriaRequest inputCriteria) {
        System.out.println("inputCriteria = " + inputCriteria);
        Page<Input> inputs = inputService.findInputsByCriteria(inputCriteria);
        return ResponseEntity.ok(inputs);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Input> createInput(@Validated @RequestBody UpsertInputDto upsertInputDto) {
        Input createdInput = inputService.createInput(upsertInputDto);
        return new ResponseEntity<>(createdInput, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Input> updateInput(@Validated @RequestBody UpsertInputDto upsertInputDto, @PathVariable Long id) {
        Input updatedInput = inputService.updateInput(upsertInputDto, id);
        return ResponseEntity.ok(updatedInput);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteInput(@PathVariable Long id) {
        inputService.deleteInput(id);
        return ResponseEntity.noContent().build();
    }
}
