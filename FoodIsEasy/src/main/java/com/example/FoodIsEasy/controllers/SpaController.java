package com.example.FoodIsEasy.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/")
public class SpaController {

    @GetMapping(value = {"", "{path:[^\\.]*}"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> index() throws IOException {
        ClassPathResource index = new ClassPathResource("static/index.html");
        if (index.exists()) {
            return ResponseEntity.ok(index.getContentAsByteArray());
        }
        return ResponseEntity.notFound().build();
    }
}



