package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.MealRole;
import com.example.FoodIsEasy.model.enums.MealType;

import java.time.LocalDate;

public record MealPlanEntryDto(
        Long id,
        LocalDate planDate,
        MealType mealType,
        Long delishiesId,
        String delishiesTitle,
        String imageUrl,
        MealRole mealRole
) {
}
