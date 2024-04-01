package com.yz.pferestapi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductsController {
    @GetMapping
    public List<String> getLoginInfo() {
        return List.of("Product1", "Product2", "Product3");
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('SCOPE_MANAGER')")
    public List<String> getLoginInfoOnlyForManagers() {
        return List.of("Product1-manager", "Product2-manager", "Product3-manager");
    }
}
