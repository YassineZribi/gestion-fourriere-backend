package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.InputCriteriaRequest;
import com.yz.pferestapi.dto.UpdateInputOwnerDto;
import com.yz.pferestapi.dto.UpsertInputDto;
import com.yz.pferestapi.entity.Input;
import com.yz.pferestapi.service.InputService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Input> createInput(@Validated @ModelAttribute UpsertInputDto upsertInputDto) throws IOException {
        System.out.println("upsertInputDto.getSourceId() = " + upsertInputDto.getSourceId());
        Input createdInput = inputService.createInput(upsertInputDto);
        return new ResponseEntity<>(createdInput, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Input> updateInput(@Validated @ModelAttribute UpsertInputDto upsertInputDto, @PathVariable Long id) throws IOException {
        System.out.println("upsertInputDto.getSourceId() = " + upsertInputDto.getSourceId());
        Input updatedInput = inputService.updateInput(upsertInputDto, id);
        return ResponseEntity.ok(updatedInput);
    }

    @PatchMapping("/{id}/owner")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Input> updateInputOwner(@Validated @RequestBody UpdateInputOwnerDto updateInputOwnerDto, @PathVariable("id") Long inputId) throws IOException {
        Input updatedInput = inputService.updateInputOwner(updateInputOwnerDto, inputId);
        return ResponseEntity.ok(updatedInput);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteInput(@PathVariable Long id) throws IOException {
        inputService.deleteInput(id);
        return ResponseEntity.noContent().build();
    }
}
