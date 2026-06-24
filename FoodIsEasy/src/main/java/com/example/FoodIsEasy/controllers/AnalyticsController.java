package com.example.FoodIsEasy.controllers;

import com.example.FoodIsEasy.dto.NutritionAnalyticsDto;
import com.example.FoodIsEasy.model.entity.User;
import com.example.FoodIsEasy.service.CurrentUserService;
import com.example.FoodIsEasy.service.NutritionAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/analytics")
public class AnalyticsController {

    private final CurrentUserService currentUserService;
    private final NutritionAnalyticsService analyticsService;

    public AnalyticsController(CurrentUserService currentUserService, NutritionAnalyticsService analyticsService) {
        this.currentUserService = currentUserService;
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<NutritionAnalyticsDto> analyze(@RequestParam(defaultValue = "30") int days) {
        User user = currentUserService.requireCurrentUser();
        return ResponseEntity.ok(analyticsService.analyze(user.getId(), days));
    }
}
