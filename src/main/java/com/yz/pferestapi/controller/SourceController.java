package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.SourceCriteriaRequest;
import com.yz.pferestapi.dto.UpsertSourceDto;
import com.yz.pferestapi.entity.Source;
import com.yz.pferestapi.service.SourceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/sources")
@RestController
public class SourceController {
    private final SourceService sourceService;

    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<List<Source>> getAllISourcesByName(@RequestParam(required = false, defaultValue = "") String name) {
        List<Source> sources = sourceService.findSourcesByName(name);
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Source> getSource(@PathVariable Long id) {
        Source source = sourceService.getSource(id);
        return ResponseEntity.ok(source);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Source>> findSourcesByCriteria(SourceCriteriaRequest sourceCriteria) {
        System.out.println("sourceCriteria = " + sourceCriteria);
        Page<Source> sources = sourceService.findSourcesByCriteria(sourceCriteria);
        return ResponseEntity.ok(sources);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Source> createSource(@Valid @RequestBody UpsertSourceDto upsertSourceDto) {
        Source createdSource = sourceService.createSource(upsertSourceDto);
        return new ResponseEntity<>(createdSource, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Source> updateSource(@Valid @RequestBody UpsertSourceDto upsertSourceDto, @PathVariable Long id) {
        Source updatedSource = sourceService.updateSource(upsertSourceDto, id);
        return ResponseEntity.ok(updatedSource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteSource(@PathVariable Long id) {
        sourceService.deleteSource(id);
        return ResponseEntity.noContent().build();
    }
}
