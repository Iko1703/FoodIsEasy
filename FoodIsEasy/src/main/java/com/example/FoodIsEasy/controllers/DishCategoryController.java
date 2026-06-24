package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.model.entity.DishCategory;
import com.example.FoodIsEasy.model.entity.repository.DishCategoryRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dish-categories")
public class DishCategoryController {

    private final DishCategoryRepo dishCategoryRepo;

    public DishCategoryController(DishCategoryRepo dishCategoryRepo) {
        this.dishCategoryRepo = dishCategoryRepo;
    }

    @GetMapping
    public ResponseEntity<List<DishCategory>> list() {
        return ResponseEntity.ok(dishCategoryRepo.findAll());
    }
}
