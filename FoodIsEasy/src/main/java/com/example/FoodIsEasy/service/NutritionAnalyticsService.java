package com.example.FoodIsEasy.service;

import com.example.FoodIsEasy.dto.NutritionAnalyticsDto;

public interface NutritionAnalyticsService {
    NutritionAnalyticsDto analyze(Long userId, int days);
}
