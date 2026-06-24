package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.*;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me/meal-plans")
public class MealPlanController {

    private final CurrentUserService currentUserService;
    private final MealPlanService mealPlanService;

    public MealPlanController(CurrentUserService currentUserService, MealPlanService mealPlanService) {
        this.currentUserService = currentUserService;
        this.mealPlanService = mealPlanService;
    }

    @GetMapping
    public ResponseEntity<List<MealPlanSummaryDto>> listPersonal() {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(mealPlanService.listPersonalPlans(user.getId()));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<MealPlanSummaryDto>> listGroup(@PathVariable Long groupId) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(mealPlanService.listGroupPlans(user.getId(), groupId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealPlanDetailDto> get(@PathVariable Long id) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(mealPlanService.getPlan(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<MealPlanDetailDto> create(@Valid @RequestBody CreateMealPlanRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(mealPlanService.createPlan(user.getId(), request));
    }

    @PutMapping("/{id}/entries")
    public ResponseEntity<MealPlanDetailDto> setEntry(
            @PathVariable Long id,
            @Valid @RequestBody SetMealPlanEntryRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(mealPlanService.setEntry(user.getId(), id, request));
    }

    @DeleteMapping("/{id}/entries/{entryId}")
    public ResponseEntity<Void> removeEntry(@PathVariable Long id, @PathVariable Long entryId) {
        User user = currentUserService.requireCurrentUser();
        mealPlanService.removeEntry(user.getId(), id, entryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/generate")
    public ResponseEntity<MealPlanDetailDto> generate(@PathVariable Long id) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(mealPlanService.generatePlan(user.getId(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = currentUserService.requireCurrentUser();
        mealPlanService.deletePlan(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
