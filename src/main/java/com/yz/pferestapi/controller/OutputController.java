package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.OutputCriteriaRequest;
import com.yz.pferestapi.dto.UpsertOutputDto;
import com.yz.pferestapi.entity.Output;
import com.yz.pferestapi.service.OutputService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequestMapping("/operations/outputs")
@RestController
public class OutputController {
    private final OutputService outputService;

    public OutputController(OutputService outputService) {
        this.outputService = outputService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Output> getOutput(@PathVariable Long id) {
        Output output = outputService.getOutput(id);
        return ResponseEntity.ok(output);
    }

    // In Spring Boot, when you don't specify @RequestParam or @RequestBody in a method parameter
    // of a controller, Spring Boot will treat the data as part of the request parameters by default.
    // http://example.com/api/users?param1=value1&param2=value2
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<Page<Output>> findOutputsByCriteria(OutputCriteriaRequest outputCriteria) {
        System.out.println("outputCriteria = " + outputCriteria);
        Page<Output> outputs = outputService.findOutputsByCriteria(outputCriteria);
        return ResponseEntity.ok(outputs);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Output> createOutput(@Validated @RequestBody UpsertOutputDto upsertOutputDto) {
        Output createdOutput = outputService.createOutput(upsertOutputDto);
        return new ResponseEntity<>(createdOutput, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<?> deleteOutput(@PathVariable Long id) {
        outputService.deleteOutput(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_MANAGER')")
    public ResponseEntity<byte[]> generateOutputsReport(OutputCriteriaRequest outputCriteria) throws JRException, IOException {
        ByteArrayOutputStream reportStream = outputService.generateOutputsReport(outputCriteria);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        return new ResponseEntity<>(reportStream.toByteArray(), httpHeaders, HttpStatus.OK);
    }
}
