package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.model.entity.Cuisine;
import com.example.FoodIsEasy.model.entity.repository.CuisineRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cuisines")
public class CuisineController {

    private final CuisineRepo cuisineRepo;

    public CuisineController(CuisineRepo cuisineRepo) {
        this.cuisineRepo = cuisineRepo;
    }

    @GetMapping
    public ResponseEntity<List<Cuisine>> list() {
        return ResponseEntity.ok(cuisineRepo.findAll());
    }
}
