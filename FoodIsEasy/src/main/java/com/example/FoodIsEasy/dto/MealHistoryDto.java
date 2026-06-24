package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.MealType;

import java.time.LocalDateTime;

public record MealHistoryDto(
        Long id,
        Long delishiesId,
        String delishiesTitle,
        String imageUrl,
        MealType mealType,
        LocalDateTime eatenAt
) {
}
