package com.yz.pferestapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final ResourceLoader resourceLoader;

    public Resource getTemplateFile(String filename) {
        return resourceLoader.getResource("classpath:templates/" + filename);
    }
}
