package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.LogMealRequest;
import com.example.FoodIsEasy.dto.MealHistoryDto;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.MealHistoryService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/me/meal-history")
public class MealHistoryController {

    private final CurrentUserService currentUserService;
    private final MealHistoryService mealHistoryService;

    public MealHistoryController(CurrentUserService currentUserService, MealHistoryService mealHistoryService) {
        this.currentUserService = currentUserService;
        this.mealHistoryService = mealHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<MealHistoryDto>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(mealHistoryService.getHistory(user.getId(), from, to));
    }

    @PostMapping
    public ResponseEntity<MealHistoryDto> log(@Valid @RequestBody LogMealRequest request) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(mealHistoryService.logMeal(user.getId(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = currentUserService.requireCurrentUser();
        mealHistoryService.deleteEntry(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
