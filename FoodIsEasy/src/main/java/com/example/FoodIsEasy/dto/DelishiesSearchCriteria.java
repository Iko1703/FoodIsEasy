package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.MealRole;

public record DelishiesSearchCriteria(
        String q,
        Long cuisineId,
        Long categoryId,
        MealRole mealRole,
        Integer maxKcal,
        Boolean favoritesOnly
) {
}
