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

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<String> getLoginInfoOnlyForAdmins() {
        return List.of("Product1-Admin", "Product2-Admin", "Product3-Admin");
    }
}
