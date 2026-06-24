package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.MealType;

import java.time.LocalDateTime;

public record LogMealRequest(
        Long delishiesId,
        MealType mealType,
        LocalDateTime eatenAt
) {
}
