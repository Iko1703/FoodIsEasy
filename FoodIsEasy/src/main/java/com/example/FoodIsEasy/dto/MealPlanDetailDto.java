package com.example.FoodIsEasy.dto;

import com.example.FoodIsEasy.model.enums.MealPlanScope;

import java.time.LocalDate;
import java.util.List;

public record MealPlanDetailDto(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        MealPlanScope scope,
        Long groupId,
        String groupName,
        List<MealPlanEntryDto> entries
) {
}
