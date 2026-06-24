package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.MealType;

import java.time.LocalDate;

public record SetMealPlanEntryRequest(
        LocalDate planDate,
        MealType mealType,
        Long delishiesId
) {
}
