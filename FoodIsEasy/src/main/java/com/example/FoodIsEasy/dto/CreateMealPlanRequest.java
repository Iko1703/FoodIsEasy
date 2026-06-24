package com.example.FoodIsEasy.dto;

import java.time.LocalDate;

public record CreateMealPlanRequest(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Long groupId
) {
}
