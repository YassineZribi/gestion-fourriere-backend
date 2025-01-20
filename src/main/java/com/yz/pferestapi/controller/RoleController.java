package com.yz.pferestapi.controller;

import com.yz.pferestapi.dto.RoleDto;
import com.yz.pferestapi.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/roles")
@RestController
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/non-admin")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<RoleDto>> getNonAdminRoles() {
        List<RoleDto> roles = roleService.getNonAdminRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}
