package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.IndividualCriteriaRequest;
import com.yz.pferestapi.dto.UpsertIndividualDto;
import com.yz.pferestapi.entity.Individual;
import com.yz.pferestapi.service.IndividualService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners/individuals")
public class IndividualController {
    private final IndividualService individualService;

    public IndividualController(IndividualService individualService) {
        this.individualService = individualService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Individual> getIndividual(@PathVariable Long id) {
        Individual individual = individualService.getIndividual(id);
        return ResponseEntity.ok(individual);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Individual>> findIndividualsByCriteria(IndividualCriteriaRequest individualCriteria) {
        System.out.println("individualCriteria = " + individualCriteria);
        Page<Individual> individuals = individualService.findIndividualsByCriteria(individualCriteria);
        return ResponseEntity.ok(individuals);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Individual> createIndividual(@Valid @RequestBody UpsertIndividualDto upsertIndividualDto) {
        Individual createdIndividual = individualService.createIndividual(upsertIndividualDto);
        return new ResponseEntity<>(createdIndividual, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Individual> updateIndividual(@Valid @RequestBody UpsertIndividualDto upsertIndividualDto, @PathVariable Long id) {
        Individual updatedIndividual = individualService.updateIndividual(upsertIndividualDto, id);
        return ResponseEntity.ok(updatedIndividual);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteIndividual(@PathVariable Long id) {
        individualService.deleteIndividual(id);
        return ResponseEntity.noContent().build();
    }
}
