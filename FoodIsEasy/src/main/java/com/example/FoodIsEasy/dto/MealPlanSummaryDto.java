package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.MealPlanScope;

import java.time.LocalDate;

public record MealPlanSummaryDto(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        MealPlanScope scope,
        Long groupId,
        String groupName,
        int entriesCount
) {
}
